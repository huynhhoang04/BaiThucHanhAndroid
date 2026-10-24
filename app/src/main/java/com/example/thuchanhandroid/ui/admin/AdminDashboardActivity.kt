package com.example.thuchanhandroid.ui.admin

import com.example.thuchanhandroid.ui.employee.EmployeeListActivity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.thuchanhandroid.R
import com.example.thuchanhandroid.data.local.AppDatabase
import com.example.thuchanhandroid.data.pref.SessionManager
import com.example.thuchanhandroid.ui.admin.salary.AdminSalaryActivity
import com.example.thuchanhandroid.ui.login.LoginActivity
import com.example.thuchanhandroid.utils.DateTimeUtils
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var tvTotalEmployees: TextView
    private lateinit var tvTotalWorkHoursToday: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        sessionManager = SessionManager(this)

        tvTotalEmployees = findViewById(R.id.tvTotalEmployees)
        tvTotalWorkHoursToday = findViewById(R.id.tvTotalWorkHoursToday)
        val btnNavEmployee: Button = findViewById(R.id.btnNavEmployeeManagement)
        val btnNavSalary: Button = findViewById(R.id.btnNavSalaryManagement)
        val btnLogout: Button = findViewById(R.id.btnLogout)

        btnNavEmployee.setOnClickListener {
            val intent = Intent(this, EmployeeListActivity::class.java)
            startActivity(intent)
        }

        btnNavSalary.setOnClickListener {
            startActivity(Intent(this, AdminSalaryActivity::class.java))
        }

        btnLogout.setOnClickListener {
            sessionManager.clear()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadDashboardStats()
    }

    private fun loadDashboardStats() {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(this@AdminDashboardActivity)
            val (employeeCount, hoursToday) = withContext(Dispatchers.IO) {
                val count = db.employeeDao().getAll().size
                val today = DateTimeUtils.today()
                val hours = db.workLogDao().sumHoursBetween(0, today, today)
                Pair(count, hours)
            }
            tvTotalEmployees.text = employeeCount.toString()
            tvTotalWorkHoursToday.text = String.format("%.1fh", hoursToday)
        }
    }
}