package com.example.myaeki

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

class Fragment_Account_Menu : Fragment() {

    private lateinit var logoutButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout fragment-nya
        return inflater.inflate(R.layout.fragment_account, container, false) // Ganti dengan nama XML kamu
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        logoutButton = view.findViewById(R.id.button_logout)

        logoutButton.setOnClickListener {
            Toast.makeText(requireContext(), "Logout berhasil", Toast.LENGTH_SHORT).show()

            // Contoh navigasi balik ke login/home fragment kalau pakai Navigation Component:
            // findNavController().navigate(R.id.action_akunFragment_to_loginFragment)

            // Atau logika lain sesuai kebutuhan
        }
    }
}
