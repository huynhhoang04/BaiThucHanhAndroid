package com.example.thuchanhandroid.ui.login

interface LoginContract {

    interface View {
        fun showLoading(show: Boolean)
        fun showError(message: String)
        fun navigateToAdmin()
        fun navigateToEmployee()
    }

    interface Presenter {
        fun login(username: String, password: String)
        fun onDestroy()
    }
}
