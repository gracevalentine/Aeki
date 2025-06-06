package com.example.myaeki.wallet.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myaeki.HomeFragment
import com.example.myaeki.R
import com.example.myaeki.databinding.FragmentTopUpBinding

class TopUpFragment : Fragment() {

    private var _binding: FragmentTopUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var nominalButtons: List<Button>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopUpBinding.inflate(inflater, container, false)
        return binding.root  // ✅ INI YANG HARUS DI-RETURN
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Akses elemen dari include pakai view.findViewById
        val backBtn = view.findViewById<ImageView>(R.id.backButtonTopUp)
        backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Inisialisasi daftar tombol nominal
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
                // Reset semua tombol
                nominalButtons.forEach { it.isSelected = false }
                // Aktifkan yang dipilih
                button.isSelected = true
            }
        }
    }

    private fun setupConfirmButton() {
        binding.btnConfirmPayment.setOnClickListener {
            Toast.makeText(requireContext(), "Top Up Success", Toast.LENGTH_SHORT).show()
            parentFragmentManager.beginTransaction()
                .replace(R.id.main, HomeFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}