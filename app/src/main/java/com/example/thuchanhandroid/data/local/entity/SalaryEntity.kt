package com.example.thuchanhandroid.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "salaries",
    foreignKeys = [
        ForeignKey(
            entity = EmployeeEntity::class,
            parentColumns = ["id"],
            childColumns = ["employee_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["employee_id"]),
        Index(value = ["employee_id", "month", "year"], unique = true)
    ]
)
data class SalaryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "employee_id")
    val employeeId: Long,
    val month: Int,
    val year: Int,
    @ColumnInfo(name = "total_hours")
    val totalHours: Double,
    @ColumnInfo(name = "overtime_hours")
    val overtimeHours: Double = 0.0,
    @ColumnInfo(name = "total_salary")
    val totalSalary: Double,
    val status: String
)
