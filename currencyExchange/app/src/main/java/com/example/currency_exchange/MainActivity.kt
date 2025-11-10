package com.example.currencyconverter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.currency_exchange.R

class MainActivity : AppCompatActivity() {

    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    private lateinit var editTextFrom: EditText
    private lateinit var editTextTo: EditText

    private var isUpdating = false

    // Tỷ giá so với USD (1 USD = ? đơn vị tiền tệ)
    private val exchangeRates = mapOf(
        "USD - Đô la Mỹ" to 1.0,
        "EUR - Euro" to 0.85,
        "GBP - Bảng Anh" to 0.73,
        "JPY - Yên Nhật" to 110.0,
        "VND - Việt Nam Đồng" to 23000.0,
        "CNY - Nhân dân tệ" to 6.45,
        "KRW - Won Hàn Quốc" to 1180.0,
        "THB - Baht Thái Lan" to 33.0,
        "SGD - Đô la Singapore" to 1.35,
        "AUD - Đô la Úc" to 1.35,
        "CAD - Đô la Canada" to 1.25,
        "CHF - Franc Thụy Sĩ" to 0.92
    )

    private val currencies = exchangeRates.keys.toList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupSpinners()
        setupTextWatchers()
    }

    private fun initViews() {
        spinnerFrom = findViewById(R.id.spinnerFrom)
        spinnerTo = findViewById(R.id.spinnerTo)
        editTextFrom = findViewById(R.id.editTextFrom)
        editTextTo = findViewById(R.id.editTextTo)
    }

    private fun setupSpinners() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerFrom.adapter = adapter
        spinnerTo.adapter = adapter

        // Đặt mặc định
        spinnerFrom.setSelection(0) // USD
        spinnerTo.setSelection(4) // VND

        // Listener cho spinner From
        spinnerFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                convertCurrency(true)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Listener cho spinner To
        spinnerTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                convertCurrency(true)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupTextWatchers() {
        // TextWatcher cho EditText From
        editTextFrom.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!isUpdating) {
                    convertCurrency(true)
                }
            }
        })

        // TextWatcher cho EditText To
        editTextTo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!isUpdating) {
                    convertCurrency(false)
                }
            }
        })
    }

    private fun convertCurrency(fromSource: Boolean) {
        isUpdating = true

        try {
            if (fromSource) {
                // Chuyển đổi từ editTextFrom sang editTextTo
                val amountStr = editTextFrom.text.toString()
                if (amountStr.isNotEmpty() && amountStr != ".") {
                    val amount = amountStr.toDoubleOrNull()
                    if (amount != null) {
                        val result = convertAmount(
                            amount,
                            currencies[spinnerFrom.selectedItemPosition],
                            currencies[spinnerTo.selectedItemPosition]
                        )
                        editTextTo.setText(formatResult(result))
                    }
                } else {
                    editTextTo.setText("")
                }
            } else {
                // Chuyển đổi từ editTextTo sang editTextFrom
                val amountStr = editTextTo.text.toString()
                if (amountStr.isNotEmpty() && amountStr != ".") {
                    val amount = amountStr.toDoubleOrNull()
                    if (amount != null) {
                        val result = convertAmount(
                            amount,
                            currencies[spinnerTo.selectedItemPosition],
                            currencies[spinnerFrom.selectedItemPosition]
                        )
                        editTextFrom.setText(formatResult(result))
                    }
                } else {
                    editTextFrom.setText("")
                }
            }
        } finally {
            isUpdating = false
        }
    }

    private fun convertAmount(amount: Double, fromCurrency: String, toCurrency: String): Double {
        val fromRate = exchangeRates[fromCurrency] ?: 1.0
        val toRate = exchangeRates[toCurrency] ?: 1.0

        // Chuyển về USD trước, sau đó chuyển sang tiền tệ đích
        val amountInUSD = amount / fromRate
        return amountInUSD * toRate
    }

    private fun formatResult(value: Double): String {
        return if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {
            String.format("%.2f", value)
        }
    }
}