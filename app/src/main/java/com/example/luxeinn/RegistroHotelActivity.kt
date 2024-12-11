package com.example.luxeinn

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.database.FirebaseDatabase
import java.io.IOException

class RegistroHotelActivity : AppCompatActivity() {

    private lateinit var etHotelName: EditText
    private lateinit var etHotelLocation: EditText
    private lateinit var etHotelInfo: EditText
    private lateinit var etLatitude: EditText
    private lateinit var etLongitude: EditText
    private lateinit var etImageName: EditText
    private lateinit var cbSingleRoom: CheckBox
    private lateinit var cbDoubleRoom: CheckBox
    private lateinit var cbTripleRoom: CheckBox
    private lateinit var etPriceSingle: EditText
    private lateinit var etPriceDouble: EditText
    private lateinit var etPriceTriple: EditText
    private lateinit var cbWifi: CheckBox
    private lateinit var cbPool: CheckBox
    private lateinit var cbBreakfast: CheckBox
    private lateinit var cbBalcon: CheckBox
    private lateinit var switchAvailability: Switch
    private lateinit var btnSave: Button
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button
    private lateinit var btnBuscar: Button

    private val firestore = FirebaseFirestore.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val hotelRef = database.getReference("Hotels")
    private var currentHotelId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_hotel)

        etHotelName = findViewById(R.id.et_hotel_name)
        etHotelLocation = findViewById(R.id.et_hotel_location)
        etHotelInfo = findViewById(R.id.et_hotel_info)
        etLatitude = findViewById(R.id.et_latitude)
        etLongitude = findViewById(R.id.et_longitude)
        etImageName = findViewById(R.id.et_image_name)
        cbSingleRoom = findViewById(R.id.cb_single_room)
        cbDoubleRoom = findViewById(R.id.cb_double_room)
        cbTripleRoom = findViewById(R.id.cb_triple_room)
        etPriceSingle = findViewById(R.id.et_price_single)
        etPriceDouble = findViewById(R.id.et_price_double)
        etPriceTriple = findViewById(R.id.et_price_triple)
        cbWifi = findViewById(R.id.cb_wifi)
        cbPool = findViewById(R.id.cb_pool)
        cbBreakfast = findViewById(R.id.cb_breakfast)
        cbBalcon = findViewById(R.id.cb_balcon)
        switchAvailability = findViewById(R.id.switch_availability)
        btnSave = findViewById(R.id.btn_save)
        btnEdit = findViewById(R.id.btn_edit)
        btnDelete = findViewById(R.id.btn_delete)
        btnBuscar = findViewById(R.id.btn_buscar )

        btnSave.setOnClickListener { saveHotel() }
        btnEdit.setOnClickListener { editHotel() }
        btnDelete.setOnClickListener { deleteHotel(  ) }
        btnBuscar.setOnClickListener{ searchHotel() }

    }

    private fun saveHotel() {

        val hotelName = etHotelName.text.toString()
        val hotelLocation = etHotelLocation.text.toString()
        val hotelInfo = etHotelInfo.text.toString()
        val latitude = etLatitude.text.toString().toDoubleOrNull() ?: 0.0
        val longitude = etLongitude.text.toString().toDoubleOrNull() ?: 0.0
        val priceSingle = etPriceSingle.text.toString().toDoubleOrNull()
        val priceDouble = etPriceDouble.text.toString().toDoubleOrNull()
        val priceTriple = etPriceTriple.text.toString().toDoubleOrNull()
        val ImageName  = etImageName.text.toString()

        if (hotelName.isEmpty() || hotelLocation.isEmpty() || hotelInfo.isEmpty() || latitude == null || longitude == null
            || ImageName.isEmpty()) {
            Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener servicios seleccionados
        val services = mutableListOf<String>()
        if (cbWifi.isChecked) services.add("WiFi")
        if (cbPool.isChecked) services.add("Piscina")
        if (cbBreakfast.isChecked) services.add("Desayuno")
        if (cbBalcon.isChecked) services.add("Balcon")

        // Estructura de los cuartos
        val rooms = hashMapOf(
            "single" to hashMapOf(
                "available" to cbSingleRoom.isChecked,
                "price" to priceSingle
            ),
            "double" to hashMapOf(
                "available" to cbDoubleRoom.isChecked,
                "price" to priceDouble
            ),
            "triple" to hashMapOf(
                "available" to cbTripleRoom.isChecked,
                "price" to priceTriple
            )
        )

        // Datos del hotel en la estructura de Firebase
        val hotelData = hashMapOf(
            "name" to hotelName,
            "location" to hotelLocation,
            "info" to hotelInfo,
            "latitude" to latitude,
            "longitude" to longitude,
            "imageUrl" to ImageName,
            "rooms" to rooms,
            "services" to services,
            "available" to switchAvailability.isChecked
        )

        // Aquí deberías guardar `hotelData` en Firebase
        val database = FirebaseDatabase.getInstance()
        val hotelRef = database.getReference("Hotels")
        val hotelId = hotelRef.push().key // Genera un ID único para el hotel
        hotelRef.child(hotelId ?: "default").setValue(hotelData).addOnCompleteListener {
            if (it.isSuccessful) {
                // Código para manejar éxito
                Toast.makeText(this, "Hotel guardado exitosamente", Toast.LENGTH_SHORT).show()
            } else {
                // Código para manejar error
                Toast.makeText(this, "Error al guardar el hotel", Toast.LENGTH_SHORT).show()
            }
        }

        firestore.collection("Hotels").add(hotelData)
            .addOnSuccessListener {
                Toast.makeText(this, "Hotel guardado exitosamente", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar hotel: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun searchHotel() {
        val hotelName = etHotelName.text.toString()
        if (hotelName.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa el nombre del hotel a buscar", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("SearchHotel", "Nombre buscado: $hotelName")


        hotelRef.orderByChild("name").equalTo(hotelName).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val hotelSnapshot = snapshot.children.first()
                currentHotelId = hotelSnapshot.key // Guarda el ID del hotel encontrado

                val hotelData = hotelSnapshot.value as? Map<String, Any>
                if (hotelData != null) {
                    // Llena los campos con la información del hotel
                    etHotelLocation.setText(hotelData["location"] as? String ?: "")
                    etHotelInfo.setText(hotelData["info"] as? String ?: "")
                    etLatitude.setText(hotelData["latitude"].toString())
                    //etLatitude.setText(( hotelData["latitude"]as? Double)?.toString() ?: "0.0")
                    etLongitude.setText(hotelData["longitude"].toString())
                    etImageName.setText(hotelData["imageUrl"] as? String ?: "")

                    val rooms = hotelData["rooms"] as? Map<String, Map<String, Any>>
                    cbSingleRoom.isChecked = rooms?.get("single")?.get("available") as? Boolean ?: false
                    cbDoubleRoom.isChecked = rooms?.get("double")?.get("available") as? Boolean ?: false
                    cbTripleRoom.isChecked = rooms?.get("triple")?.get("available") as? Boolean ?: false
                    val priceSingle = (rooms?.get("single")?.get("price").toString() )
                    val priceDouble = (rooms?.get("double")?.get("price").toString() )
                    val priceTriple = (rooms?.get("triple")?.get("price").toString() )

                    etPriceSingle.setText(priceSingle)
                    etPriceDouble.setText(priceDouble)
                    etPriceTriple.setText(priceTriple)

                    val services = hotelData["services"] as? List<String> ?: emptyList()
                    cbWifi.isChecked = "WiFi" in services
                    cbPool.isChecked = "Piscina" in services
                    cbBreakfast.isChecked = "Desayuno" in services
                    cbBalcon.isChecked = "Balcon" in services

                    switchAvailability.isChecked = hotelData["available"] as? Boolean ?: false

                    Toast.makeText(this, "Hotel encontrado", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Hotel no encontrado", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al buscar: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun editHotel() {
        if (currentHotelId == null) {
            Toast.makeText(this, "Primero busca un hotel para editarlo", Toast.LENGTH_SHORT).show()
            return
        }

        saveHotelData(currentHotelId!!)
    }

    private fun deleteHotel() {
        if (currentHotelId == null) {
            Toast.makeText(this, "Primero busca un hotel para eliminarlo", Toast.LENGTH_SHORT).show()
            return
        }

        hotelRef.child(currentHotelId!!).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Hotel eliminado", Toast.LENGTH_SHORT).show()
                clearFields()
                currentHotelId = null
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al eliminar: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveHotelData(hotelId: String) {
        // Obtener los valores del formulario
        val hotelName = etHotelName.text.toString()
        val hotelLocation = etHotelLocation.text.toString()
        val hotelInfo = etHotelInfo.text.toString()
        val latitude = etLatitude.text.toString().toDoubleOrNull() ?: 0.0
        val longitude = etLongitude.text.toString().toDoubleOrNull() ?: 0.0
        val priceSingle = etPriceSingle.text.toString().toDoubleOrNull()
        val priceDouble = etPriceDouble.text.toString().toDoubleOrNull()
        val priceTriple = etPriceTriple.text.toString().toDoubleOrNull()
        val imageName = etImageName.text.toString()

        // Validar campos requeridos
        if (hotelName.isEmpty() || hotelLocation.isEmpty() || hotelInfo.isEmpty() || imageName.isEmpty()) {
            Toast.makeText(this, "Por favor llena todos los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener servicios seleccionados
        val services = mutableListOf<String>()
        if (cbWifi.isChecked) services.add("WiFi")
        if (cbPool.isChecked) services.add("Piscina")
        if (cbBreakfast.isChecked) services.add("Desayuno")
        if (cbBalcon.isChecked) services.add("Balcón")

        // Estructura de los cuartos
        val rooms = hashMapOf(
            "single" to hashMapOf(
                "available" to cbSingleRoom.isChecked,
                "price" to priceSingle
            ),
            "double" to hashMapOf(
                "available" to cbDoubleRoom.isChecked,
                "price" to priceDouble
            ),
            "triple" to hashMapOf(
                "available" to cbTripleRoom.isChecked,
                "price" to priceTriple
            )
        )

        // Estructura de los datos del hotel
        val hotelData = hashMapOf(
            "name" to hotelName,
            "location" to hotelLocation,
            "info" to hotelInfo,
            "latitude" to latitude,
            "longitude" to longitude,
            "imageUrl" to imageName,
            "rooms" to rooms,
            "services" to services,
            "available" to switchAvailability.isChecked
        )

        // Guardar datos en Firebase Realtime Database
        val hotelRef = FirebaseDatabase.getInstance().getReference("Hotels").child(hotelId)
        hotelRef.setValue(hotelData)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Datos del hotel actualizados correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al actualizar los datos del hotel", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        etHotelName.text.clear()
        etHotelLocation.text.clear()
        etHotelInfo.text.clear()
        etLatitude.text.clear()
        etImageName.text.clear()
        etLongitude.text.clear()
        etPriceSingle.text.clear()
        etPriceDouble.text.clear()
        etPriceTriple.text.clear()
        cbSingleRoom.isChecked = false
        cbDoubleRoom.isChecked = false
        cbTripleRoom.isChecked = false
        cbWifi.isChecked = false
        cbPool.isChecked = false
        cbBreakfast.isChecked = false
        cbBalcon.isChecked = false
        switchAvailability.isChecked = false
    }
}