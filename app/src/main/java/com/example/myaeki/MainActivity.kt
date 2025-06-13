package com.example.myaeki

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myaeki.Authentication.View.AccountFragment
import com.example.myaeki.Authentication.View.SignInFragment
import com.example.myaeki.Transaction.View.CartFragment

class MainActivity : AppCompatActivity(), SignInFragment.LoginListener {

    private lateinit var bottomNavContainer: View
    private lateinit var navHome: LinearLayout
    private lateinit var navCart: LinearLayout
    private lateinit var navAccount: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Menghubungkan ID dari layout
        bottomNavContainer = findViewById(R.id.bottomNavViewInclude)
        navHome = findViewById(R.id.navHome)
        navCart = findViewById(R.id.navCart)
        navAccount = findViewById(R.id.navAccount)

        bottomNavContainer.visibility = View.GONE
        loadFragment(SignInFragment())

        navHome.setOnClickListener { loadFragment(HomeFragment()) }
        navCart.setOnClickListener { loadFragment(CartFragment()) }
        navAccount.setOnClickListener { loadFragment(AccountFragment()) }
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, fragment)
            .commit()
        return true
    }

    override fun onLoginSuccess() {
        bottomNavContainer.visibility = View.VISIBLE
        loadFragment(HomeFragment())
    }
}
