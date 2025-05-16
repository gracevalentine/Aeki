package com.example.myaeki

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class SignUp : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_sign_up)
//
//        viewPager = findViewById(R.id.viewPager)
//        tabLayout = findViewById(R.id.tabLayout)
//
//        val adapter = SignUpPagerAdapter(this)
//        viewPager.adapter = adapter
//
//        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//            tab.text = if (position == 0) "Pribadi" else "Bisnis"
//        }.attach()

        findViewById<ImageView>(R.id.imageView).setOnClickListener {
            finish()
        }
    }
}
