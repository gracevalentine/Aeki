package com.example.myaeki

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class SignInFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val etEmailPhone = view.findViewById<EditText>(R.id.etEmailPhone)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)

        btnLogin.setOnClickListener {
            val emailOrPhone = etEmailPhone.text.toString()
            val password = etPassword.text.toString()

            if (emailOrPhone.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Data tidak boleh kosong", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.main, HomeFragment())
                .addToBackStack(null)
                .commit()
        }

        btnRegister.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main, SignUpFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}
