package com.example.luxeinn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import java.util.Calendar

class MainActivity : AppCompatActivity() {
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
                //val videoIntent = Intent(this, MapaHoteles::class.java)
                //startActivity(videoIntent)
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
        }
        return super.onOptionsItemSelected(item)

    }
}