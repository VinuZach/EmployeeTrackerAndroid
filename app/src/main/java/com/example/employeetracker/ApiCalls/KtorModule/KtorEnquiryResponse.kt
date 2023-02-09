package com.example.employeetracker.ApiCalls.KtorModule

import com.example.employeetracker.ApiCalls.modelClass.EnquiryResponse
import kotlinx.serialization.Serializable

@Serializable
class KtorEnquiryResponse(override var enquiryName: String, override var id: Int): EnquiryResponse()
