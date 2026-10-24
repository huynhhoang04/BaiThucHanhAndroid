package com.example.thuchanhandroid.ui.employee

import android.util.Patterns
import com.example.thuchanhandroid.data.local.entity.EmployeeEntity
import com.example.thuchanhandroid.data.repository.EmployeeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class EmployeePresenter(
    private val view: EmployeeContract.View,
    private val repository: EmployeeRepository
) : EmployeeContract.Presenter {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun loadEmployees() {
        scope.launch {
            try {
                val list = repository.getAll()
                view.showEmployees(list)
            } catch (e: Exception) {
                view.showError("Lỗi tải danh sách: ${e.message}")
            }
        }
    }

    override fun searchEmployees(query: String) {
        scope.launch {
            try {
                val list = if (query.isBlank()) repository.getAll() else repository.search(query)
                view.showEmployees(list)
            } catch (e: Exception) {
                view.showError("Lỗi tìm kiếm: ${e.message}")
            }
        }
    }

    override fun addEmployee(
        code: String,
        fullname: String,
        email: String,
        phone: String,
        hourlyRateStr: String,
        username: String,
        password: String
    ) {
        if (code.isBlank() || fullname.isBlank() || username.isBlank() || password.isBlank()) {
            view.showError("Vui lòng điền đủ mã, họ tên, username và password!")
            return
        }
        if (email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view.showError("Email không đúng định dạng!")
            return
        }
        val rate = hourlyRateStr.toDoubleOrNull()
        if (rate == null || rate <= 0) {
            view.showError("Lương theo giờ phải lớn hơn 0!")
            return
        }

        val newEmployee = EmployeeEntity(
            code = code.trim(),
            fullname = fullname.trim(),
            email = email.trim().ifEmpty { null },
            phone = phone.trim().ifEmpty { null },
            hourlyRate = rate,
            accountId = 0
        )

        scope.launch {
            val result = repository.create(newEmployee, username, password)
            result.onSuccess {
                view.showMessage("Thêm nhân viên thành công!")
                loadEmployees()
            }.onFailure {
                view.showError(it.message ?: "Thêm thất bại!")
            }
        }
    }

    override fun updateEmployee(
        currentEmployee: EmployeeEntity,
        code: String,
        fullname: String,
        email: String,
        phone: String,
        hourlyRateStr: String
    ) {
        if (code.isBlank() || fullname.isBlank()) {
            view.showError("Mã và họ tên không được để trống!")
            return
        }
        if (email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view.showError("Email không đúng định dạng!")
            return
        }
        val rate = hourlyRateStr.toDoubleOrNull()
        if (rate == null || rate <= 0) {
            view.showError("Lương theo giờ phải lớn hơn 0!")
            return
        }

        val updated = currentEmployee.copy(
            code = code.trim(),
            fullname = fullname.trim(),
            email = email.trim().ifEmpty { null },
            phone = phone.trim().ifEmpty { null },
            hourlyRate = rate
        )

        scope.launch {
            val result = repository.update(updated)
            result.onSuccess {
                view.showMessage("Cập nhật thành công!")
                loadEmployees()
            }.onFailure {
                view.showError(it.message ?: "Cập nhật thất bại!")
            }
        }
    }

    override fun deleteEmployee(employee: EmployeeEntity) {
        scope.launch {
            val result = repository.delete(employee)
            result.onSuccess {
                view.showMessage("Đã xóa nhân viên: ${employee.fullname}")
                loadEmployees()
            }.onFailure {
                view.showError(it.message ?: "Không thể xóa nhân viên!")
            }
        }
    }
}