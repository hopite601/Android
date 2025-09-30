import kotlin.math.abs

class PhanSo(var tu: Int, var mau: Int) : Comparable<PhanSo> {


    // So sanh voi phan so khac: -1, 0, 1
    override fun compareTo(other: PhanSo): Int {
        val left  = tu.toLong() * other.mau.toLong() // nhan cheo len so sanh 2 phan so
        val right = other.tu.toLong() * mau.toLong()
        return left.compareTo(right)
    }

    // Tinh tong voi mot phan so khac (tra ve phan so moi da toi gian)
    operator fun plus(other: PhanSo): PhanSo {
        val num = tu.toLong() * other.mau.toLong() + other.tu.toLong() * mau.toLong()
        val den = mau.toLong() * other.mau.toLong()
        // Ep ve Int (gia su khong tran trong pham vi bai tap)
        return PhanSo(num.toInt(), den.toInt()).toiGian()
    }

    // In phan so dang "tu/mau"
    fun inPS() = println(this.toString())

    // Toi gian (rut gon) phan so, giu mau duong
    fun toiGian(): PhanSo {
        if (mau < 0) { tu = -tu; mau = -mau } // doi dau neu mau am
        val g = gcd(abs(tu), abs(mau)) 
        if (g != 0) { tu /= g; mau /= g }
        return this
    }



    override fun toString(): String = "$tu/$mau"

    companion object {
        // nhap 1 phan so tu ban phim, bat nhap lai neu tu hoac mau = 0
        fun nhap(): PhanSo {
            while (true) {
                try {
                    print("  Nhap tu so (so nguyen, != 0): ")
                    val tu = readln().toInt()
                    print("  Nhap mau so (so nguyen, != 0): ")
                    val mau = readln().toInt()

                    if (tu == 0 || mau == 0) {
                        println("  -> Tu so va mau so deu phai khac 0. Vui long nhap lai!\n")
                        continue
                    }

                    // Chuan hoa mau duong; khong rut gon o buoc nhap
                    val (ntu, nmau) = if (mau < 0) -tu to -mau else tu to mau
                    return PhanSo(ntu, nmau)
                } catch (e: NumberFormatException) {
                    println("  -> Vui long nhap so nguyen hop le!\n")
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
    // 1) Nhap mang phan so
    val n = nhapSoLuong()
    val arr = MutableList(n) { idx ->
        println("Nhap phan so thu ${idx + 1}:")
        PhanSo.nhap()
    }

    // 2) In ra mang vua nhap
    println("\n==> MANG PHAN SO VUA NHAP:")
    inMang(arr)

    // 3) Toi gian tung phan tu va in ket qua
    val arrToiGian = arr.map { PhanSo(it.tu, it.mau).toiGian() }
    println("\n==> MANG SAU KHI TOI GIAN:")
    inMang(arrToiGian)

    // 4) Tinh tong cac phan so va in ket qua
    var tong = PhanSo(0, 1)
    for (ps in arr) tong += ps
    println("\n==> TONG CAC PHAN SO (da toi gian):")
    tong.inPS()

    // 5) Tim phan so co gia tri lon nhat va in ket qua
    val maxPS = arr.maxOrNull()
    println("\n==> PHAN SO LON NHAT (theo gia tri):")
    maxPS?.toiGian()?.inPS()

    // 6) Sap xep mang theo thu tu giam dan va in ra ket qua
    val arrGiamDan = arr.map { PhanSo(it.tu, it.mau).toiGian() }.sortedDescending()
    println("\n==> MANG SAP XEP GIAM DAN (da toi gian):")
    inMang(arrGiamDan)
}

// ====== Cac ham tien ich I/O ======
fun nhapSoLuong(): Int {
    while (true) {
        try {
            print("Nhap so luong phan so n (> 0): ")
            val n = readln().toInt()
            if (n > 0) return n
            println("  -> n phai > 0. Vui long nhap lai!\n")
        } catch (e: NumberFormatException) {
            println("  -> Vui long nhap so nguyen hop le!\n")
        }
    }
}

fun inMang(list: List<PhanSo>) {
    if (list.isEmpty()) { println("(mang rong)"); return }
    list.forEachIndexed { i, ps -> println("  [$i] $ps") }
}
