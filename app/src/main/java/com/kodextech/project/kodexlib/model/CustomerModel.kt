package com.kodextech.project.kodexlib.model

import java.io.Serializable

data class CustomerModel(
    val approver: Any? = null,
    val booker: Any? = null,
    val customer_rating: Int? = null,
    val drop_address: Any? = null,
    val email: String? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val model_charged_amount: Any? = null,
    val model_end_time: Any? = null,
    val phone_code: String? = null,
    val phone_numb: String? = null,
    val service: String? = null,
    val pickup_address: PickupAddress? = null,
    val pickup_address_id: Int? = null,
    val signature: Any? = null,
    val worker: Any? = null,
    val worker_rating: Int? = null
) : Serializable