import kotlin.math.abs

class PhanSo(var tu: Int, var mau: Int) : Comparable<PhanSo> {

    // In phân số dạng "tu/mau"
    fun inPS() = println(this.toString())

    // Tối giản (rút gọn) phân số, giữ mẫu dương
    fun toiGian(): PhanSo {
        if (mau < 0) { tu = -tu; mau = -mau } // doi dau neu mau am
        val g = gcd(abs(tu), abs(mau)) 
        if (g != 0) { tu /= g; mau /= g }
        return this
    }

    // So sánh với phân số khác: -1, 0, 1
    override fun compareTo(other: PhanSo): Int {
        val left  = tu.toLong() * other.mau.toLong()
        val right = other.tu.toLong() * mau.toLong()
        return left.compareTo(right)
    }

    // Tính tổng với một phân số khác (trả về phân số mới đã tối giản)
    operator fun plus(other: PhanSo): PhanSo {
        val num = tu.toLong() * other.mau.toLong() + other.tu.toLong() * mau.toLong()
        val den = mau.toLong() * other.mau.toLong()
        // Ép về Int (giả sử không tràn trong phạm vi bài tập)
        return PhanSo(num.toInt(), den.toInt()).toiGian()
    }

    override fun toString(): String = "$tu/$mau"

    companion object {
        // nhập 1 phân số từ bàn phím, bắt nhập lại nếu tử hoặc mẫu = 0
        fun nhap(): PhanSo {
            while (true) {
                try {
                    print("  Nhập tử số (số nguyên, != 0): ")
                    val tu = readln().toInt()
                    print("  Nhập mẫu số (số nguyên, != 0): ")
                    val mau = readln().toInt()

                    if (tu == 0 || mau == 0) {
                        println("  ➤ Tử số và mẫu số đều phải khác 0. Vui lòng nhập lại!\n")
                        continue
                    }

                    // Chuẩn hoá mẫu dương; không rút gọn ở bước nhập
                    val (ntu, nmau) = if (mau < 0) -tu to -mau else tu to mau
                    return PhanSo(ntu, nmau)
                } catch (e: NumberFormatException) {
                    println("  ➤ Vui lòng nhập số nguyên hợp lệ!\n")
                }
            }
        }

        private fun gcd(a: Int, b: Int): Int {
            var x = a; var y = b
            while (y != 0) { val r = x % y; x = y; y = r }
            return x
        }
    }
}

fun main() {
    // 1) Nhập mảng phân số
    val n = nhapSoLuong()
    val arr = MutableList(n) { idx ->
        println("Nhập phân số thứ ${idx + 1}:")
        PhanSo.nhap()
    }

    // 2) In ra mảng vừa nhập
    println("\n==> MẢNG PHÂN SỐ VỪA NHẬP:")
    inMang(arr)

    // 3) Tối giản từng phần tử và in kết quả
    val arrToiGian = arr.map { PhanSo(it.tu, it.mau).toiGian() }
    println("\n==> MẢNG SAU KHI TỐI GIẢN:")
    inMang(arrToiGian)

    // 4) Tính tổng các phân số và in kết quả
    var tong = PhanSo(0, 1)
    for (ps in arr) tong += ps
    println("\n==> TỔNG CÁC PHÂN SỐ (đã tối giản):")
    tong.inPS()

    // 5) Tìm phân số có giá trị lớn nhất và in kết quả
    val maxPS = arr.maxOrNull()
    println("\n==> PHÂN SỐ LỚN NHẤT (theo giá trị):")
    maxPS?.toiGian()?.inPS()

    // 6) Sắp xếp mảng theo thứ tự giảm dần và in ra kết quả
    val arrGiamDan = arr.map { PhanSo(it.tu, it.mau).toiGian() }.sortedDescending()
    println("\n==> MẢNG SẮP XẾP GIẢM DẦN (đã tối giản):")
    inMang(arrGiamDan)
}

// ====== Các hàm tiện ích I/O ======
fun nhapSoLuong(): Int {
    while (true) {
        try {
            print("Nhập số lượng phân số n (> 0): ")
            val n = readln().toInt()
            if (n > 0) return n
            println("  ➤ n phải > 0. Vui lòng nhập lại!\n")
        } catch (e: NumberFormatException) {
            println("  ➤ Vui lòng nhập số nguyên hợp lệ!\n")
        }
    }
}

fun inMang(list: List<PhanSo>) {
    if (list.isEmpty()) { println("(mảng rỗng)"); return }
    list.forEachIndexed { i, ps -> println("  [$i] $ps") }
}
