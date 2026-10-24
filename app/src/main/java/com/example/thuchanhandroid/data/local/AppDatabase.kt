package com.example.thuchanhandroid.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.thuchanhandroid.data.local.dao.AccountDao
import com.example.thuchanhandroid.data.local.dao.EmployeeDao
import com.example.thuchanhandroid.data.local.dao.SalaryDao
import com.example.thuchanhandroid.data.local.dao.WorkLogDao
import com.example.thuchanhandroid.data.local.entity.AccountEntity
import com.example.thuchanhandroid.data.local.entity.EmployeeEntity
import com.example.thuchanhandroid.data.local.entity.SalaryEntity
import com.example.thuchanhandroid.data.local.entity.WorkLogEntity

@Database(
    entities = [
        AccountEntity::class,
        EmployeeEntity::class,
        WorkLogEntity::class,
        SalaryEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao

    abstract fun employeeDao(): EmployeeDao

    abstract fun workLogDao(): WorkLogDao

    abstract fun salaryDao(): SalaryDao

    companion object {
        private const val DATABASE_NAME = "salary_management.db"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: build(context).also { instance = it }
            }
        }

        private fun build(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration(true)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        SeedData.populate(db)
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        SeedData.checkAndPopulate(db)
                    }
                })
                .build()
        }
    }
}
