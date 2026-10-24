package com.example.thuchanhandroid.ui.admin.salary

import com.example.thuchanhandroid.data.local.entity.EmployeeEntity
import com.example.thuchanhandroid.data.local.entity.SalaryEntity
import com.example.thuchanhandroid.data.repository.EmployeeRepository
import com.example.thuchanhandroid.data.repository.SalaryRepository
import com.example.thuchanhandroid.data.repository.WorkLogRepository
import com.example.thuchanhandroid.utils.Constants
import com.example.thuchanhandroid.utils.SalaryCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AdminSalaryPresenter(
    private val salaryRepository: SalaryRepository,
    private val employeeRepository: EmployeeRepository,
    private val workLogRepository: WorkLogRepository
) : AdminSalaryContract.Presenter {

    private var view: AdminSalaryContract.View? = null
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun attachView(view: AdminSalaryContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
        job.cancel()
    }

    override fun loadSalaries(month: Int, year: Int) {
        view?.showLoading()
        scope.launch {
            try {
                val salaries = salaryRepository.getByMonthYear(month, year)
                val employees = employeeRepository.getAll().associateBy { it.id }

                val resultList = salaries.mapNotNull { salary ->
                    val emp = employees[salary.employeeId]
                    if (emp != null) salary to emp else null
                }

                view?.hideLoading()
                if (resultList.isEmpty()) {
                    view?.showEmpty()
                } else {
                    view?.showSalaryList(resultList)
                }
            } catch (e: Exception) {
                view?.hideLoading()
                view?.showError(e.message ?: "Lỗi khi tải danh sách bảng lương")
            }
        }
    }

    override fun generateAndUpsertSalaries(month: Int, year: Int) {
        view?.showLoading()
        scope.launch {
            try {
                val employees = employeeRepository.getAll()
                var updatedCount = 0

                for (emp in employees) {
                    val totalHours = workLogRepository.monthlyTotalHours(emp.id, month, year)
                    if (totalHours > 0.0) {
                        val otHours = SalaryCalculator.calculateOvertimeHours(totalHours)
                        val totalSalary = SalaryCalculator.calculateTotalSalary(totalHours, emp.hourlyRate, otHours)
                        val existing = salaryRepository.getByEmployeeMonthYear(emp.id, month, year)

                        val status = existing?.status ?: Constants.SalaryStatus.PENDING
                        salaryRepository.upsert(
                            SalaryEntity(
                                id = existing?.id ?: 0,
                                employeeId = emp.id,
                                month = month,
                                year = year,
                                totalHours = totalHours,
                                overtimeHours = otHours,
                                totalSalary = totalSalary,
                                status = status
                            )
                        )
                        updatedCount++
                    }
                }

                view?.hideLoading()
                if (updatedCount == 0) {
                    view?.showError("Không tìm thấy dữ liệu chấm công tháng $month/$year")
                } else {
                    view?.showSuccess("Đã tính toán & cập nhật bảng lương cho $updatedCount nhân viên")
                }
                loadSalaries(month, year)
            } catch (e: Exception) {
                view?.hideLoading()
                view?.showError(e.message ?: "Lỗi khi tính toán bảng lương")
            }
        }
    }

    override fun markAsPaid(salary: SalaryEntity) {
        scope.launch {
            try {
                val updated = salary.copy(status = Constants.SalaryStatus.PAID)
                salaryRepository.upsert(updated)
                view?.showSuccess("Đã đánh dấu ĐÃ THANH TOÁN")
                loadSalaries(salary.month, salary.year)
            } catch (e: Exception) {
                view?.showError(e.message ?: "Lỗi khi cập nhật trạng thái thanh toán")
            }
        }
    }
}
