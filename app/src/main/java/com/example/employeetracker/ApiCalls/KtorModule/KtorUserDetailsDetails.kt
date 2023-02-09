package com.example.employeetracker.ApiCalls.RetrofitModule.KtorModule

import com.example.employeetracker.ApiCalls.modelClass.UserDetailsResponse
import kotlinx.serialization.Serializable

@Serializable
internal class KtorUserDetailsDetails(override val errorResponse: String = "", override val username: String = "",
    override val is_superuser: Boolean = false, override val companyCount: Int=0, override val phoneNumberListCount: Int=0,
    override val id: Int) :
    UserDetailsResponse()
