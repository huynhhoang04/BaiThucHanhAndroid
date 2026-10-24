package com.example.thuchanhandroid.data.repository

import androidx.room.withTransaction
import com.example.thuchanhandroid.data.local.AppDatabase
import com.example.thuchanhandroid.data.local.entity.AccountEntity
import com.example.thuchanhandroid.data.local.entity.EmployeeEntity
import com.example.thuchanhandroid.utils.Constants
import com.example.thuchanhandroid.utils.PasswordHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface EmployeeRepository {
    suspend fun getAll(): List<EmployeeEntity>
    suspend fun search(query: String): List<EmployeeEntity>
    suspend fun getById(id: Long): EmployeeEntity?
    suspend fun create(employee: EmployeeEntity, username: String, password: String): Result<Long>
    suspend fun update(employee: EmployeeEntity): Result<Unit>
    suspend fun delete(employee: EmployeeEntity): Result<Unit>
}

class EmployeeRepositoryImpl(
    private val db: AppDatabase
) : EmployeeRepository {

    private val employeeDao get() = db.employeeDao()
    private val accountDao get() = db.accountDao()

    override suspend fun getAll(): List<EmployeeEntity> =
        withContext(Dispatchers.IO) { employeeDao.getAll() }

    override suspend fun search(query: String): List<EmployeeEntity> =
        withContext(Dispatchers.IO) { employeeDao.search(query.trim()) }

    override suspend fun getById(id: Long): EmployeeEntity? =
        withContext(Dispatchers.IO) { employeeDao.findById(id) }

    override suspend fun create(
        employee: EmployeeEntity,
        username: String,
        password: String
    ): Result<Long> = withContext(Dispatchers.IO) {
        if (employeeDao.findByCode(employee.code) != null) {
            return@withContext Result.failure(Exception("Mã nhân viên đã tồn tại"))
        }
        if (accountDao.findByUsername(username.trim()) != null) {
            return@withContext Result.failure(Exception("Tên đăng nhập đã tồn tại"))
        }
        runCatching {
            db.withTransaction {
                val accountId = accountDao.insert(
                    AccountEntity(
                        username = username.trim(),
                        password = PasswordHasher.hash(password),
                        role = Constants.Role.EMPLOYEE
                    )
                )
                employeeDao.insert(employee.copy(accountId = accountId))
            }
        }.onFailure {
            return@withContext Result.failure(Exception("Không thể tạo nhân viên: ${it.message}"))
        }
    }

    override suspend fun update(employee: EmployeeEntity): Result<Unit> =
        withContext(Dispatchers.IO) {
            val current = employeeDao.findByCode(employee.code)
            if (current != null && current.id != employee.id) {
                return@withContext Result.failure(Exception("Mã nhân viên đã tồn tại"))
            }
            employeeDao.update(employee)
            Result.success(Unit)
        }

    override suspend fun delete(employee: EmployeeEntity): Result<Unit> =
        withContext(Dispatchers.IO) {
            employeeDao.delete(employee)
            Result.success(Unit)
        }
}
