package com.example.thuchanhandroid.ui.employee

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.thuchanhandroid.R
import com.example.thuchanhandroid.data.pref.SessionManager
import com.example.thuchanhandroid.ui.login.LoginActivity
import com.google.android.material.button.MaterialButton

class EmployeeDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_dashboard)

        val session = SessionManager(this)
        findViewById<TextView>(R.id.tvWelcome).text =
            getString(R.string.welcome_employee, session.username.orEmpty())

        findViewById<MaterialButton>(R.id.btnLogout).setOnClickListener {
            session.clear()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
