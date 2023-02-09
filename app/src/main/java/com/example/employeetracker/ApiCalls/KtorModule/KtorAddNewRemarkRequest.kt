package com.example.employeetracker.ApiCalls.KtorModule

import com.example.employeetracker.ApiCalls.modelClass.AddNewRemarkRequest
import kotlinx.serialization.Serializable

@Serializable
class KtorAddNewRemarkRequest(override val newFollowUpDate: String, override val companyId: Int,
    override val feasibility: Int, override val newRemark: String, override val userID: String,
    override val longitude: String, override val latitude: String): AddNewRemarkRequest()
