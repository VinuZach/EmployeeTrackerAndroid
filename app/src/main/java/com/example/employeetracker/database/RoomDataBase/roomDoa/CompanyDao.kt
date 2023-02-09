package com.example.employeetracker.database.RoomDataBase.roomDoa

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.employeetracker.database.RoomDataBase.CompanyModelRoom

@Dao
interface CompanyDao
{

    @Insert
    suspend fun insertAll(companyModelRoom: List<CompanyModelRoom>)

    @Query("SELECT * FROM CompanyModelRoom")
    suspend fun getAll(): List<CompanyModelRoom>
}