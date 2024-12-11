package com.example.luxeinn


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener


import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var hotelAdapter: HotelAdapter
    private lateinit var hotelList: MutableList<Hotel2>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Configuración de la Toolbar personalizada
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Programar la alarma para enviar notificaciones cada 2 minutos
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val interval = 1 * 60 * 1000 // 1 minutos en milisegundos

        val startTime = Calendar.getInstance().timeInMillis

        // Configurar la alarma repetitiva
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            startTime,
            interval.toLong(),
            pendingIntent
        )


        recyclerView = findViewById(R.id.rv_hotels)
        recyclerView.layoutManager = LinearLayoutManager(this)
        hotelList = mutableListOf()

        hotelAdapter = HotelAdapter(this, hotelList)
        recyclerView.adapter = hotelAdapter

        fetchHotels()
    }


    // Inflar el menú de desbordamiento
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.overflow_menu, menu)
        return true
    }

    // Manejar las opciones seleccionadas del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_hoteles -> {
                val catalogoIntent = Intent(this, HotelDescription::class.java)
                startActivity(catalogoIntent)
                return true
            }
            R.id.menu_mapa -> {
                val videoIntent = Intent(this, RegistroHotelActivity::class.java)
                startActivity(videoIntent)
                return true
            }
            R.id.FAQ -> {
                val sensorIntent = Intent(this, Preguntas_Frecuentes::class.java)
                startActivity(sensorIntent)
                return true
            }
            R.id.aboutUs -> {
                //val sensorIntent = Intent(this, AboutUs::class.java)
                //startActivity(sensorIntent)
                return true
            }

            R.id.logout -> {
                return true
            }
            R.id.Registrar -> {
                val registroIntent = Intent(this, RegistroHotelActivity::class.java)
                startActivity(registroIntent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)

    }

    private fun fetchHotels() {
        val hotelRef = FirebaseDatabase.getInstance().getReference("Hotels")
        hotelRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                hotelList.clear()
                for (hotelSnapshot in snapshot.children) {
                    val name = hotelSnapshot.child("name").getValue(String::class.java) ?: "No Name"
                    val location = hotelSnapshot.child("location").getValue(String::class.java) ?: "Unknown"
                    val services = hotelSnapshot.child("services")
                        .getValue(object : GenericTypeIndicator<List<String>>() {})
                        ?.joinToString(", ") ?: "No services"
                    val imageUrl = hotelSnapshot.child("imageUrl").getValue(String::class.java) ?: ""


                    // Agregar el hotel a la lista
                    hotelList.add(Hotel2(name, location, services, imageUrl))
                }
                hotelAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores
                Log.e("FirebaseError", "Error al obtener datos: ${error.message}")
            }
        })
    }
}