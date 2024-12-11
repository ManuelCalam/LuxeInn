package com.example.luxeinn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configuración de la Toolbar personalizada
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
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