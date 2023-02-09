package com.example.employeetracker.ApiCalls.KtorModule

import com.example.employeetracker.ApiCalls.modelClass.PrevCollectionDetails
import com.example.employeetracker.ApiCalls.modelClass.PreviousCollectionDataResponse
import kotlinx.serialization.Serializable

@Serializable
internal class KtorPreviousCollectionDetailsResponse(
    override val collectionDetails:List<KtorPrevCollectionDetails>?=null):PreviousCollectionDataResponse()

@Serializable
class KtorPrevCollectionDetails(override val companyId: Int?, override val enquiry: List<Int>?,
override val id: Int, override val imageFileData: String?,
    override val feasibility: Int?, override val reference: String?, override val remark: String?,
    override val previousCollectionID: Int?, override val followUpDate: String?): PrevCollectionDetails()

