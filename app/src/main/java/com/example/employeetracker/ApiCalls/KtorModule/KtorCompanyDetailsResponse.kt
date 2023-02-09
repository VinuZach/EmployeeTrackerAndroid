package com.example.employeetracker.ApiCalls.KtorModule

import com.example.employeetracker.ApiCalls.modelClass.CompanyDetailsResponse
import kotlinx.serialization.Serializable

@Serializable
class KtorCompanyDetailsResponse(override var companyRepresentative: String, override var companyName: String, override var status: Int,
    override var id: Int, override var phoneNumber: String, override var primaryBusinessMode: String, override var locationName: String) :
    CompanyDetailsResponse()
