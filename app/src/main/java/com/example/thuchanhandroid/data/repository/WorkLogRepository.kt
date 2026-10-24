package com.example.thuchanhandroid.data.repository

import com.example.thuchanhandroid.data.local.dao.WorkLogDao
import com.example.thuchanhandroid.data.local.entity.WorkLogEntity
import com.example.thuchanhandroid.utils.Constants
import com.example.thuchanhandroid.utils.DateTimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface WorkLogRepository {
    suspend fun checkIn(employeeId: Long): Result<WorkLogEntity>
    suspend fun checkOut(employeeId: Long): Result<WorkLogEntity>
    suspend fun getHistory(employeeId: Long, month: Int, year: Int): List<WorkLogEntity>
    suspend fun monthlyTotalHours(employeeId: Long, month: Int, year: Int): Double
}

class WorkLogRepositoryImpl(
    private val workLogDao: WorkLogDao
) : WorkLogRepository {

    override suspend fun checkIn(employeeId: Long): Result<WorkLogEntity> =
        withContext(Dispatchers.IO) {
            val today = DateTimeUtils.today()
            if (workLogDao.findByEmployeeAndDate(employeeId, today) != null) {
                return@withContext Result.failure(Exception("Bạn đã check-in hôm nay"))
            }
            val workLog = WorkLogEntity(
                employeeId = employeeId,
                workDate = today,
                checkInTime = DateTimeUtils.nowTime(),
                status = Constants.WorkLogStatus.WORKING
            )
            val id = workLogDao.insert(workLog)
            Result.success(workLog.copy(id = id))
        }

    override suspend fun checkOut(employeeId: Long): Result<WorkLogEntity> =
        withContext(Dispatchers.IO) {
            val today = DateTimeUtils.today()
            val workLog = workLogDao.findByEmployeeAndDate(employeeId, today)
                ?: return@withContext Result.failure(Exception("Chưa check-in hôm nay"))
            if (workLog.checkOutTime != null) {
                return@withContext Result.failure(Exception("Đã check-out hôm nay"))
            }
            val checkOutTime = DateTimeUtils.nowTime()
            val totalHours = DateTimeUtils.hoursBetween(workLog.checkInTime.orEmpty(), checkOutTime)
            if (totalHours <= 0.0) {
                return@withContext Result.failure(Exception("Giờ check-out phải sau giờ check-in"))
            }
            val completed = workLog.copy(
                checkOutTime = checkOutTime,
                totalHours = totalHours,
                status = Constants.WorkLogStatus.COMPLETED
            )
            workLogDao.update(completed)
            Result.success(completed)
        }

    override suspend fun getHistory(employeeId: Long, month: Int, year: Int): List<WorkLogEntity> =
        withContext(Dispatchers.IO) {
            val (from, to) = DateTimeUtils.monthRange(month, year)
            workLogDao.findByEmployeeBetween(employeeId, from, to)
        }

    override suspend fun monthlyTotalHours(employeeId: Long, month: Int, year: Int): Double =
        withContext(Dispatchers.IO) {
            val (from, to) = DateTimeUtils.monthRange(month, year)
            workLogDao.sumHoursBetween(employeeId, from, to)
        }
}
