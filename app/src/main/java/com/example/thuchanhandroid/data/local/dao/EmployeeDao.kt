package com.example.thuchanhandroid.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.thuchanhandroid.data.local.entity.EmployeeEntity

@Dao
interface EmployeeDao {

    @Insert
    suspend fun insert(employee: EmployeeEntity): Long

    @Update
    suspend fun update(employee: EmployeeEntity)

    @Delete
    suspend fun delete(employee: EmployeeEntity)

    @Query("SELECT * FROM employees WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): EmployeeEntity?

    @Query("SELECT * FROM employees WHERE code = :code LIMIT 1")
    suspend fun findByCode(code: String): EmployeeEntity?

    @Query("SELECT * FROM employees WHERE account_id = :accountId LIMIT 1")
    suspend fun findByAccountId(accountId: Long): EmployeeEntity?

    @Query("SELECT * FROM employees ORDER BY fullname")
    suspend fun getAll(): List<EmployeeEntity>

    @Query(
        "SELECT * FROM employees WHERE fullname LIKE '%' || :query || '%' " +
            "OR code LIKE '%' || :query || '%' ORDER BY fullname"
    )
    suspend fun search(query: String): List<EmployeeEntity>
}
