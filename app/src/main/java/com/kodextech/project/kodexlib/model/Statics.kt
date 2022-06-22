package com.kodextech.project.kodexlib.model

import java.io.Serializable


data class Statics(

    var Hours: Int? = null,
    var Rides: Int? = null,
    var Earnings: Int? = null,
    var Profit: Int? = null,
    var Loss: Int? = null

) : Serializable