package com.example.employeetracker

import android.content.Context
import com.example.employeetracker.ApiCalls.ApiMethods
import com.example.employeetracker.ApiCalls.RetrofitModule.KtorModule.KtorMethods
import com.example.employeetracker.database.RoomDataBase.DatabaseBuilder

class Repository(context: Context)
{
    val dataBase=DatabaseBuilder.getInstance(context)

    companion object
    {
        val apiCalls: ApiMethods = KtorMethods()
        val cacheData: DataStoreModule = DataStoreModule()


    }
}