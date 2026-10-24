package com.example.thuchanhandroid.ui.employee

import com.example.thuchanhandroid.data.local.entity.EmployeeEntity

interface EmployeeContract {
    interface View {
        fun showEmployees(employees: List<EmployeeEntity>)
        fun showMessage(message: String)
        fun showError(error: String)
    }

    interface Presenter {
        fun loadEmployees()
        fun searchEmployees(query: String)
        fun addEmployee(
            code: String,
            fullname: String,
            email: String,
            phone: String,
            hourlyRateStr: String,
            username: String,
            password: String
        )
        fun updateEmployee(
            currentEmployee: EmployeeEntity,
            code: String,
            fullname: String,
            email: String,
            phone: String,
            hourlyRateStr: String
        )
        fun deleteEmployee(employee: EmployeeEntity)
    }
}