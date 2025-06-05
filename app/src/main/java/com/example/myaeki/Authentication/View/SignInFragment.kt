package com.example.myaeki.Authentication.View

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myaeki.API.ApiClient
import com.example.myaeki.Authentication.Model.UserResponse
import com.example.myaeki.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.myaeki.Authentication.Model.LoginRequest

class SignInFragment : Fragment() {

    interface LoginListener {
        fun onLoginSuccess()
    }

    private var loginListener: LoginListener? = null

    private lateinit var etEmailPhone: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginListener = context as? LoginListener
    }

    override fun onDetach() {
        super.onDetach()
        loginListener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etEmailPhone = view.findViewById(R.id.etEmail)
        etPassword = view.findViewById(R.id.etPassword)
        btnLogin = view.findViewById(R.id.btnLogin)
        btnRegister = view.findViewById(R.id.btnRegister)

        btnLogin.setOnClickListener {
            val email = etEmailPhone.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Data tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val loginRequest = LoginRequest(email, password)

            ApiClient.authService.login(loginRequest).enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    if (response.isSuccessful && response.body()?.user != null) {
                        val user = response.body()!!.user!!

                        // Simpan user_id ke SharedPreferences
                        val sharedPref = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                        sharedPref.edit().putString("USER_ID", user.id.toString()).apply()

                        Toast.makeText(requireContext(), "Selamat datang, ${user.first_name}", Toast.LENGTH_SHORT).show()

                        // Login sukses, kasih tahu activity lewat callback
                        loginListener?.onLoginSuccess()
                    } else {
                        Toast.makeText(requireContext(), "Login gagal. Cek kembali email/password", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        btnRegister.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main, SignUpFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}