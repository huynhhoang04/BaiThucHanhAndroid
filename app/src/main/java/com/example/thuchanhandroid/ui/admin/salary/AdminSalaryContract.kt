package com.example.thuchanhandroid.ui.admin.salary

import com.example.thuchanhandroid.data.local.entity.EmployeeEntity
import com.example.thuchanhandroid.data.local.entity.SalaryEntity

interface AdminSalaryContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun showSalaryList(salaries: List<Pair<SalaryEntity, EmployeeEntity>>)
        fun showEmpty()
        fun showSuccess(message: String)
        fun showError(message: String)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadSalaries(month: Int, year: Int)
        fun generateAndUpsertSalaries(month: Int, year: Int)
        fun markAsPaid(salary: SalaryEntity)
    }
}
