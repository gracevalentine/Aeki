package com.example.myaeki

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Akses komponen layout
        val searchView = findViewById<android.widget.SearchView>(R.id.searchView)

        val imageProduct1 = findViewById<ImageView>(R.id.imageProduct1)
        val productName1 = findViewById<TextView>(R.id.productName1)
        val productDescription1 = findViewById<TextView>(R.id.productDescription1)
        val productPrice1 = findViewById<TextView>(R.id.productPrice1)

        val imageProduct2 = findViewById<ImageView>(R.id.imageProduct2)
        val productName2 = findViewById<TextView>(R.id.productName2)
        val productDescription2 = findViewById<TextView>(R.id.productDescription2)
        val productPrice2 = findViewById<TextView>(R.id.productPrice2)

        val imageProduct3 = findViewById<ImageView>(R.id.imageProduct3)
        val productName3 = findViewById<TextView>(R.id.productName3)
        val productDescription3 = findViewById<TextView>(R.id.productDescription3)
        val productPrice3 = findViewById<TextView>(R.id.productPrice3)

        // Contoh interaksi: klik produk pertama
        imageProduct1.setOnClickListener {
            Toast.makeText(this, "Kamu klik ${productName1.text}", Toast.LENGTH_SHORT).show()
        }

        // Contoh interaksi: pencarian
        searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(this@MainActivity, "Cari: $query", Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        // Load DetailProductFragment saat pertama kali activity dibuka
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DetailProductFragment())
                .commit()
        }
    }
}
