package com.example.employeetracker.ApiCalls

import com.example.employeetracker.ApiCalls.modelClass.EnquiryResponse

interface ApiMethods
{
    fun verifyUserApiCall(userName: String, password: String, apiResponse: ApiResponse)

    fun uploadCollectionDetails(companyId: Int, companyName: String, representative: String, phone: String, location: String,
        businessMode: String, enquiry: List<EnquiryResponse>, feasibility: String, followUpDate: String, remarks: String, reference: String,
        userID:String,latitude:String,longitude:String,
        apiResponse: ApiResponse)

    fun retrieveCollectionList(startDate: String = "", endDate: String = "", apiResponse: ApiResponse)
    fun retrievePreviousCollectionDetails(companyId: Int, apiResponse: ApiResponse)

    fun addNewRemark(companyId: Int,newRemark:String,newFollowUpDate:String,feasibility: Int,  userID:String,latitude:String,longitude:String, apiResponse: ApiResponse)

    fun retrieveCompanyList(apiResponse: ApiResponse)
    fun retrieveEnquiry(apiResponse: ApiResponse)

}

interface ApiResponse
{
    fun onResponseObtained(isSuccess: Boolean, response: Any?)
}