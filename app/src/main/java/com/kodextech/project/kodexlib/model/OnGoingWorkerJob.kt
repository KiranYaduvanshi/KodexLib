package com.kodextech.project.kodexlib.model

data class OnGoingWorkerJob(
    val additional_details: String,
    val advance_amount: String,
    val approver_id: Any,
    val booker_id: Int,
    val charged_amount: String,
    val created_at: String,
    val customer_signature_media_id: Int,
    val deleted_at: Any,
    val drop_address_id: Any,
    val email: String,
    val end_time: Any,
    val first_name: String,
    val id: Int,
    val is_approved: Boolean,
    val job_status: String,
    val last_name: String,
    val phone_code: String,
    val phone_numb: String,
    val pickup_address_id: Int,
    val price_amount: String,
    val price_nature: String,
    val priority: String,
    val service: String,
    val start_time: String,
    val updated_at: String,
    val uuid: String,
    val worker_id: Int,
    val worker_remarks: String
)