package com.example.myaeki

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), SignInFragment.LoginListener {

    private lateinit var bottomNavContainer: View
    private lateinit var bottomNavView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavContainer = findViewById(R.id.bottomNavViewInclude)
        bottomNavView = findViewById(R.id.bottomNavView)

        // Awal app langsung load SignInFragment, hide bottom nav
        bottomNavContainer.visibility = View.GONE
        loadFragment(SignInFragment())

        bottomNavView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.home -> loadFragment(HomeFragment())
                R.id.inspiration -> loadFragment(InspoFragment())
                R.id.cart -> loadFragment(CartFragment())
                R.id.account -> loadFragment(AccountFragment())
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, fragment)
            .commitNow()
        return true
    }

    // Callback dari SignInFragment pas login sukses
    override fun onLoginSuccess() {
        bottomNavContainer.visibility = View.VISIBLE
        loadFragment(HomeFragment())
    }
}
