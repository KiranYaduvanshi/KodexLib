package com.kodextech.project.kodexlib.model

import java.io.Serializable

data class SmsCommunicationModel (

     var id     : Int?    = null,
     var Order  : String? = null,
     var Name   : String? = null,
     var phone  : String? = null,
     var Date   : String? = null,
     var Time   : String? = null,
     var sms    : String? = null,
     var status : String? = null

): Serializable