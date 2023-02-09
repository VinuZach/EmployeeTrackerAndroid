package com.example.employeetracker.database.RoomDataBase

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class CompanyModelRoom
{
    @PrimaryKey(autoGenerate = false)
    var companyID:Int=0


    @ColumnInfo(name = "companyName")
    var companyName:String=""

    @ColumnInfo(name = "companyRepresentative")
    var companyRepresentative:String=""

    @ColumnInfo(name = "modeOFBusiness")
    var modeOfBusiness:String=""


}