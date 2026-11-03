package com.anhphu.phoneprefix
import android.app.role.RoleManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {
    private val roleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java)
            if (!roleManager.isRoleHeld(RoleManager.ROLE_CALL_REDIRECTION)) {
                roleLauncher.launch(roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_REDIRECTION))
            }
        }
        val prefs = getSharedPreferences("Config", Context.MODE_PRIVATE)
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(60, 100, 60, 60) }
        val tvStatus = TextView(this).apply { textSize = 18f; setPadding(0, 0, 0, 50) }
        val edtNumber = EditText(this).apply { hint = "Nhập số đích cần chuyển đến..."; inputType = InputType.TYPE_CLASS_PHONE; textSize = 20f; setText(prefs.getString("TARGET_NUMBER", "")) }
        fun updateUI() {
            val enabled = prefs.getBoolean("ENABLED", false)
            val num = prefs.getString("TARGET_NUMBER", "")
            if (enabled && !num.isNullOrBlank()) {
                tvStatus.text = "🟢 ĐANG CHUYỂN HƯỚNG TỚI: $num"; tvStatus.setTextColor(Color.parseColor("#2E7D32"))
            } else {
                tvStatus.text = "⚪ ĐANG TẮT (Gọi bình thường)"; tvStatus.setTextColor(Color.DKGRAY)
            }
        }
        val btnSet = Button(this).apply {
            text = "ĐẶT CẤU HÌNH (BẬT)"; textSize = 16f
            setOnClickListener {
                val num = edtNumber.text.toString().trim()
                if (num.isNotEmpty()) {
                    prefs.edit().putString("TARGET_NUMBER", num).putBoolean("ENABLED", true).apply()
                    updateUI()
                    Toast.makeText(context, "Đã đặt cấu hình chuyển đến: $num", Toast.LENGTH_SHORT).show()
                } else { Toast.makeText(context, "Vui lòng nhập số điện thoại!", Toast.LENGTH_SHORT).show() }
            }
        }
        val btnStop = Button(this).apply {
            text = "TẮT CHUYỂN HƯỚNG (GỌI BÌNH THƯỜNG)"; textSize = 16f
            setOnClickListener { prefs.edit().putBoolean("ENABLED", false).apply(); updateUI(); Toast.makeText(context, "Đã tắt chuyển hướng!", Toast.LENGTH_SHORT).show() }
        }
        layout.addView(tvStatus); layout.addView(edtNumber); layout.addView(btnSet); layout.addView(btnStop)
        setContentView(layout); updateUI()
    }
}
