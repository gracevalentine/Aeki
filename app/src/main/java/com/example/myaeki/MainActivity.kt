package com.example.myaeki

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity(), SignInFragment.LoginListener {

    private lateinit var bottomNavContainer: View
    private lateinit var navHome: LinearLayout
    private lateinit var navInspiration: LinearLayout
    private lateinit var navCart: LinearLayout
    private lateinit var navAccount: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Menghubungkan ID dari layout
        bottomNavContainer = findViewById(R.id.bottomNavViewInclude)
        navHome = findViewById(R.id.navHome)
        navInspiration = findViewById(R.id.navInspiration)
        navCart = findViewById(R.id.navCart)
        navAccount = findViewById(R.id.navAccount)

        // Awal app langsung load SignInFragment dan sembunyikan bottom nav
        bottomNavContainer.visibility = View.GONE
        loadFragment(SignInFragment())

        // Menangani item klik pada bottom navigation
        navHome.setOnClickListener { loadFragment(HomeFragment()) }
        navInspiration.setOnClickListener { loadFragment(InspoFragment()) }
        navCart.setOnClickListener { loadFragment(CartFragment()) }
        navAccount.setOnClickListener { loadFragment(AccountFragment()) }
    }

    // Load fragment ke dalam container
    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, fragment) // Pastikan ID main sesuai dengan FrameLayout di XML
            .commit() // Ganti commitNow() dengan commit() yang lebih aman
        return true
    }

    // Callback setelah login sukses
    override fun onLoginSuccess() {
        bottomNavContainer.visibility = View.VISIBLE
        loadFragment(HomeFragment()) // Memuat fragment Home setelah login sukses
    }
}
