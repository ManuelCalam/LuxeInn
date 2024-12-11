package com.example.luxeinn

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class HotelDescription : AppCompatActivity(), OnMapReadyCallback  {
    private lateinit var mMap: GoogleMap
    private val database = FirebaseDatabase.getInstance()
    private val hotelRef = database.getReference("Hotels")
    private lateinit var reservar: Button
    private var pendingLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel_description)


        reservar = findViewById(R.id.reservarBtn)
        reservar.setOnClickListener { agregarReserva() }

        val hotelName = intent.getStringExtra("hotel_name") ?: ""
        if (hotelName.isNotEmpty()) {

            searchHotel(hotelName)
        } else {
            Toast.makeText(this, "Hotel name not provided", Toast.LENGTH_SHORT).show()
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = resources.getColor(R.color.blue, theme)
        }
    }

    fun agregarReserva(){
        Toast.makeText(this, "Reservación registrada", Toast.LENGTH_SHORT).show()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val initialLocation = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(initialLocation).title("Initial Marker"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 10f))

        // Check for any pending map update
        pendingLocation?.let {
            updateMapLocation(it.latitude, it.longitude)
            pendingLocation = null // Clear after use
        }
    }


    private fun populateHotelDescription(hotelData: Map<String, Any>) {
        // Set hotel name and info
        findViewById<TextView>(R.id.hotelName).text = hotelData["name"] as? String ?: ""
        findViewById<TextView>(R.id.textView6).text = hotelData["location"] as? String ?: ""
        findViewById<TextView>(R.id.hotelInfo).text = hotelData["info"] as? String ?: ""

        // Set the hotel image dynamically based on the imageUrl
        val imageView = findViewById<ImageView>(R.id.imageView)
        val imageUrl = hotelData["imageUrl"] as? String
        if (imageUrl != null) {
            val resourceId = resources.getIdentifier(imageUrl, "drawable", packageName)
            if (resourceId != 0) {
                imageView.setImageResource(resourceId)
            } else {
                imageView.setImageResource(R.drawable.logo)
            }
        } else {
            imageView.setImageResource(R.drawable.logo)
        }

        // Set room types in spinner
        val roomSpinner = findViewById<Spinner>(R.id.roomSpn)
        val roomOptions = mutableListOf<String>()
        val roomPrices = mutableMapOf<String, String>()

        val rooms = hotelData["rooms"] as? Map<String, Map<String, Any>> ?: emptyMap()
        rooms.forEach { (roomType, details) ->
            val price = details["price"]?.toString()
            if (!price.isNullOrEmpty()) {
                roomOptions.add(roomType.capitalize())
                roomPrices[roomType.capitalize()] = price
            }
        }

        if (roomOptions.isEmpty()) {
            Toast.makeText(this, "No available rooms with prices.", Toast.LENGTH_SHORT).show()
            roomSpinner.visibility = View.GONE // Hide spinner if no valid options
            findViewById<TextView>(R.id.precioHotel).text = "" // Clear price field
        } else {
            roomSpinner.visibility = View.VISIBLE
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roomOptions)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            roomSpinner.adapter = adapter

            // Set price based on room selection
            val priceTextView = findViewById<TextView>(R.id.precioHotel)
            roomSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val selectedRoom = roomOptions[position]
                    priceTextView.text = "Precio: $${roomPrices[selectedRoom]}"
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    priceTextView.text = ""
                }
            }
        }

        // Set hotel services
        val services = hotelData["services"] as? List<String> ?: emptyList()
        val servicesTextView = findViewById<TextView>(R.id.hotelServices)
        servicesTextView.text = "Servicios: ${services.joinToString(", ")}"

        // Update map location
        val latitude = hotelData["latitude"] as? Double ?: 0.0
        val longitude = hotelData["longitude"] as? Double ?: 0.0
        updateMapLocation(latitude, longitude)
    }




    private fun updateMapLocation(latitude: Double, longitude: Double) {
        if (::mMap.isInitialized) {
            val hotelLocation = LatLng(latitude, longitude)
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(hotelLocation).title("Hotel Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hotelLocation, 15f))
        } else {
            pendingLocation = LatLng(latitude, longitude) // Queue the update
        }
    }







    private fun searchHotel(hotelName: String) {
        if (hotelName.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa el nombre del hotel a buscar", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("SearchHotel", "Nombre buscado: $hotelName")

        hotelRef.orderByChild("name").equalTo(hotelName).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val hotelSnapshot = snapshot.children.first()
                val hotelData = hotelSnapshot.value as? Map<String, Any>
                if (hotelData != null) {
                    populateHotelDescription(hotelData)
                }
            } else {
                Toast.makeText(this, "Hotel no encontrado", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al buscar: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

}