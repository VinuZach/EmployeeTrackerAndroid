package com.example.employeetracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.employeetracker.ApiCalls.ApiResponse
import com.example.employeetracker.ApiCalls.RetrofitModule.KtorModule.KtorMethods
import com.example.employeetracker.ApiCalls.modelClass.UserDetailsResponse
import com.example.employeetracker.DataStoreModule.Companion.IS_ADMIN
import com.example.employeetracker.DataStoreModule.Companion.IS_USER_LOGGED_IN
import com.example.employeetracker.DataStoreModule.Companion.USERNAME
import com.example.employeetracker.DataStoreModule.Companion.USER_ID
import com.example.employeetracker.ui.theme.EmployeeTrackerTheme
import com.example.employeetracker.ui.theme.error_text_color

class MainActivity : ComponentActivity()
{

    lateinit var homePageIntent: () -> Unit
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContent {
            homePageIntent = {

                startActivity(Intent(this, DashBoardActivity::class.java))
                finish()
            }
            EmployeeTrackerTheme(dynamicColor = false) {
                if (Repository.cacheData.getDataWithoutLiveData(this, IS_USER_LOGGED_IN, false)) homePageIntent.invoke()
                else MainComposeView()
            }
        }
    }


    @Composable()
    fun MainComposeView()
    {
        val userName = remember {
            mutableStateOf("")
        }
        val password = remember {
            mutableStateOf("")
        }
        val showLoadingDialog = remember {
            mutableStateOf(false)
        }
        val showErrorMessage = remember {
            mutableStateOf("")
        }
        if (showLoadingDialog.value) LoadingDialog("Verifying user Account")


        Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(vertical = 10.dp)) {

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {

                Column(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), horizontalAlignment = Alignment.CenterHorizontally) {


                    Text(text = "Login", style = MaterialTheme.typography.headlineLarge, fontSize = 30.sp,
                        modifier = Modifier.padding(vertical = 20.dp))

                    Text(text = showErrorMessage.value, color = MaterialTheme.colorScheme.onError)
                    Column {

                        Text(text = "Username", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(vertical = 10.dp))
                        TextField(value = userName.value, onValueChange = {
                            userName.value = it
                        }, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Email,
                            autoCorrect = false),  singleLine =true )

                        Text(text = "Password", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(vertical = 10.dp))
                        TextField(value = password.value, onValueChange = {
                            password.value = it
                        }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            singleLine = true,)

                    }


                    Button(colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = Color.White), onClick = {
                        showLoadingDialog.value = true
                        showErrorMessage.value = ""
                        Repository.apiCalls.verifyUserApiCall(userName.value, password.value, object : ApiResponse
                        {
                            override fun onResponseObtained(isSuccess: Boolean, response: Any?)
                            {
                                showLoadingDialog.value = false


                                if (isSuccess)
                                {
                                    val ssss = response as UserDetailsResponse
                                    Repository.cacheData.saveDataToDataStore<Boolean>(this@MainActivity, IS_USER_LOGGED_IN, true)
                                    Repository.cacheData.saveDataToDataStore<String>(this@MainActivity, USERNAME, ssss.username)
                                    Repository.cacheData.saveDataToDataStore<Boolean>(this@MainActivity, IS_ADMIN, ssss.is_superuser)
                                    Repository.cacheData.saveDataToDataStore<String>(this@MainActivity, USER_ID, ""+ssss.id)
                                    homePageIntent.invoke()
                                }
                                else showErrorMessage.value = response as String

                            }

                        })

                    }, modifier = Modifier.padding(vertical = 40.dp)) {
                        Text(text = "Submit", modifier = Modifier.padding(vertical = 10.dp, horizontal = 30.dp))
                    }
                }
            }

        }


    }


    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview()
    {
        EmployeeTrackerTheme {
            MainComposeView()
        }
    }


    @Composable
    fun LoadingDialog(textMessage: String = "")
    {
        Dialog(onDismissRequest = { }) {
            Row(modifier = Modifier.background(Color.White).fillMaxWidth().padding(vertical = 30.dp),
                horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.padding(horizontal = 10.dp))
                Text(text = textMessage)
            }
        }
    }
}


