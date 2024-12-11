package com.example.luxeinn

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Programar la alarma para enviar notificaciones cada 2 minutos
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val interval = 1 * 60 * 1000 // 2 minutos en milisegundos
        val startTime = Calendar.getInstance().timeInMillis

        // Configurar la alarma repetitiva
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            startTime,
            interval.toLong(),
            pendingIntent
        )
    }
}