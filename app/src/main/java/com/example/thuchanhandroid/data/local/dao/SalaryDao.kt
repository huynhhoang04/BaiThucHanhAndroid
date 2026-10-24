package com.example.thuchanhandroid.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.thuchanhandroid.data.local.entity.SalaryEntity

@Dao
interface SalaryDao {

    @Insert
    suspend fun insert(salary: SalaryEntity): Long

    @Update
    suspend fun update(salary: SalaryEntity)

    @Query(
        "SELECT * FROM salaries WHERE employee_id = :employeeId " +
            "AND month = :month AND year = :year LIMIT 1"
    )
    suspend fun findByEmployeeMonthYear(employeeId: Long, month: Int, year: Int): SalaryEntity?

    @Query("SELECT * FROM salaries WHERE employee_id = :employeeId ORDER BY year DESC, month DESC")
    suspend fun findByEmployee(employeeId: Long): List<SalaryEntity>

    @Query("SELECT * FROM salaries WHERE month = :month AND year = :year ORDER BY employee_id")
    suspend fun findByMonthYear(month: Int, year: Int): List<SalaryEntity>
}
