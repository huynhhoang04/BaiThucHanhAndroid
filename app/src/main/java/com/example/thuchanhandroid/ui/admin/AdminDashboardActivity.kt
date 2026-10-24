package com.example.thuchanhandroid.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.thuchanhandroid.R
import com.example.thuchanhandroid.data.pref.SessionManager
import com.example.thuchanhandroid.ui.admin.salary.AdminSalaryActivity
import com.example.thuchanhandroid.ui.login.LoginActivity
import com.google.android.material.button.MaterialButton

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val session = SessionManager(this)
        findViewById<TextView>(R.id.tvWelcome).text =
            getString(R.string.welcome_admin, session.username.orEmpty())

        findViewById<MaterialButton>(R.id.btnAdminSalary).setOnClickListener {
            startActivity(Intent(this, AdminSalaryActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.btnLogout).setOnClickListener {
            session.clear()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
