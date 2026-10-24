package com.example.thuchanhandroid.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.thuchanhandroid.data.local.entity.WorkLogEntity

@Dao
interface WorkLogDao {

    @Insert
    suspend fun insert(workLog: WorkLogEntity): Long

    @Update
    suspend fun update(workLog: WorkLogEntity)

    @Query("SELECT * FROM work_logs WHERE employee_id = :employeeId AND work_date = :date LIMIT 1")
    suspend fun findByEmployeeAndDate(employeeId: Long, date: String): WorkLogEntity?

    @Query(
        "SELECT * FROM work_logs WHERE employee_id = :employeeId " +
            "AND work_date BETWEEN :from AND :to ORDER BY work_date DESC"
    )
    suspend fun findByEmployeeBetween(employeeId: Long, from: String, to: String): List<WorkLogEntity>

    @Query(
        "SELECT COALESCE(SUM(total_hours), 0) FROM work_logs WHERE employee_id = :employeeId " +
            "AND work_date BETWEEN :from AND :to"
    )
    suspend fun sumHoursBetween(employeeId: Long, from: String, to: String): Double

    @Query(
        "SELECT COALESCE(SUM(total_hours), 0) FROM work_logs WHERE work_date BETWEEN :from AND :to"
    )
    suspend fun sumTotalHoursBetween(from: String, to: String): Double

    @Query("SELECT * FROM work_logs WHERE employee_id = :employeeId ORDER BY work_date DESC")
    suspend fun findByEmployee(employeeId: Long): List<WorkLogEntity>
}
