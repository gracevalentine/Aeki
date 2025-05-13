package com.example.myaeki

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Home : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_intro)

        val btnGoToSignIn = findViewById<Button>(R.id.btnGoToSignIn)
        val btnGoToSignUp = findViewById<Button>(R.id.btnGoToSignUp)

        btnGoToSignIn.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
            finish()  // Close MainActivity to prevent navigating back to it
        }

        // Go to SignUp Activity
        btnGoToSignUp.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }
}
