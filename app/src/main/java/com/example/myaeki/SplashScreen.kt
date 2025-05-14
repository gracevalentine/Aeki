package com.example.myaeki

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        val videoView = findViewById<VideoView>(R.id.videoView)
        val videoPath = "android.resource://${packageName}/${R.raw.intro}" // contoh video di res/raw

        videoView.setVideoURI(Uri.parse(videoPath))
        videoView.setOnCompletionListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        videoView.start()
    }
}