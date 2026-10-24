package com.example.thuchanhandroid

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.thuchanhandroid.data.local.AppDatabase
import com.example.thuchanhandroid.data.pref.SessionManager
import com.example.thuchanhandroid.ui.admin.AdminDashboardActivity
import com.example.thuchanhandroid.ui.employee.EmployeeDashboardActivity
import com.example.thuchanhandroid.ui.login.LoginActivity
import com.example.thuchanhandroid.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scope.launch {
            withContext(Dispatchers.IO) {
                AppDatabase.getInstance(this@MainActivity).accountDao().count()
            }
            val session = SessionManager(this@MainActivity)
            val intent = when {
                !session.isLoggedIn ->
                    Intent(this@MainActivity, LoginActivity::class.java)
                session.role == Constants.Role.ADMIN ->
                    Intent(this@MainActivity, AdminDashboardActivity::class.java)
                else ->
                    Intent(this@MainActivity, EmployeeDashboardActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
