package com.example.thuchanhandroid.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "work_logs",
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
        Index(value = ["employee_id", "work_date"], unique = true)
    ]
)
data class WorkLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "employee_id")
    val employeeId: Long,
    @ColumnInfo(name = "work_date")
    val workDate: String,
    @ColumnInfo(name = "check_in_time")
    val checkInTime: String? = null,
    @ColumnInfo(name = "check_out_time")
    val checkOutTime: String? = null,
    @ColumnInfo(name = "total_hours")
    val totalHours: Double = 0.0,
    val status: String
)
