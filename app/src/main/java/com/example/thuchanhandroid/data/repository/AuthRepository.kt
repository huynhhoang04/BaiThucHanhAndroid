package com.example.thuchanhandroid.data.repository

import com.example.thuchanhandroid.data.local.dao.AccountDao
import com.example.thuchanhandroid.data.local.dao.EmployeeDao
import com.example.thuchanhandroid.data.local.entity.AccountEntity
import com.example.thuchanhandroid.data.local.entity.EmployeeEntity
import com.example.thuchanhandroid.utils.Constants
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
            val u = username.trim()
            var account = accountDao.findByUsername(u)

            // Auto-seed default accounts if database was created empty
            if (account == null && accountDao.count() == 0) {
                seedDefaultAccounts()
                account = accountDao.findByUsername(u)
            }

            if (account == null) {
                return@withContext Result.failure(Exception("Tài khoản không tồn tại. Thử: admin/admin123 hoặc nv01/123456"))
            }
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

    private suspend fun seedDefaultAccounts() {
        val adminPassword = PasswordHasher.hash("admin123")
        val employeePassword = PasswordHasher.hash("123456")

        val adminAccId = accountDao.insert(
            AccountEntity(
                username = "admin",
                password = adminPassword,
                role = Constants.Role.ADMIN
            )
        )
        val emp1AccId = accountDao.insert(
            AccountEntity(
                username = "nv01",
                password = employeePassword,
                role = Constants.Role.EMPLOYEE
            )
        )
        val emp2AccId = accountDao.insert(
            AccountEntity(
                username = "nv02",
                password = employeePassword,
                role = Constants.Role.EMPLOYEE
            )
        )

        employeeDao.insert(
            EmployeeEntity(
                code = "NV001",
                fullname = "Nguyễn Văn A",
                email = "nva@gmail.com",
                phone = "0901234567",
                hourlyRate = 50000.0,
                accountId = emp1AccId
            )
        )
        employeeDao.insert(
            EmployeeEntity(
                code = "NV002",
                fullname = "Trần Thị B",
                email = "ttb@gmail.com",
                phone = "0912345678",
                hourlyRate = 60000.0,
                accountId = emp2AccId
            )
        )
    }
}
