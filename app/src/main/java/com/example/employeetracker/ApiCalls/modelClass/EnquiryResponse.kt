package com.example.employeetracker.ApiCalls.modelClass

open class EnquiryResponse()
{
  open  var id: Int = 0
  open  var enquiryName: String = ""
  override fun toString(): String
  {
    return "{\"id\":"+id+",\"enquiryName\":\""+enquiryName+"\"}"
  }
  constructor( id:Int,enquiryString:String) : this()
  {
    this.id=id
    this.enquiryName=enquiryString
  }
}