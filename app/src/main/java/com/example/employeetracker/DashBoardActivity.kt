package com.example.employeetracker

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.app.ActivityCompat
import com.example.employeetracker.ApiCalls.ApiResponse
import com.example.employeetracker.ApiCalls.modelClass.*
import com.example.employeetracker.DataStoreModule.Companion.USERNAME
import com.example.employeetracker.DataStoreModule.Companion.USER_ID
import com.example.employeetracker.ui.theme.EmployeeTrackerTheme
import com.example.employeetracker.ui.theme.Pink40
import com.example.employeetracker.ui.theme.labelColor
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.*

class DashBoardActivity : ComponentActivity()
{
    var latitude by mutableStateOf("")
    var longitude by mutableStateOf("")
    var isButtonEnabled by mutableStateOf(false)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setContent {
            EmployeeTrackerTheme {
                val displayContent = remember {
                    mutableStateOf(ContentType.NEW_COLLECTION_DETAILS)
                }
                if (displayContent.value == ContentType.NEW_COLLECTION_DETAILS) MainComposeView(displayContent)
                else CollectionDetailsMainPage(displayContent)
            }
        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

            Log.d("casjek", "MainComposeView: " + isGranted)
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            )
            {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null)
                        {

                            latitude = "" + location.latitude
                            longitude = "" + location.longitude
                            isButtonEnabled=true

                            Log.d("vrujjr", "onCreate: "+latitude)
                            Log.d("vrujjr", "onCreate: "+longitude)
                        }

                    }

            }

        }
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }


    @Preview
    @Composable
    private fun CollectionDetailsMainPage(displayContent: MutableState<String>? = null)
    {
        var previousCollectionData = remember {
            mutableListOf<PrevCollectionDetails>()
        }

        val selectedDate = remember {
            mutableStateOf("")
        }
        getCurrentDate()
        val current = mYear.toString() + "-" + mMonth.toString() + "-" + mDay.toString()
        selectedDate.value = current

        Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp)) {
            Icon(imageVector = Icons.Filled.ArrowBack, "", tint = Color.White, modifier = Modifier.clickable {
                displayContent?.let {
                    if (it.value == ContentType.FOLLOWUP_COLLECTION) it.value = ContentType.NEW_COLLECTION_DETAILS
                    else if (it.value == ContentType.COLLECTION_HISTORY) it.value = ContentType.FOLLOWUP_COLLECTION
                }

            })
            val selectedCompany = remember {
                mutableStateOf<CollectionDetailsResponse>(CollectionDetailsResponse())
            }
            if (displayContent?.value == ContentType.FOLLOWUP_COLLECTION)
            {
                Text(text = "Followup Appointment".uppercase(), color = MaterialTheme.colorScheme.tertiary, textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.bodyMedium)


                var collectionDetailsList = remember {
                    mutableListOf<CollectionDetailsResponse>()
                }

                Repository.apiCalls.retrieveCollectionList(apiResponse = object : ApiResponse
                {
                    override fun onResponseObtained(isSuccess: Boolean, response: Any?)
                    {
                        if (isSuccess)
                        {
                            collectionDetailsList = (response as List<CollectionDetailsResponse>).toMutableList()

                        }
                        else
                        {
                            collectionDetailsList.clear()
                        }

                    }

                });

                Row(modifier = Modifier.fillMaxWidth().padding(top = 20.dp), horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.CalendarMonth, contentDescription = "month", tint = Color.White)
                    Text(
                        text = selectedDate.value.uppercase(), color = MaterialTheme.colorScheme.tertiary, textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(horizontal = 20.dp),
                        )
                }
                Divider(modifier = Modifier.padding(top = 10.dp))

                LazyColumn() {
                    items(collectionDetailsList.size) {
                        SingleCollectionItem(collectionDetailsList[it], displayContent, selectedCompany)
                    }
                }
            }
            else if (displayContent?.value == ContentType.COLLECTION_HISTORY)
            {
                Repository.apiCalls.retrievePreviousCollectionDetails(selectedCompany.value.companyId, object : ApiResponse
                {
                    override fun onResponseObtained(isSuccess: Boolean, response: Any?)
                    {
                        if (isSuccess)
                        {
                            val previousCollectionDataResponse = response as PreviousCollectionDataResponse
                            Log.d("cwere", "onResponseObtained: " + previousCollectionDataResponse.collectionDetails)
                            previousCollectionDataResponse.collectionDetails?.let {
                                previousCollectionData = it.toMutableList()
                            }

                        }
                    }

                })
                CollectionHistory(selectedCompany, previousCollectionData)
            }


        }

    }


    @Composable
    fun CollectionHistory(selectedCompany: MutableState<CollectionDetailsResponse>,
        previousCollectionData: MutableList<PrevCollectionDetails>)
    {
        LazyColumn() {
            item {
                Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                    Text(text = selectedCompany.value.companyName.uppercase(), modifier = Modifier.padding(top = 10.dp),
                        color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.headlineLarge)
                    Text(text = selectedCompany.value.companyRepresentative.uppercase(), modifier = Modifier.padding(top = 5.dp),
                        color = MaterialTheme.colorScheme.tertiary)

                    val textColor = MaterialTheme.colorScheme.secondary
                    LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.fillMaxWidth().height(140.dp).padding(top = 20.dp)) {


                        item() {
                            Text(text = "Date", modifier = Modifier.padding(end = 20.dp), color = textColor)
                        }
                        item() {
                            Text(text = "Remark", modifier = Modifier.padding(end = 20.dp), color = textColor)
                        }
                        item() {
                            Text(text = "Feasibility", modifier = Modifier.padding(end = 20.dp), color = textColor)
                        }
                        for (item in previousCollectionData) items(3) {

                            when (it)
                            {
                                0 -> Text(text = "" + item.followUpDate, color = textColor, modifier = Modifier.padding(top = 10.dp))
                                1 -> Text(text = "" + item.remark, color = textColor, modifier = Modifier.padding(top = 10.dp))
                                2 -> Text(text = "" + getFeasibilityLabel(item.feasibility!!), color = textColor,
                                    modifier = Modifier.padding(top = 10.dp))
                            }
                        }

                    }

                    Text(text = "New Entry", color = MaterialTheme.colorScheme.tertiary)
                    val newFollowUpDate = remember {
                        mutableStateOf("")
                    }

                    val newRemarkMutableState = remember {
                        mutableStateOf("")
                    }
                    val newFeasibilityMutableState = remember {
                        mutableStateOf("")
                    }
                    LabelAndField("Followup Date", newFollowUpDate, {
                        PickDate(newFollowUpDate)
                    })
                    LabelAndField("Remark", newRemarkMutableState)
                    LabelWithThreeOptions({
                        newFeasibilityMutableState.value = it
                    })

                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                        Button(enabled =isButtonEnabled ,colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = Color.White), onClick = {

                            if (newFeasibilityMutableState.value.length > 0 && newRemarkMutableState.value.length > 0 && newFollowUpDate.value.length > 0)
                            {
                                Repository.apiCalls.addNewRemark(selectedCompany!!.value.companyId, newRemarkMutableState.value,
                                    newFollowUpDate.value, getFeasibilityValue(newFeasibilityMutableState.value),
                                    Repository.cacheData.getDataWithoutLiveData(this@DashBoardActivity,USER_ID,""),latitude,longitude,
                                    object : ApiResponse
                                    {
                                        override fun onResponseObtained(isSuccess: Boolean, response: Any?)
                                        {
                                            if (isSuccess) Toast.makeText(this@DashBoardActivity, "Remark Added", Toast.LENGTH_SHORT).show()
                                            else Toast.makeText(this@DashBoardActivity, "" + (response as String), Toast.LENGTH_SHORT)
                                                .show()
                                        }

                                    })
                            }
                            else
                            {
                                Toast.makeText(this@DashBoardActivity, "Enter the required fields", Toast.LENGTH_SHORT).show()
                            }
                        }, modifier = Modifier.padding(vertical = 20.dp)) {
                            Text(text = "Upload".uppercase(), modifier = Modifier.padding(vertical = 10.dp, horizontal = 30.dp))

                        }
                    }

                }
            }
        }

    }


    @Preview
    private
    @Composable
    fun SingleCollectionItem(collectionDetailsResponse: CollectionDetailsResponse? = null, displayContent: MutableState<String>? = null,
        selectedCompany: MutableState<CollectionDetailsResponse>? = null)
    {
        collectionDetailsResponse?.let {
            Column(modifier = Modifier.fillMaxWidth().padding(10.dp).background(Color.White).padding(10.dp).clickable {
                selectedCompany?.value = it
                displayContent?.value = ContentType.COLLECTION_HISTORY
            }) {
                Text(text = it.companyName.uppercase(), style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.secondary)
                Text(text = it.companyRepresentative, color = MaterialTheme.colorScheme.secondary)

                Text(text = getFeasibilityLabel(it.feasibility), modifier = Modifier.padding(top = 30.dp).fillMaxWidth(),
                    textAlign = TextAlign.End)
            }
        }


    }

    fun getFeasibilityLabel(feasibility: Int): String
    {
        if (feasibility == 1) return "High"
        else if (feasibility == 2) return "Medium"
        else return "Low"

    }

    fun getFeasibilityValue(feasibility: String): Int
    {
        if (feasibility.equals("High")) return 1
        else if (feasibility.equals("Medium")) return 2
        else return 3

    }

    var companyListMutableList: MutableList<CompanyDetailsResponse> by mutableStateOf(mutableListOf())
    var originalCompanyList: MutableList<CompanyDetailsResponse> by mutableStateOf(mutableListOf())
    var isItemSelected by mutableStateOf(true)
    var selectedCompanyItem by mutableStateOf(CompanyDetailsResponse())


    var enquiryMutableList: MutableList<EnquiryResponse> by mutableStateOf(mutableListOf())
    var originalenquiryList: MutableList<EnquiryResponse> by mutableStateOf(mutableListOf())
    var isenquirySelected by mutableStateOf(true)


    lateinit var permissionLauncher: ActivityResultLauncher<String>

    @Preview(showBackground = true)
    @Composable
    fun MainComposeView(displayContent: MutableState<String>? = null)
    {


        val selectedEnquiryList = remember {
            mutableStateListOf<EnquiryResponse>()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Repository.apiCalls.retrieveCompanyList(object : ApiResponse
        {
            override fun onResponseObtained(isSuccess: Boolean, response: Any?)
            {
                if (isSuccess)
                {
                    val companyDetailsList = response as kotlin.collections.List<CompanyDetailsResponse>
                    companyListMutableList = companyDetailsList.toMutableList()
                    originalCompanyList = companyDetailsList.toMutableList()

                }
            }

        })
        Repository.apiCalls.retrieveEnquiry(object : ApiResponse
        {
            override fun onResponseObtained(isSuccess: Boolean, response: Any?)
            {
                if (isSuccess)
                {
                    val enquiryResponse = response as kotlin.collections.List<EnquiryResponse>
                    enquiryMutableList = enquiryResponse.toMutableList()
                    originalenquiryList = enquiryResponse.toMutableList()
                }
            }

        })

        getCurrentDate()
        val companyNameMutableData = remember {
            mutableStateOf("")
        }
        val companyRepresentativeMutableState = remember {
            mutableStateOf("")
        }
        val companyContactNumberMutableState = remember {
            mutableStateOf("")
        }
        val enquiryMutableState = remember {
            mutableStateOf("")
        }
        val businessModeMutableState = remember {
            mutableStateOf("")
        }

        val remarkMutableState = remember {
            mutableStateOf("")
        }

        val referencesMutableState = remember {
            mutableStateOf("")
        }
        val locationMutableState = remember {
            mutableStateOf("")
        }

        val followUpDateMutableState = remember {
            mutableStateOf("")
        }
        val companyIDMutableState = remember {
            mutableStateOf(-1)
        }
        val feasibilityMutableState = remember {
            mutableStateOf("")
        }
        ConstraintLayout(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(10.dp)) {
            val (titleElement, bottomElement) = createRefs()



            Column(modifier = Modifier.fillMaxWidth().constrainAs(titleElement) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                bottom.linkTo(bottomElement.top)
                height = Dimension.fillToConstraints
            }, horizontalAlignment = Alignment.CenterHorizontally) {

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = Repository.cacheData.getDataWithoutLiveData<String>(this@DashBoardActivity, USERNAME, "").uppercase(),
                        fontSize = 30.sp, color = labelColor, letterSpacing = 2.sp, modifier = Modifier.padding(top = 20.dp, start = 10.dp))
                    val expanded = remember { mutableStateOf(false) }


                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                        IconButton(onClick = { expanded.value = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Localized description")
                        }

                        DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
                            Column {
                                Text(text = "FollowUp Collections",
                                    modifier = Modifier.padding(horizontal = 30.dp, vertical = 5.dp).clickable {
                                        displayContent?.value = ContentType.FOLLOWUP_COLLECTION
                                    })
                            }

                        }
                    }


                }



                LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                    item {
                        Text(text = "Create new Details".uppercase(), color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(top = 30.dp).border(2.dp, color = MaterialTheme.colorScheme.secondary)
                                .padding(horizontal = 30.dp, vertical = 10.dp), fontSize = 20.sp)
                    }
                    item {

                        Column(Modifier.fillMaxWidth().padding(20.dp)) {

                            LabelAndField("Company Name", companyNameMutableData, onTextChange = { textChange ->

                                if (textChange.length == 0) companyListMutableList = originalCompanyList
                                else companyListMutableList = companyListMutableList.filter {
                                    it.companyName.uppercase().startsWith(textChange.uppercase())

                                }.toMutableList()
                                isItemSelected = false
                            })
                            if (!isItemSelected) Popup(offset = IntOffset(10, 250)) {
                                LazyColumn(modifier = Modifier.background(MaterialTheme.colorScheme.secondary).width(200.dp)) {
                                    items(items = companyListMutableList, key = { message ->

                                        message.id
                                    }) { message ->
                                        Text(text = message.companyName, color = Color.White,
                                            modifier = Modifier.width(200.dp).background(Color.Transparent).padding(10.dp).clickable {
                                                selectedCompanyItem = message
                                                companyNameMutableData.value = message.companyName
                                                companyRepresentativeMutableState.value = message.companyRepresentative
                                                companyContactNumberMutableState.value = message.phoneNumber
                                                locationMutableState.value = message.locationName
                                                businessModeMutableState.value = message.primaryBusinessMode
                                                companyIDMutableState.value = message.id

                                                isItemSelected = true
                                            })
                                    }
                                }
                            }

                            LabelAndField("Company Representative", companyRepresentativeMutableState)
                            LabelAndField("Contact Number", companyContactNumberMutableState,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                            LabelAndField("Location", locationMutableState)
                            LabelAndField("Mode of Business", businessModeMutableState)
                            LabelAndField("Enquiry", enquiryMutableState, onTextChange = { textChange ->
                                if (textChange.length == 0) enquiryMutableList = originalenquiryList
                                else enquiryMutableList = enquiryMutableList.filter {
                                    it.enquiryName.uppercase().startsWith(textChange.uppercase())

                                }.toMutableList()
                                isenquirySelected = false
                            })

                            if (!isenquirySelected) Popup(alignment = Alignment.TopCenter) {
                                LazyColumn(modifier = Modifier.background(MaterialTheme.colorScheme.secondary).width(200.dp)) {
                                    items(items = enquiryMutableList, key = { message ->

                                        message.id
                                    }) { message ->
                                        Text(text = message.enquiryName, color = Color.White,
                                            modifier = Modifier.width(200.dp).background(Color.Transparent).padding(10.dp).clickable {
                                                selectedEnquiryList.add(message)
                                                enquiryMutableState.value = ""
                                                isenquirySelected = true
                                            })
                                    }
                                }
                            }
                            LazyRow(modifier = Modifier.padding(top = 10.dp)) {
                                items(selectedEnquiryList, key = {
                                    it.id
                                }) {
                                    Text(text = it.enquiryName, modifier = Modifier.padding(10.dp)
                                        .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(10.dp)).padding(10.dp)
                                        .clickable {
                                            selectedEnquiryList.remove(it)

                                        }, color = MaterialTheme.colorScheme.tertiary)

                                }
                            }
                            Log.d("vrujjr", "MainComposeView: " + selectedEnquiryList.toList().toString())

                            LabelWithThreeOptions({
                                feasibilityMutableState.value = it
                            })
                            LabelAndField("Followup Date", followUpDateMutableState, {
                                PickDate(followUpDateMutableState)
                            })
                            LabelAndField("Remark", remarkMutableState)
                            LabelAndField("References", referencesMutableState)
                        }
                    }

                }

            }
            Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxWidth().constrainAs(bottomElement) {
                bottom.linkTo(parent.bottom)
            }) {
                Button(enabled = isButtonEnabled,
                    colors = if (isButtonEnabled)ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = Color.White)
                    else ButtonDefaults.buttonColors(containerColor = Pink40 ,contentColor = Color.Black),
                    onClick = {

                        if (enquiryMutableState.value.length > 0)
                        {
                            if (selectedEnquiryList.isEmpty())
                            {
                                selectedEnquiryList.add(EnquiryResponse(-1, enquiryMutableState.value))
                            }
                        }

                        if (companyNameMutableData.value.length > 0 && companyRepresentativeMutableState.value.length > 0 && companyContactNumberMutableState.value.length > 0 && locationMutableState.value.length > 0 && businessModeMutableState.value.length > 0 && selectedEnquiryList.toList().size > 0 && feasibilityMutableState.value.length > 0 && followUpDateMutableState.value.length > 0 && remarkMutableState.value.length > 0 && referencesMutableState.value.length > 0)

                            Repository.apiCalls.uploadCollectionDetails(companyIDMutableState.value, companyNameMutableData.value,
                                companyRepresentativeMutableState.value, companyContactNumberMutableState.value, locationMutableState.value,
                                businessModeMutableState.value, selectedEnquiryList.toList(),
                                "" + getFeasibilityValue(feasibilityMutableState.value), followUpDateMutableState.value,
                                remarkMutableState.value, referencesMutableState.value,  Repository.cacheData.getDataWithoutLiveData(this@DashBoardActivity,USER_ID,""),latitude,longitude, object : ApiResponse
                                {
                                    override fun onResponseObtained(isSuccess: Boolean, response: Any?)
                                    {
                                        if (isSuccess)
                                        {
                                            Toast.makeText(this@DashBoardActivity, "Entry added", Toast.LENGTH_SHORT).show()
                                            companyNameMutableData.value = ""
                                            companyRepresentativeMutableState.value = ""
                                            companyContactNumberMutableState.value = ""
                                            locationMutableState.value = ""
                                            businessModeMutableState.value = ""
                                            selectedEnquiryList.clear()
                                            feasibilityMutableState.value = ""
                                            followUpDateMutableState.value = ""
                                            remarkMutableState.value = ""
                                            referencesMutableState.value = ""
                                            enquiryMutableState.value = ""
                                        }
                                        else Toast.makeText(this@DashBoardActivity, "Entry not added", Toast.LENGTH_SHORT).show()


                                    }

                                })
                        else Toast.makeText(this@DashBoardActivity, "Enter all required fields", Toast.LENGTH_SHORT).show()
                    }, modifier = Modifier.padding(vertical = 20.dp)) {
                    Text(text = "Upload".uppercase(), modifier = Modifier.padding(vertical = 10.dp, horizontal = 30.dp))

                }
            }


        }
    }


    val DATE_FORMAT_ddMMYYYY = "dd-MM-yyyy"

    fun getCurrentDate(): String
    {
        val sdf = SimpleDateFormat(DATE_FORMAT_ddMMYYYY)


        val c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH) + 1;
        mDay = c.get(Calendar.DAY_OF_MONTH);

        return sdf.format(c.time)
    }

    var mYear = 0
    var mMonth: Int = 0
    var mDay: Int = 0
    private fun PickDate(selectedDate: MutableState<String>? = null)
    {

        val datePickerDialog = DatePickerDialog(this, R.style.DialogTheme, { view, year, monthOfYear, dayOfMonth ->

            mYear = year
            mMonth = monthOfYear + 1
            mDay = dayOfMonth
            selectedDate?.let {
                val current = mYear.toString() + "-" + mMonth.toString() + "-" + mDay.toString()
                Log.d("awewek", "CollectionDetailsMainPage: " + current)
                it.value = current
            }

        }, mYear, mMonth, mDay)


        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Ok", datePickerDialog)
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancel", datePickerDialog)

        datePickerDialog.show()
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setAllCaps(false)
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(android.graphics.Color.BLACK)
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setAllCaps(false)

        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(android.graphics.Color.BLACK)
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(android.graphics.Color.BLACK)
    }


    @Composable
    fun LabelWithThreeOptions(onItemSelected: (String) -> Unit)
    {
        val isFirstOptionSelectedMutableData = remember {
            mutableStateOf(false)
        }
        val isSecondOptionSelectedMutableData = remember {
            mutableStateOf(false)
        }
        val isThirdOptionSelectedMutableData = remember {
            mutableStateOf(false)
        }
        Column(modifier = Modifier.fillMaxWidth().padding(top = 5.dp)) {
            Text(text = "Feasibility", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(vertical = 14.dp))
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                Row(modifier = Modifier.border(2.dp, color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(10.dp))
                    .padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    OptionText("Low", isFirstOptionSelectedMutableData, {

                        isFirstOptionSelectedMutableData.value = true
                        isSecondOptionSelectedMutableData.value = false
                        isThirdOptionSelectedMutableData.value = false
                        onItemSelected.invoke("Low")
                    })
                    OptionText("Medium", isSecondOptionSelectedMutableData, {
                        isFirstOptionSelectedMutableData.value = false
                        isSecondOptionSelectedMutableData.value = true
                        isThirdOptionSelectedMutableData.value = false
                        onItemSelected.invoke("Medium")
                    })
                    OptionText("High", isThirdOptionSelectedMutableData, {
                        isFirstOptionSelectedMutableData.value = false
                        isSecondOptionSelectedMutableData.value = false
                        isThirdOptionSelectedMutableData.value = true
                        onItemSelected.invoke("High")
                    })
                }
            }


        }
    }

    @Composable
    fun OptionText(text: String = "low", isSelected: MutableState<Boolean>, onElementClick: () -> Unit)
    {
        val roundedBorder = RoundedCornerShape(10.dp)


        val backGroundColor = if (isSelected.value) MaterialTheme.colorScheme.secondary else Color.Transparent
        val textColor = if (isSelected.value) Color.White else MaterialTheme.colorScheme.secondary
        Text(text = text.uppercase(), modifier = Modifier.clickable {
            onElementClick.invoke()
        }.padding(horizontal = 3.dp).border(width = 0.dp, shape = roundedBorder, color = backGroundColor)
            .background(backGroundColor, shape = roundedBorder).padding(vertical = 10.dp, horizontal = 20.dp), color = textColor)

    }

    @Composable
    fun LabelAndField(labelText: String, fieldMutableData: MutableState<String>, predefinedFunction: (() -> Unit)? = null,
        onTextChange: ((String) -> Unit)? = null, keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text))
    {

        Column(modifier = Modifier.fillMaxWidth().padding(top = 5.dp)) {
            Text(text = labelText, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(vertical = 10.dp))
            TextField(enabled = (predefinedFunction == null), value = fieldMutableData.value, onValueChange = {
                fieldMutableData.value = it
                onTextChange?.invoke(it)
            }, modifier = Modifier.fillMaxWidth().clickable {
                predefinedFunction?.invoke()
            }, keyboardOptions = keyboardOptions)

        }

    }


}



