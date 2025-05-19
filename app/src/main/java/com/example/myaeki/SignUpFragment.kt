package com.example.myaeki

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class SignUpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)

        // Ambil view yang diperlukan
        val email = view.findViewById<EditText>(R.id.email)
        val firstName = view.findViewById<EditText>(R.id.first_name)
        val lastName = view.findViewById<EditText>(R.id.last_name)
        val address = view.findViewById<EditText>(R.id.address)
        val postalCode = view.findViewById<EditText>(R.id.postal_code)
        val password = view.findViewById<EditText>(R.id.password)
        val confirmPassword = view.findViewById<EditText>(R.id.confirm_password)
        val termsCheckBox = view.findViewById<CheckBox>(R.id.terms_checkbox)
        val createButton = view.findViewById<Button>(R.id.create_account_button)

        // Event saat tombol ditekan
        createButton.setOnClickListener {
            val emailText = email.text.toString()
            val pass = password.text.toString()
            val confirmPass = confirmPassword.text.toString()

            if (emailText.isBlank() || pass.isBlank()) {
                Toast.makeText(requireContext(), "Isi semua data wajib!", Toast.LENGTH_SHORT).show()
            } else if (pass != confirmPass) {
                Toast.makeText(requireContext(), "Password tidak cocok", Toast.LENGTH_SHORT).show()
            } else if (!termsCheckBox.isChecked) {
                Toast.makeText(requireContext(), "Harap setujui kebijakan privasi", Toast.LENGTH_SHORT).show()
            } else {
                // Lanjutkan ke proses pendaftaran
                Toast.makeText(requireContext(), "Akun berhasil dibuat!", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
