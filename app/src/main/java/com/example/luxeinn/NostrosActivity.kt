package com.example.luxeinn

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class NostrosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nostros)
        val videoView = findViewById<VideoView>(R.id.videoView3)

        // Configuración del video
        val videoUri =
            Uri.parse("android.resource://" + packageName + "/" + R.raw.video)
        videoView.setVideoURI(videoUri)

        // Controlador de medios
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        // Iniciar el video
        videoView.start()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = resources.getColor(R.color.blue, theme)
        }
    }
}