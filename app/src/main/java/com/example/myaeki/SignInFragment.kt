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

<<<<<<< HEAD
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val etEmailPhone = view.findViewById<EditText>(R.id.etEmailPhone)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)

        btnLogin.setOnClickListener {
            val emailOrPhone = etEmailPhone.text.toString()
            val password = etPassword.text.toString()

            if (emailOrPhone.isEmpty() || password.isEmpty()) {
=======
        etEmailPhone = view.findViewById(R.id.etEmailPhone)
        etPassword = view.findViewById(R.id.etPassword)
        btnLogin = view.findViewById(R.id.btnLogin)
        btnRegister = view.findViewById(R.id.btnRegister)

        btnLogin.setOnClickListener {
            val username = etEmailPhone.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
>>>>>>> 39b1fb8dbd12c4cde181c867d148516e4258fa9d
                Toast.makeText(requireContext(), "Data tidak boleh kosong", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

<<<<<<< HEAD
            parentFragmentManager.beginTransaction()
                .replace(R.id.main, HomeFragment())
                .addToBackStack(null)
                .commit()
        }

        btnRegister.setOnClickListener {

        }
    }
}
=======
            val loginRequest = LoginRequest(username, password)

            ApiClient.authService.login(loginRequest).enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    if (response.isSuccessful && response.body()?.user != null) {
                        val user = response.body()!!.user!!
                        Toast.makeText(
                            requireContext(),
                            "Selamat datang, ${user.username}",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Berhasil login → ke HomeFragment
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.main, HomeFragment())
                            .addToBackStack(null)
                            .commit()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Login gagal. Cek kembali username/password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }

        btnRegister.setOnClickListener {
            // Nanti bisa diarahkan ke RegisterFragment (jika sudah dibuat)
            Toast.makeText(requireContext(), "Arahkan ke halaman registrasi", Toast.LENGTH_SHORT)
                .show()
        }
    }
}
>>>>>>> 39b1fb8dbd12c4cde181c867d148516e4258fa9d
