package com.kodextech.project.kodexlib.model

data class Booking(
    val advance_amount: String,
    val approx_boxes_count: Int,
    val booked_by: Int,
    val booking_status: String,
    val comments: String,
    val created_at: String,
    val deleted_at: Any,
    val drop_address_id: Int,
    val email: String,
    val end_time: Any,
    val first_name: String,
    val has_assembling: Int,
    val id: Int,
    val last_name: String,
    val minimum_amount: String,
    val phone_code: String,
    val phone_numb: String,
    val pickup_address_id: Int,
    val price_nature: String,
    val price_total: String,
    val service: Any,
    val start_time: String,
    val updated_at: String,
    val uuid: String,
    val white_goods: String
)