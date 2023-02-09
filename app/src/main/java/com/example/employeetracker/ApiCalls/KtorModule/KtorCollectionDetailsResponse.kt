package com.example.employeetracker.ApiCalls.KtorModule

import com.example.employeetracker.ApiCalls.modelClass.CollectionDetailsResponse
import kotlinx.serialization.Serializable


@Serializable
class KtorCollectionDetailsResponse(override val errorResponse: String="",override val companyId: Int=0, override val companyName: String="",
    override val companyRepresentative: String="", override val remark: String="", override val status1: Int=0,
    override val previousCollectionId: Int=0, override val feasibility: Int=0) : CollectionDetailsResponse()
