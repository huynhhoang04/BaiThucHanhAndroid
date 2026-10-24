package com.example.thuchanhandroid.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.thuchanhandroid.R
import com.example.thuchanhandroid.data.local.AppDatabase
import com.example.thuchanhandroid.data.pref.SessionManager
import com.example.thuchanhandroid.data.repository.AuthRepositoryImpl
import com.example.thuchanhandroid.ui.admin.AdminDashboardActivity
import com.example.thuchanhandroid.ui.employee.EmployeeDashboardActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity(), LoginContract.View {

    private lateinit var presenter: LoginContract.Presenter
    private lateinit var tilUsername: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var btnLogin: MaterialButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tilUsername = findViewById(R.id.tilUsername)
        tilPassword = findViewById(R.id.tilPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar)

        val database = AppDatabase.getInstance(this)
        presenter = LoginPresenter(
            this,
            AuthRepositoryImpl(database.accountDao(), database.employeeDao()),
            SessionManager(this)
        )

        btnLogin.setOnClickListener {
            presenter.login(
                tilUsername.editText?.text?.toString().orEmpty(),
                tilPassword.editText?.text?.toString().orEmpty()
            )
        }
    }

    override fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !show
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun navigateToAdmin() {
        startActivity(Intent(this, AdminDashboardActivity::class.java))
        finish()
    }

    override fun navigateToEmployee() {
        startActivity(Intent(this, EmployeeDashboardActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}
