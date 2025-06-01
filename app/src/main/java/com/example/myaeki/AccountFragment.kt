package com.example.myaeki

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myaeki.API.UserProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountFragment : Fragment() {

    private lateinit var logoutButton: Button
    private lateinit var nameTextView: TextView
    private lateinit var idTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        logoutButton = view.findViewById(R.id.button_logout)
        nameTextView = view.findViewById(R.id.nameTextView) // Tambahkan id ini di XML
        idTextView = view.findViewById(R.id.idTextView)     // Tambahkan id ini di XML

        fetchUserProfile("160006312351") // ID user bisa dari SharedPreferences

        logoutButton.setOnClickListener {
            Toast.makeText(requireContext(), "Logout berhasil", Toast.LENGTH_SHORT).show()
            // Navigasi atau hapus sesi
        }
    }

    private fun fetchUserProfile(userId: String) {
        ApiClient.userService.getUserProfile(userId).enqueue(object : Callback<UserProfile> {
            override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                if (response.isSuccessful) {
                    val profile = response.body()
                    nameTextView.text = "${'$'}{profile?.first_name} ${'$'}{profile?.last_name}"
                    idTextView.text = "ID: ${'$'}{profile?.user_id}"
                } else {
                    Toast.makeText(requireContext(), "Gagal memuat profil", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${'$'}{t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

