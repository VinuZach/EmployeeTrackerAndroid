package com.example.employeetracker.ApiCalls.RetrofitModule.KtorModule

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.kotlinx.serializer.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

val json = Json {
    encodeDefaults= true
    ignoreUnknownKeys = true
    isLenient = true
}
internal val client= HttpClient(Android)
{

    install(ContentNegotiation) {
        json()
    }
    defaultRequest {
        contentType(ContentType.Application.Json)
        accept(ContentType.Application.Json)
    }


//    install(Logging){
//
//        logger = object: Logger{
//            override fun log(message: String) {
//                Log.d("asdsadsad", "log: $message")
//            }
//        }
//
//    }

}