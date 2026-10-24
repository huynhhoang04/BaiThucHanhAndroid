package com.example.thuchanhandroid.ui.employee

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.thuchanhandroid.data.local.AppDatabase
import com.example.thuchanhandroid.data.local.entity.WorkLogEntity
import com.example.thuchanhandroid.data.repository.WorkLogRepositoryImpl
import com.example.thuchanhandroid.utils.DateTimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AttendanceViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val workLogRepository = WorkLogRepositoryImpl(db.workLogDao())
    private val accountDao = db.accountDao()
    private val employeeDao = db.employeeDao()

    private var employeeId: Long = -1L

    private val _operationResult = MutableLiveData<Result<WorkLogEntity>>()
    val operationResult: LiveData<Result<WorkLogEntity>> get() = _operationResult

    private val _todayLog = MutableLiveData<WorkLogEntity?>()
    val todayLog: LiveData<WorkLogEntity?> get() = _todayLog

    private val _historyList = MutableLiveData<List<WorkLogEntity>>()
    val historyList: LiveData<List<WorkLogEntity>> get() = _historyList

    private val _monthlyHours = MutableLiveData<Double>()
    val monthlyHours: LiveData<Double> get() = _monthlyHours

    fun initEmployee(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val account = accountDao.findByUsername(username)
            if (account != null) {
                val employee = employeeDao.findByAccountId(account.id)
                if (employee != null) {
                    employeeId = employee.id
                    loadTodayStatus()
                    loadMonthlyData(DateTimeUtils.currentMonth(), DateTimeUtils.currentYear())
                }
            }
        }
    }

    fun loadTodayStatus() {
        if (employeeId == -1L) return
        viewModelScope.launch(Dispatchers.IO) {
            val today = DateTimeUtils.today()
            val log = db.workLogDao().findByEmployeeAndDate(employeeId, today)
            _todayLog.postValue(log)
        }
    }

    fun checkIn() {
        if (employeeId == -1L) {
            _operationResult.postValue(Result.failure(Exception("Không tìm thấy thông tin nhân viên")))
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val result = workLogRepository.checkIn(employeeId)
            _operationResult.postValue(result)
            if (result.isSuccess) {
                loadTodayStatus()
                loadMonthlyData(DateTimeUtils.currentMonth(), DateTimeUtils.currentYear())
            }
        }
    }

    fun checkOut() {
        if (employeeId == -1L) {
            _operationResult.postValue(Result.failure(Exception("Không tìm thấy thông tin nhân viên")))
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val result = workLogRepository.checkOut(employeeId)
            _operationResult.postValue(result)
            if (result.isSuccess) {
                loadTodayStatus()
                loadMonthlyData(DateTimeUtils.currentMonth(), DateTimeUtils.currentYear())
            }
        }
    }

    fun loadMonthlyData(month: Int, year: Int) {
        if (employeeId == -1L) return
        viewModelScope.launch(Dispatchers.IO) {
            val history = workLogRepository.getHistory(employeeId, month, year)
            val totalHours = workLogRepository.monthlyTotalHours(employeeId, month, year)
            _historyList.postValue(history)
            _monthlyHours.postValue(totalHours)
        }
    }
}
