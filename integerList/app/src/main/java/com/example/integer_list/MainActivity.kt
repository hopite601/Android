package com.example.numberlist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.integer_list.R

class MainActivity : AppCompatActivity() {

    private lateinit var etNumber: EditText
    private lateinit var rgNumberType: RadioGroup
    private lateinit var rbOdd: RadioButton
    private lateinit var rbPrime: RadioButton
    private lateinit var rbPerfect: RadioButton
    private lateinit var rbEven: RadioButton
    private lateinit var rbSquare: RadioButton
    private lateinit var rbFibonacci: RadioButton
    private lateinit var lvNumbers: ListView
    private lateinit var tvNoData: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupListeners()

        // Đặt giá trị mặc định và cập nhật danh sách
        etNumber.setText("100")
        rbOdd.isChecked = true

        // Gọi updateNumberList sau khi đã set giá trị
        etNumber.post {
            updateNumberList()
        }
    }

    private fun initViews() {
        etNumber = findViewById(R.id.etNumber)
        rgNumberType = findViewById(R.id.rgNumberType)
        rbOdd = findViewById(R.id.rbOdd)
        rbPrime = findViewById(R.id.rbPrime)
        rbPerfect = findViewById(R.id.rbPerfect)
        rbEven = findViewById(R.id.rbEven)
        rbSquare = findViewById(R.id.rbSquare)
        rbFibonacci = findViewById(R.id.rbFibonacci)
        lvNumbers = findViewById(R.id.lvNumbers)
        tvNoData = findViewById(R.id.tvNoData)
    }

    private fun setupListeners() {
        etNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateNumberList()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        rgNumberType.setOnCheckedChangeListener { _, _ ->
            updateNumberList()
        }
    }

    private fun updateNumberList() {
        val input = etNumber.text.toString()

        // Kiểm tra input rỗng hoặc không hợp lệ
        if (input.isEmpty()) {
            showNoData()
            return
        }

        val n = try {
            input.toInt()
        } catch (e: NumberFormatException) {
            showNoData()
            return
        }

        if (n <= 0) {
            showNoData()
            return
        }

        // Lấy ID của RadioButton được chọn
        val selectedId = rgNumberType.checkedRadioButtonId

        val numbers = when (selectedId) {
            R.id.rbOdd -> getOddNumbers(n)
            R.id.rbPrime -> getPrimeNumbers(n)
            R.id.rbPerfect -> getPerfectNumbers(n)
            R.id.rbEven -> getEvenNumbers(n)
            R.id.rbSquare -> getSquareNumbers(n)
            R.id.rbFibonacci -> getFibonacciNumbers(n)
            else -> {
                // Nếu không có gì được chọn, mặc định chọn số lẻ
                rbOdd.isChecked = true
                getOddNumbers(n)
            }
        }

        if (numbers.isEmpty()) {
            showNoData()
        } else {
            showNumbers(numbers)
        }
    }

    private fun showNumbers(numbers: List<Int>) {
        tvNoData.visibility = View.GONE
        lvNumbers.visibility = View.VISIBLE
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, numbers)
        lvNumbers.adapter = adapter
    }

    private fun showNoData() {
        tvNoData.visibility = View.VISIBLE
        lvNumbers.visibility = View.GONE
    }

    private fun getOddNumbers(n: Int): List<Int> {
        return (1 until n).filter { it % 2 != 0 }
    }

    private fun getEvenNumbers(n: Int): List<Int> {
        return (2 until n).filter { it % 2 == 0 }
    }

    private fun getPrimeNumbers(n: Int): List<Int> {
        return (2 until n).filter { isPrime(it) }
    }

    private fun isPrime(num: Int): Boolean {
        if (num < 2) return false
        if (num == 2) return true
        if (num % 2 == 0) return false
        for (i in 3..Math.sqrt(num.toDouble()).toInt() step 2) {
            if (num % i == 0) return false
        }
        return true
    }

    private fun getPerfectNumbers(n: Int): List<Int> {
        return (2 until n).filter { isPerfect(it) }
    }

    private fun isPerfect(num: Int): Boolean {
        var sum = 1
        for (i in 2..Math.sqrt(num.toDouble()).toInt()) {
            if (num % i == 0) {
                sum += i
                if (i != num / i) {
                    sum += num / i
                }
            }
        }
        return sum == num && num > 1
    }

    private fun getSquareNumbers(n: Int): List<Int> {
        val result = mutableListOf<Int>()
        var i = 1
        while (i * i < n) {
            result.add(i * i)
            i++
        }
        return result
    }

    private fun getFibonacciNumbers(n: Int): List<Int> {
        val result = mutableListOf<Int>()
        var a = 1
        var b = 1
        while (a < n) {
            result.add(a)
            val temp = a + b
            a = b
            b = temp
        }
        return result
    }
}