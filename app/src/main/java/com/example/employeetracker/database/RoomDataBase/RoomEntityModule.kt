package com.example.employeetracker.database.RoomDataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.employeetracker.database.RoomDataBase.roomDoa.CompanyDao


@Database(entities = [CompanyModelRoom::class], version = 1)
abstract class AppDatabase : RoomDatabase()
{
    abstract fun companyDao(): CompanyDao
}



object DatabaseBuilder
{
    private var INSTANCE: AppDatabase? = null
    fun getInstance(context: Context): AppDatabase {
        if (INSTANCE == null) {
            synchronized(AppDatabase::class) {
                INSTANCE = buildRoomDB(context)
            }

        }
        return INSTANCE!!
    }

    private fun buildRoomDB(context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "EmployeeTracker"
                            ).build()
}