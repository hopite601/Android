package com.example.bai1

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.abs
import kotlin.math.round

class MainActivity : AppCompatActivity() {

    private lateinit var display: TextView

    private var currentInput: String = ""        // số đang nhập (chuỗi)
    private var operator: String? = null         // "+", "-", "×", "÷"
    private var firstOperand: Double? = null     // toán hạng 1
    private var lastResult: Double? = null       // kết quả trước đó
    private var resetOnNextInput: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activitymainbai1) // giữ nguyên tên layout của bạn

        display = findViewById(R.id.display)

        // Nút số
        val numberButtons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
            R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7,
            R.id.btn8, R.id.btn9
        )
        numberButtons.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                onNumberClick((it as Button).text.toString())
            }
        }

        // Phép toán
        findViewById<Button>(R.id.btnPlus).setOnClickListener { onOperatorClick("+") }
        findViewById<Button>(R.id.btnMinus).setOnClickListener { onOperatorClick("-") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { onOperatorClick("×") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { onOperatorClick("÷") }

        // Bằng
        findViewById<Button>(R.id.btnEquals).setOnClickListener { onEqualClick() }

        // CE, C, BS, ., +/-
        findViewById<Button>(R.id.btnCE).setOnClickListener { clearAll() }
        findViewById<Button>(R.id.btnC).setOnClickListener { clearEntry() }
        findViewById<Button>(R.id.btnBS).setOnClickListener { backspace() }
        findViewById<Button>(R.id.btnDot).setOnClickListener { onDotClick() }
        findViewById<Button>(R.id.btnPlusMinus).setOnClickListener { toggleSign() }

        // Hiển thị mặc định
        updateDisplay()
    }

    // ---- xử lý nhập số ----
    private fun onNumberClick(number: String) {
        if (resetOnNextInput) {
            // nếu mới vừa có kết quả và user bắt đầu nhập tiếp -> reset
            currentInput = ""
            operator = null
            firstOperand = null
            lastResult = null
            resetOnNextInput = false
        }
        // tránh nhiều số 0 đầu
        if (currentInput == "0") currentInput = number else currentInput += number
        updateDisplay()
    }

    // ---- xử lý khi bấm phép toán ----
    private fun onOperatorClick(op: String) {
        // nếu không có số hiện tại nhưng có lastResult -> dùng lastResult làm firstOperand
        if (currentInput.isEmpty() && lastResult == null) {
            // không có gì để làm
            return
        }

        if (firstOperand == null) {
            // set firstOperand từ số đang nhập hoặc từ lastResult
            firstOperand = currentInput.toDoubleOrNull() ?: lastResult
        } else if (operator != null && currentInput.isNotEmpty()) {
            // có firstOperand + operator + số hiện tại -> tính ngay (chain operations)
            val sec = currentInput.toDoubleOrNull() ?: 0.0
            val res = calculate(firstOperand!!, sec, operator!!)
            firstOperand = res
            lastResult = res
            // giữ currentInput trống để nhập toán hạng tiếp theo
        }

        operator = op
        currentInput = ""
        resetOnNextInput = false
        updateDisplay()
    }

    // ---- xử lý "=" ----
    private fun onEqualClick() {
        if (firstOperand == null || operator == null || currentInput.isEmpty()) {
            // nothing to compute
            return
        }
        val second = currentInput.toDoubleOrNull() ?: 0.0
        val result = calculate(firstOperand!!, second, operator!!)
        // hiển thị như: "7 + 2 = 9"
        display.text = "${formatNumber(firstOperand!!)} $operator ${formatNumber(second)} = ${formatNumber(result)}"

        lastResult = result
        // reset để bắt đầu phép mới nếu user nhập số tiếp
        currentInput = ""
        firstOperand = null
        operator = null
        resetOnNextInput = true
    }

    // ---- tính toán ----
    private fun calculate(a: Double, b: Double, op: String): Double {
        return when (op) {
            "+" -> a + b
            "-" -> a - b
            "×" -> a * b
            "÷" -> if (b != 0.0) a / b else Double.NaN
            else -> 0.0
        }
    }

    // ---- clear all ----
    private fun clearAll() {
        currentInput = ""
        operator = null
        firstOperand = null
        lastResult = null
        resetOnNextInput = false
        updateDisplay()
    }

    // ---- clear entry (xóa số hiện tại) ----
    private fun clearEntry() {
        currentInput = ""
        updateDisplay()
    }

    // ---- backspace ----
    private fun backspace() {
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
        } else if (operator != null) {
            // nếu không còn số đang nhập nhưng có operator -> xóa operator
            operator = null
        } else if (firstOperand != null) {
            // nếu chỉ còn firstOperand (đã lưu) -> chuyển firstOperand sang currentInput để xóa
            currentInput = formatNumber(firstOperand!!).toString()
            firstOperand = null
            if (currentInput.isNotEmpty()) currentInput = currentInput.dropLast(1)
        }
        updateDisplay()
    }

    // ---- dấu chấm ----
    private fun onDotClick() {
        if (resetOnNextInput) {
            currentInput = ""
            resetOnNextInput = false
        }
        if (!currentInput.contains(".")) {
            if (currentInput.isEmpty()) currentInput = "0"
            currentInput += "."
            updateDisplay()
        }
    }

    // ---- đổi dấu ----
    private fun toggleSign() {
        if (currentInput.isEmpty()) return
        currentInput = if (currentInput.startsWith("-")) currentInput.substring(1) else "-$currentInput"
        updateDisplay()
    }

    // ---- cập nhật màn hình: hiển thị biểu thức hoặc số đang nhập ----
    private fun updateDisplay() {
        // Nếu đang nhập phép thứ hai (operator != null), hiển thị full expression: "first op current"
        if (operator != null) {
            val left = firstOperand?.let { formatNumber(it) } ?: ""
            val right = if (currentInput.isEmpty()) "" else currentInput
            // hiển thị như "7 + 2" (nếu right rỗng thì "7 + ")
            display.text = if (right.isEmpty()) "$left $operator " else "$left $operator $right"
        } else {
            // không có operator -> chỉ hiển thị số hiện tại, nếu rỗng thì hiển thị lastResult hoặc 0
            when {
                currentInput.isNotEmpty() -> display.text = currentInput
                lastResult != null -> display.text = formatNumber(lastResult!!)
                else -> display.text = "0"
            }
        }
    }

    // ---- helper: format double bỏ .0 nếu là số nguyên, và rút gọn chữ số thập phân nếu cần ----
    private fun formatNumber(value: Double): String {
        // nếu là NaN / Infinity thì trả về chuỗi tương ứng
        if (value.isNaN()) return "NaN"
        if (value.isInfinite()) return if (value > 0) "∞" else "-∞"

        val rounded = round(value * 1_000_000) / 1_000_000 // giữ tối đa 6 chữ số thập phân
        return if (abs(rounded % 1.0) < 1e-9) {
            // là số nguyên
            rounded.toLong().toString()
        } else {
            // bỏ 0 phụ ở cuối (ví dụ 2.500000 -> 2.5)
            var s = rounded.toString()
            // loại bỏ trailing zeros và trailing dot nếu cần
            if (s.contains('.')) {
                s = s.trimEnd('0').trimEnd('.')
            }
            s
        }
    }
}
