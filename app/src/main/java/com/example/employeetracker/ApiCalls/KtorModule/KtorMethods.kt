package com.example.employeetracker.ApiCalls.RetrofitModule.KtorModule

import android.util.Log
import com.example.employeetracker.ApiCalls.*
import com.example.employeetracker.ApiCalls.KtorModule.*
import com.example.employeetracker.ApiCalls.KtorModule.KtorPreviousCollectionDetailsResponse
import com.example.employeetracker.ApiCalls.modelClass.EnquiryResponse

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.JsonArray
import org.json.JSONArray


class KtorMethods : ApiMethods
{


    fun callApiAsync(apiResponse: ApiResponse, methodCall: suspend () -> Unit)
    {
        runBlocking {
            val job = withTimeoutOrNull(4000) {
                methodCall.invoke()
            }
            if (job == null)
            {
                Log.d("asjdsajd", "callApiAsync: 1111")
                apiResponse.onResponseObtained(false, "Server unreachable")
            }
        }
    }

    override fun verifyUserApiCall(userName: String, password: String, apiResponse: ApiResponse)
    {
        callApiAsync(apiResponse) {
            val responseData: HttpResponse = client.submitForm(url = BASE_URL + verifyUser_url, formParameters = Parameters.build {
                append("userName", userName)
                append("password", password)
            })
            Log.d("asjdsajd", "verifyUserApiCall: "+responseData.status)
            if (responseData.status.value in 200..298)
            {
                val ssss = responseData.body() as KtorUserDetailsDetails
                apiResponse.onResponseObtained(true, ssss)

            }
            else if (responseData.status.value == 299)
            {

                apiResponse.onResponseObtained(false, "Server Error")
            }
            else apiResponse.onResponseObtained(false, "Server Error")
        }
    }

    override fun retrieveEnquiry(apiResponse: ApiResponse)
    {
        callApiAsync(apiResponse)
        {
            val responseData= client.get(BASE_URL+retrieveEnquiry)
            if (responseData.status.value==200)
            {
                val enquiryResponse = responseData.body() as List<KtorEnquiryResponse>

                apiResponse.onResponseObtained(true,enquiryResponse)

            }
            else
                apiResponse.onResponseObtained(false,"server not reachable")
        }
    }
    override fun uploadCollectionDetails(companyId: Int, companyName: String, representative: String, phone: String, location: String,
        businessMode: String, enquiry: List<EnquiryResponse>, feasibility: String, followUpDate: String, remarks: String, reference: String,
        userID:String,latitude:String,longitude:String,
        apiResponse: ApiResponse)
    {

        Log.d("asdsadsad", "uploadCollectionDetails: ")
        callApiAsync(apiResponse) {

            val responseData: HttpResponse =
                client.submitForm(url = BASE_URL + uploadCollectionDetails_url, formParameters = Parameters.build {
                    append("companyID", "" + companyId)
                    append("companyName", companyName)
                    append("representative", representative)
                    append("location", location)
                    append("feasibility", feasibility)
                    append("followUpDate", followUpDate)
                    append("remarks", remarks)
                    append("reference", reference)
                    append("userID", userID)
                    append("latitude", latitude)
                    append("longitude", longitude)
                    append("phone", phone)
                    append("businessMode", businessMode)
                    append("enquiry", ""+enquiry)
                })
            if (responseData.status.value in 200..298)
            {
                apiResponse.onResponseObtained(true,"")
            }
            else
                apiResponse.onResponseObtained(false,"")

            Log.d("asdsadsad", "uploadCollectionDetails: " + responseData.status)
        }
    }

    override fun retrieveCollectionList(startDate: String, endDate: String, apiResponse: ApiResponse)
    {
        callApiAsync(apiResponse) {
            val responseData = client.get(BASE_URL + retrieveCollectionList) {
                url {
                    parameters.append("startDate", startDate)
                    parameters.append("endDate", endDate)
                    parameters.append("selectedFeasibility", "-1")
                }
            }

            if (responseData.status.value in 200..298)
            {
                val stringBody: String = responseData.body()
                val jsonArray = JSONArray(stringBody)
                val collectionDetailsList = mutableListOf<KtorCollectionDetailsResponse>()
                for (i in 0..jsonArray.length() - 1)
                {
                    val item = jsonArray.getJSONObject(i)
                    collectionDetailsList.add(KtorCollectionDetailsResponse(companyId = item.get("companyId").toString().toInt(),
                        companyName = item.get("companyName").toString(),
                        companyRepresentative = item.get("companyRepresentative").toString(), remark = item.get("remark").toString(),
                        previousCollectionId = item.get("previousCollectionId").toString().toInt(),
                        status1 = item.get("status1").toString().toInt(), feasibility = item.get("feasibility").toString().toInt()))
                }
                apiResponse.onResponseObtained(true, collectionDetailsList)

            }
            else if (responseData.status.value == 299)
            {
                val ssss = responseData.body() as KtorCollectionDetailsResponse
                apiResponse.onResponseObtained(false, ssss)
            }
            else apiResponse.onResponseObtained(false, KtorCollectionDetailsResponse("Server Error"))


        }
    }

    override fun retrieveCompanyList(apiResponse: ApiResponse)
    {
        callApiAsync(apiResponse)
        {
            val responseData= client.get(BASE_URL+retrieveCompanyList)

           if (responseData.status.value==200)
           {
               val companyDetailsList = responseData.body() as List<KtorCompanyDetailsResponse>

               apiResponse.onResponseObtained(true,companyDetailsList)

           }
            else
               apiResponse.onResponseObtained(false,"server not reachable")


        }
    }

    override fun addNewRemark(companyId: Int, newRemark: String, newFollowUpDate: String, feasibility: Int,   userID:String,latitude:String,longitude:String,apiResponse: ApiResponse)
    {
        callApiAsync(apiResponse)
        {
            val responseData= client.post(BASE_URL+ addNewRemark)
            {
              setBody(KtorAddNewRemarkRequest(newFollowUpDate,companyId,feasibility,newRemark,userID,longitude,latitude))
            }
            Log.d("vsdrfedr", "addNewRemark: "+responseData.status)
            if (responseData.status.value==200)
                apiResponse.onResponseObtained(true,null)
            else
                apiResponse.onResponseObtained(true,"Remark Not Added")
        }
    }


    override fun retrievePreviousCollectionDetails(companyId: Int, apiResponse: ApiResponse)
    {
        callApiAsync(apiResponse) {
            val responseData = client.get(BASE_URL + getPreviousCollectionDetails)
            {
               url {
                   parameters.append("companyId", ""+companyId)
               }
            }

            if (responseData.status.value in 200..298)
            {
                val ssss = responseData.body() as KtorPreviousCollectionDetailsResponse
                apiResponse.onResponseObtained(true, ssss)

            }
            else if (responseData.status.value == 299)
            {
                val ssss = responseData.body() as KtorPreviousCollectionDetailsResponse
                apiResponse.onResponseObtained(false, ssss)
            }

            else apiResponse.onResponseObtained(false, KtorCollectionDetailsResponse("Server Error"))
            Log.d("asdasdasddccc", "retrieveCollectionList: " + responseData.status)

        }
    }
}