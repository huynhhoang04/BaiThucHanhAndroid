package com.example.thuchanhandroid.ui.login

import com.example.thuchanhandroid.data.pref.SessionManager
import com.example.thuchanhandroid.data.repository.AuthRepository
import com.example.thuchanhandroid.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LoginPresenter(
    private val view: LoginContract.View,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : LoginContract.Presenter {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            view.showError("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu")
            return
        }
        view.showLoading(true)
        scope.launch {
            authRepository.login(username, password)
                .onSuccess { user ->
                    sessionManager.saveSession(user.username, user.role)
                    view.showLoading(false)
                    if (user.role == Constants.Role.ADMIN) {
                        view.navigateToAdmin()
                    } else {
                        view.navigateToEmployee()
                    }
                }
                .onFailure { error ->
                    view.showLoading(false)
                    view.showError(error.message ?: "Đăng nhập thất bại")
                }
        }
    }

    override fun onDestroy() {
        scope.cancel()
    }
}
