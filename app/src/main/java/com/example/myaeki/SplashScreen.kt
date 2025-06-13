package com.example.myaeki

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val videoView = findViewById<VideoView>(R.id.videoView)

        val videoUri = Uri.parse("android.resource://${packageName}/${R.raw.dekora}")
        videoView.setVideoURI(videoUri)

        videoView.setOnCompletionListener {
            // Setelah video selesai, pindah ke MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        videoView.setOnPreparedListener { it.isLooping = false }
        videoView.start()
    }
}
