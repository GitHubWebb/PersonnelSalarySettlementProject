package com.personal.salary.kotlin.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.personal.salary.kotlin.db.dao.EmployeeRosterDao
import com.personal.salary.kotlin.manager.EmployeeRosterStore

// 将类注释为带有 RosterCookieStore 类的表（实体）的 Room 数据库
@Database(entities = [EmployeeRosterStore::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class EmployeeRosterRoomDatabase : RoomDatabase() {

    abstract fun empDao(): EmployeeRosterDao

    companion object {
        // 单例可防止同时打开多个数据库实例（双重校验锁式（Double Check)单例）。
        @Volatile
        private var sINSTANCE: EmployeeRosterRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): EmployeeRosterRoomDatabase {
            //如果 INSTANCE 不为空，则返回它，如果是，则创建数据库
            return sINSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EmployeeRosterRoomDatabase::class.java,
                    "employee_database"
                )

//                    .addMigrations()
                    // 使用这个配置 ,数据库升级会清除以前的数据,重新建表
                    .fallbackToDestructiveMigration().build()
                sINSTANCE = instance
                // 返回实例
                instance
            }
        }
    }
}