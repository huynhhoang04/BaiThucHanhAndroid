package com.example.thuchanhandroid.data.local

import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.thuchanhandroid.utils.Constants
import com.example.thuchanhandroid.utils.PasswordHasher

object SeedData {

    fun populate(db: SupportSQLiteDatabase) {
        val adminPassword = PasswordHasher.hash("admin123")
        val employeePassword = PasswordHasher.hash("123456")

        db.execSQL(
            "INSERT INTO accounts (id, username, password, role) VALUES " +
                "(1, 'admin', '$adminPassword', '${Constants.Role.ADMIN}'), " +
                "(2, 'nv01', '$employeePassword', '${Constants.Role.EMPLOYEE}'), " +
                "(3, 'nv02', '$employeePassword', '${Constants.Role.EMPLOYEE}')"
        )

        db.execSQL(
            "INSERT INTO employees (id, code, fullname, email, phone, hourly_rate, account_id) VALUES " +
                "(1, 'NV001', 'Nguyen Van A', 'nva@gmail.com', '0901234567', 30000, 2), " +
                "(2, 'NV002', 'Tran Thi B', 'ttb@gmail.com', '0912345678', 35000, 3)"
        )

        db.execSQL(
            "INSERT INTO work_logs (employee_id, work_date, check_in_time, check_out_time, total_hours, status) VALUES " +
                "(1, strftime('%Y-%m-%d', 'now', '-2 days'), '08:00', '17:00', 8.0, '${Constants.WorkLogStatus.COMPLETED}'), " +
                "(1, strftime('%Y-%m-%d', 'now', '-1 day'), '08:00', '17:00', 8.0, '${Constants.WorkLogStatus.COMPLETED}'), " +
                "(2, strftime('%Y-%m-%d', 'now', '-1 day'), '08:30', '17:30', 8.0, '${Constants.WorkLogStatus.COMPLETED}'), " +
                "(1, strftime('%Y-%m-%d', 'now'), '08:00', '16:30', 8.5, '${Constants.WorkLogStatus.COMPLETED}')"
        )
    }
}
