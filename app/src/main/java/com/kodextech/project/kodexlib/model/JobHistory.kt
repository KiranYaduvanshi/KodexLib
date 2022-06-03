package com.kodextech.project.kodexlib.model

import java.io.Serializable

data class JobHistory(
    val approver_id: Any? = null,
    val booking: JobModel? = null,
    val driver: Driver? = null,
    val booking_id: Int? = null,
    val charged: String? = null,
    val created_at: String? = null,
    val customer_id: Int? = null,
    val deleted_at: Any? = null,
    val description: Any? = null,
    val driver_id: Int? = null,
    val drop_address_id: Int? = null,
    val end_time: String? = null,
    val id: Int? = null,
    val is_approved: Boolean? = null,
    val job_status: String? = null,
    val job_type: String? = null,
    val pickup_address: PickupAddress? = null,
    val drop_address: DropAddress? = null,
    val pickup_address_id: Int? = null,
    val start_time: String? = null,
    val customer: CustomerModel? = null,
    val updated_at: String? = null,
    val uuid: String? = null,
) : Serializable