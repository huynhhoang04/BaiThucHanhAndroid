package com.example.thuchanhandroid.ui.employee

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thuchanhandroid.R
import com.example.thuchanhandroid.data.pref.SessionManager
import com.example.thuchanhandroid.ui.employee.salary.MySalaryActivity
import com.example.thuchanhandroid.ui.login.LoginActivity
import com.google.android.material.button.MaterialButton

class EmployeeDashboardActivity : AppCompatActivity() {

    private val viewModel: AttendanceViewModel by viewModels()
    private lateinit var workLogAdapter: WorkLogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_dashboard)

        val session = SessionManager(this)
        val username = session.username.orEmpty()

        findViewById<TextView>(R.id.tvWelcome).text =
            getString(R.string.welcome_employee, username)

        val btnCheckIn = findViewById<Button>(R.id.btnCheckIn)
        val btnCheckOut = findViewById<Button>(R.id.btnCheckOut)
        val tvMonthlyHours = findViewById<TextView>(R.id.tvMonthlyHours)
        val rvWorkLogs = findViewById<RecyclerView>(R.id.rvWorkLogs)

        rvWorkLogs.layoutManager = LinearLayoutManager(this)
        workLogAdapter = WorkLogAdapter(emptyList())
        rvWorkLogs.adapter = workLogAdapter

        viewModel.initEmployee(username)

        btnCheckIn.setOnClickListener {
            viewModel.checkIn()
        }

        btnCheckOut.setOnClickListener {
            viewModel.checkOut()
        }

        viewModel.operationResult.observe(this) { result ->
            if (result.isSuccess) {
                Toast.makeText(this, "Thành công!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, result.exceptionOrNull()?.message ?: "Thất bại", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.historyList.observe(this) { logs ->
            workLogAdapter.updateData(logs)
        }

        viewModel.monthlyHours.observe(this) { hours ->
            tvMonthlyHours.text = "Tổng giờ làm tháng này: $hours h"
        }

        findViewById<MaterialButton>(R.id.btnMySalary).setOnClickListener {
            startActivity(Intent(this, MySalaryActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.btnLogout).setOnClickListener {
            session.clear()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
