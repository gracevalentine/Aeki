package com.example.myaeki.Authentication.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myaeki.API.ApiClient
import com.example.myaeki.Authentication.Model.UserResponse
import com.example.myaeki.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.myaeki.Authentication.Model.SignupRequest
import com.example.myaeki.Authentication.Model.AuthService

class SignUpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)

        // Ambil view
        val email = view.findViewById<EditText>(R.id.email)
        val firstName = view.findViewById<EditText>(R.id.first_name)
        val lastName = view.findViewById<EditText>(R.id.last_name)
        val address = view.findViewById<EditText>(R.id.address)
        val postalCode = view.findViewById<EditText>(R.id.postal_code)
        val password = view.findViewById<EditText>(R.id.password)
        val confirmPassword = view.findViewById<EditText>(R.id.confirm_password)
        val termsCheckBox = view.findViewById<CheckBox>(R.id.terms_checkbox)
        val createButton = view.findViewById<Button>(R.id.create_account_button)

        createButton.setOnClickListener {
            val emailText = email.text.toString().trim()
            val first = firstName.text.toString().trim()
            val last = lastName.text.toString().trim()
            val add = address.text.toString().trim()
            val code = postalCode.text.toString().trim()
            val pass = password.text.toString()
            val confirmPass = confirmPassword.text.toString()

            // Validasi semua field harus diisi
            if (emailText.isEmpty() || first.isEmpty() || last.isEmpty() || add.isEmpty()
                || code.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()
            ) {
                Toast.makeText(requireContext(), "Semua field harus diisi", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Validasi email harus @gmail.com
            if (!emailText.endsWith("@gmail.com", ignoreCase = true)) {
                Toast.makeText(
                    requireContext(),
                    "Email harus menggunakan @gmail.com",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Validasi kode pos harus angka
            if (!code.matches(Regex("^[0-9]+$"))) {
                Toast.makeText(requireContext(), "Kode pos hanya boleh angka", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Validasi konfirmasi password
            if (pass != confirmPass) {
                Toast.makeText(
                    requireContext(),
                    "Password dan konfirmasi tidak cocok",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Validasi checkbox
            if (!termsCheckBox.isChecked) {
                Toast.makeText(
                    requireContext(),
                    "Harap setujui kebijakan privasi",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Data valid, kirim request
            val signupRequest = SignupRequest(
                first_name = first,
                last_name = last,
                email = emailText,
                password = pass,
                address = add,
                postal_code = code.toInt(),
                email_checkbox = true
            )

            sendSignupRequest(signupRequest)
        }

        return view
    }

    private fun sendSignupRequest(signupRequest: SignupRequest) {
        ApiClient.authService.signup(signupRequest).enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(requireContext(), "Akun berhasil dibuat!", Toast.LENGTH_SHORT)
                        .show()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.main, SignInFragment())
                        .addToBackStack(null)
                        .commit()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Gagal daftar: ${response.errorBody()?.string() ?: "Unknown error"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.localizedMessage}", Toast.LENGTH_LONG)
                    .show()
            }
        })
    }
}