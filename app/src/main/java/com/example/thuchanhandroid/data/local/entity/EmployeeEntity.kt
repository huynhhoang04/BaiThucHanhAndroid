package com.example.thuchanhandroid.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "employees",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["code"], unique = true),
        Index(value = ["account_id"], unique = true)
    ]
)
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String,
    val fullname: String,
    val email: String? = null,
    val phone: String? = null,
    @ColumnInfo(name = "hourly_rate")
    val hourlyRate: Double,
    @ColumnInfo(name = "account_id")
    val accountId: Long
)
