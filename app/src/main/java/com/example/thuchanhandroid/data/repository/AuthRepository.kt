package com.example.thuchanhandroid.data.repository

import com.example.thuchanhandroid.data.local.dao.AccountDao
import com.example.thuchanhandroid.data.local.dao.EmployeeDao
import com.example.thuchanhandroid.utils.PasswordHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class LoggedInUser(
    val accountId: Long,
    val username: String,
    val role: String,
    val employeeId: Long?
)

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<LoggedInUser>
}

class AuthRepositoryImpl(
    private val accountDao: AccountDao,
    private val employeeDao: EmployeeDao
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<LoggedInUser> =
        withContext(Dispatchers.IO) {
            val account = accountDao.findByUsername(username.trim())
                ?: return@withContext Result.failure(Exception("Tài khoản không tồn tại"))
            if (!PasswordHasher.verify(password, account.password)) {
                return@withContext Result.failure(Exception("Mật khẩu không đúng"))
            }
            val employeeId = employeeDao.findByAccountId(account.id)?.id
            Result.success(
                LoggedInUser(
                    accountId = account.id,
                    username = account.username,
                    role = account.role,
                    employeeId = employeeId
                )
            )
        }
}
