package com.example.myaeki

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import androidx.fragment.app.Fragment
import com.example.myaeki.api.SignupRequest
import com.example.myaeki.api.UserResponse
import retrofit2.Response


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
            val first = firstName.text.toString()
            val last = lastName.text.toString()
            val add = address.text.toString()
            val code = postalCode.text.toString()
            val pass = password.text.toString()
            val confirmPass = confirmPassword.text.toString()

            if (emailText.isBlank() || first.isBlank() || last.isBlank() || add.isBlank() || code.isBlank() || pass.isBlank() || confirmPass.isBlank()) {
                Toast.makeText(requireContext(), "Isi semua data wajib!", Toast.LENGTH_SHORT).show()
            } else if (!emailText.endsWith("@gmail.com")) {
                Toast.makeText(requireContext(), "Masukkan email yang benar (harus @gmail.com)", Toast.LENGTH_SHORT).show()
            } else if (!code.matches(Regex("^[0-9]+$"))) {
                Toast.makeText(requireContext(), "Kode pos hanya boleh berupa angka", Toast.LENGTH_SHORT).show()
            } else if (pass != confirmPass) {
                Toast.makeText(requireContext(), "Password tidak cocok", Toast.LENGTH_SHORT).show()
            } else if (!termsCheckBox.isChecked) {
                Toast.makeText(requireContext(), "Harap setujui kebijakan privasi", Toast.LENGTH_SHORT).show()
            } else {
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
                    Toast.makeText(requireContext(), "Akun berhasil dibuat!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.main, SignInFragment())
                        .addToBackStack(null)
                        .commit()
                } else {
                    Toast.makeText(requireContext(), "Gagal daftar: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
