package com.example.myaeki

import android.content.Intent
import android.os.Bundle
import android.text.Html.ImageGetter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myaeki.MainActivity
import com.example.myaeki.databinding.FragmentTopUpBinding

class TopUpFragment : Fragment() {

    private var _binding: FragmentTopUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var nominalButtons: List<Button>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTopUpBinding.inflate(inflater, container, false)
        val backButton = view?.findViewById<ImageView>(R.id.backButtonTopUp)
        backButton?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi daftar tombol nominal
        nominalButtons = listOf(
            binding.btnNominal20000,
            binding.btnNominal50000,
            binding.btnNominal100000
        )

//        setupBackButton()
        setupNominalButtons()
        setupConfirmButton()
    }

//    private fun setupBackButton() {
//        binding.backButtonTopUp.setOnClickListener {
//            val intent = Intent(requireContext(), HomeFragment::class.java)
//            startActivity(intent)
//            requireActivity().finish()
//        }
//    }


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