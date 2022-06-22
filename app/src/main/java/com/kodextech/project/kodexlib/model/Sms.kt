package com.kodextech.project.kodexlib.model

import java.io.Serializable

data class Sms(

    var bookingId: String? = null,
    var sms: String? = null,
    var updatedAt: String? = null,
    var createdAt: String? = null,
    var id: Int? = null

) : Serializable
