package com.example.thuchanhandroid.data.repository

import com.example.thuchanhandroid.data.local.dao.SalaryDao
import com.example.thuchanhandroid.data.local.entity.SalaryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface SalaryRepository {
    suspend fun getByEmployee(employeeId: Long): List<SalaryEntity>
    suspend fun getByEmployeeMonthYear(employeeId: Long, month: Int, year: Int): SalaryEntity?
    suspend fun getByMonthYear(month: Int, year: Int): List<SalaryEntity>
    suspend fun upsert(salary: SalaryEntity)
}

class SalaryRepositoryImpl(
    private val salaryDao: SalaryDao
) : SalaryRepository {

    override suspend fun getByEmployee(employeeId: Long): List<SalaryEntity> =
        withContext(Dispatchers.IO) { salaryDao.findByEmployee(employeeId) }

    override suspend fun getByEmployeeMonthYear(
        employeeId: Long,
        month: Int,
        year: Int
    ): SalaryEntity? = withContext(Dispatchers.IO) {
        salaryDao.findByEmployeeMonthYear(employeeId, month, year)
    }

    override suspend fun getByMonthYear(month: Int, year: Int): List<SalaryEntity> =
        withContext(Dispatchers.IO) { salaryDao.findByMonthYear(month, year) }

    override suspend fun upsert(salary: SalaryEntity) {
        withContext(Dispatchers.IO) {
            val existing = salaryDao.findByEmployeeMonthYear(salary.employeeId, salary.month, salary.year)
            if (existing == null) {
                salaryDao.insert(salary)
            } else {
                salaryDao.update(salary.copy(id = existing.id))
            }
        }
    }
}
