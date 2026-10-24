package com.example.thuchanhandroid.ui.employee.salary

import com.example.thuchanhandroid.data.local.entity.SalaryEntity

interface MySalaryContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun showSalaryList(salaries: List<SalaryEntity>)
        fun showEmpty()
        fun showError(message: String)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadSalaries(employeeId: Long)
    }
}
