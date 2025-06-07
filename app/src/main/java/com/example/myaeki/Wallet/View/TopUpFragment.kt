package com.example.myaeki.wallet.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myaeki.API.ApiClient.walletService
import com.example.myaeki.HomeFragment
import com.example.myaeki.R
import com.example.myaeki.Wallet.Model.TopUpRequest
import com.example.myaeki.Wallet.Model.TopUpResponse
import com.example.myaeki.databinding.FragmentTopUpBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TopUpFragment : Fragment() {

    private var _binding: FragmentTopUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var nominalButtons: List<Button>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backBtn = view.findViewById<ImageView>(R.id.backButtonTopUp)
        backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        nominalButtons = listOf(
            binding.btnNominal20000,
            binding.btnNominal50000,
            binding.btnNominal100000
        )

        setupNominalButtons()
        setupConfirmButton()
    }

    private fun setupNominalButtons() {
        nominalButtons.forEach { button ->
            button.setOnClickListener {
                nominalButtons.forEach { it.isSelected = false }
                button.isSelected = true
            }
        }
    }

    private fun setupConfirmButton() {
        binding.btnConfirmPayment.setOnClickListener {
            val selectedAmount = nominalButtons
                .firstOrNull { it.isSelected }
                ?.text
                ?.toString()
                ?.replace(".", "")
                ?.replace("Rp", "")
                ?.trim()
                ?.toDoubleOrNull()

            if (selectedAmount == null) {
                Toast.makeText(requireContext(), "Pilih nominal dulu bro", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ambil user_id dari SharedPreferences (harus sudah login sebelumnya)
            val sharedPref = requireActivity().getSharedPreferences("MyAppPrefs", 0)
            val userIdStr = sharedPref.getString("USER_ID", null)

            if (userIdStr == null) {
                Toast.makeText(requireContext(), "User belum login", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = userIdStr.toIntOrNull()
            if (userId == null) {
                Toast.makeText(requireContext(), "ID user tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = TopUpRequest(user_id = userId, amount = selectedAmount)

            walletService.topUp(request).enqueue(object : Callback<TopUpResponse> {
                override fun onResponse(call: Call<TopUpResponse>, response: Response<TopUpResponse>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Toast.makeText(requireContext(), result?.message ?: "Top up berhasil", Toast.LENGTH_SHORT).show()

                        parentFragmentManager.beginTransaction()
                            .replace(R.id.main, HomeFragment())
                            .addToBackStack(null)
                            .commit()
                    } else {
                        Toast.makeText(requireContext(), "Top up gagal: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<TopUpResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
