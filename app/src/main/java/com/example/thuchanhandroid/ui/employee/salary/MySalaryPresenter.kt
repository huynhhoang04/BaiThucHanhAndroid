package com.example.thuchanhandroid.ui.employee.salary

import com.example.thuchanhandroid.data.repository.EmployeeRepository
import com.example.thuchanhandroid.data.repository.SalaryRepository
import com.example.thuchanhandroid.data.repository.WorkLogRepository
import com.example.thuchanhandroid.data.local.entity.SalaryEntity
import com.example.thuchanhandroid.utils.Constants
import com.example.thuchanhandroid.utils.DateTimeUtils
import com.example.thuchanhandroid.utils.SalaryCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MySalaryPresenter(
    private val salaryRepository: SalaryRepository,
    private val employeeRepository: EmployeeRepository,
    private val workLogRepository: WorkLogRepository
) : MySalaryContract.Presenter {

    private var view: MySalaryContract.View? = null
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun attachView(view: MySalaryContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
        job.cancel()
    }

    override fun loadSalaries(employeeId: Long) {
        view?.showLoading()
        scope.launch {
            try {
                // Tự động tính/cập nhật bảng lương tháng hiện tại từ WorkLog
                val currentMonth = DateTimeUtils.currentMonth()
                val currentYear = DateTimeUtils.currentYear()
                val employee = employeeRepository.getById(employeeId)

                if (employee != null) {
                    val totalHours = workLogRepository.monthlyTotalHours(employeeId, currentMonth, currentYear)
                    if (totalHours > 0.0) {
                        val otHours = SalaryCalculator.calculateOvertimeHours(totalHours)
                        val totalSalary = SalaryCalculator.calculateTotalSalary(totalHours, employee.hourlyRate, otHours)
                        val currentSalaryRecord = salaryRepository.getByEmployeeMonthYear(employeeId, currentMonth, currentYear)

                        val status = currentSalaryRecord?.status ?: Constants.SalaryStatus.PENDING
                        salaryRepository.upsert(
                            SalaryEntity(
                                id = currentSalaryRecord?.id ?: 0,
                                employeeId = employeeId,
                                month = currentMonth,
                                year = currentYear,
                                totalHours = totalHours,
                                overtimeHours = otHours,
                                totalSalary = totalSalary,
                                status = status
                            )
                        )
                    }
                }

                val list = salaryRepository.getByEmployee(employeeId)
                view?.hideLoading()
                if (list.isEmpty()) {
                    view?.showEmpty()
                } else {
                    view?.showSalaryList(list)
                }
            } catch (e: Exception) {
                view?.hideLoading()
                view?.showError(e.message ?: "Đã xảy ra lỗi khi tải bảng lương")
            }
        }
    }
}
