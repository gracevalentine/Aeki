package com.example.myaeki.Authentication.View

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myaeki.API.ApiClient
import com.example.myaeki.Authentication.Model.UserProfileResponse
import com.example.myaeki.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountFragment : Fragment() {

    private lateinit var logoutButton: Button
    private lateinit var nameTextView: TextView
    private lateinit var idTextView: TextView
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        logoutButton = view.findViewById(R.id.button_logout)
        nameTextView = view.findViewById(R.id.nameTextView)
        idTextView = view.findViewById(R.id.idTextView)

        // Ambil SharedPreferences
        sharedPref = requireActivity().getSharedPreferences("MyAppPrefs", 0)

        // Ambil userId dari SharedPreferences (harus sudah login sebelumnya)
        val userId = sharedPref.getString("USER_ID", null)
        val allPrefs = sharedPref.all
        Log.d("AccountFragment", "SharedPrefs content: $allPrefs")

        if (userId != null) {
            fetchUserProfile(userId)
        } else {
            Toast.makeText(requireContext(), "User belum login", Toast.LENGTH_SHORT).show()
            // Bisa navigasi ke login screen
        }

        logoutButton.setOnClickListener {
            // Hapus data SharedPreferences saat logout
            sharedPref.edit().clear().apply()
            Toast.makeText(requireContext(), "Logout berhasil", Toast.LENGTH_SHORT).show()

            // Sembunyikan BottomNavigationView langsung dari fragment
            activity?.findViewById<View>(R.id.bottomNavViewInclude)?.visibility = View.GONE

            // Navigasi ke SignInFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.main, SignInFragment())
                .addToBackStack(null)
                .commit()
        }

    }

    private fun fetchUserProfile(userId: String) {
        Log.d("AccountFragment", "Fetching user profile for userId: $userId")
        ApiClient.userService.getUserProfile(userId).enqueue(object :
            Callback<UserProfileResponse> {
            override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                Log.d("AccountFragment", "Response received: $response")
                if (response.isSuccessful) {
                    val profile = response.body()?.user
                    if (profile != null) {
                        Log.d("AccountFragment", "User profile fetched: $profile")
                        nameTextView.text = "${profile.first_name} ${profile.last_name}"
                        idTextView.text = "ID: ${profile.user_id}"
                    } else {
                        Log.w("AccountFragment", "Profile kosong di response body")
                        Toast.makeText(requireContext(), "Profil kosong", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("AccountFragment", "Error response code: ${response.code()}")
                    Log.e("AccountFragment", "Error response body: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Gagal memuat profil", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                Log.e("AccountFragment", "Failed to fetch user profile", t)
                Toast.makeText(requireContext(), "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}