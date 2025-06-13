package com.example.myaeki

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myaeki.API.ApiClient
import com.example.myaeki.Product.View.DetailProductFragment
import com.example.myaeki.Wallet.Model.WalletResponse
import com.example.myaeki.wallet.view.TopUpFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var saldoText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saldoText = view.findViewById(R.id.saldoDekoraPay)

        val searchButton = view.findViewById<ImageView>(R.id.searchButtoninHome)
        val topUpButton = view.findViewById<ImageButton>(R.id.topUpButton)

        val imageProduct1 = view.findViewById<ImageView>(R.id.imageProduct1)

        imageProduct1.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main, DetailProductFragment())
                .addToBackStack(null)
                .commit()
        }

        topUpButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main, TopUpFragment())
                .addToBackStack(null)
                .commit()
        }

        searchButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main, SearchFragment())
                .addToBackStack(null)
                .commit()
        }

        // Ambil user_id dari SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("USER_ID", null)?.toIntOrNull()

        // Hit endpoint wallet dan update saldo
        userId?.let {
            ApiClient.walletService.getWalletByUserId(it)
                .enqueue(object : Callback<WalletResponse> {
                    override fun onResponse(
                        call: Call<WalletResponse>,
                        response: Response<WalletResponse>
                    ) {
                        if (response.isSuccessful) {
                            val wallet = response.body()?.wallet ?: 0.0
                            val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
                            saldoText.text = formatter.format(wallet)
                        } else {
                            saldoText.text = "Rp 0"
                            Log.d("Retrofit", call.request().url.toString())
                        }
                    }

                    override fun onFailure(call: Call<WalletResponse>, t: Throwable) {
                        saldoText.text = "Rp 0"
                    }
                })
        } ?: run {
            saldoText.text = "Rp 0"
        }
    }
}
