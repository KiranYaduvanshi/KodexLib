package com.kodextech.project.kodexlib.model

import java.io.Serializable

data class CustomerProfileModel(
    val address:  PickupAddress?=null,
    val approver_id: Any?=null,
    val bio: Any?=null,
    val created_at: String?=null,
    val deleted_at: Any?=null,
    val dob: Any?=null,
    val first_name: String?=null,
    val full_name: String?=null,
    val gender: Any?=null,
    val id: Int?=null,
    val is_driver_available: Int?=null,
    val job_history: List<JobHistory>?=null,
    val last_name: String?=null,
    val meta: Any?=null,
    val on_going_driver_jobs: List<Any>?=null,
    val phone_2_verified_at: Any?=null,
    val phone_code: String?=null,
    val phone_code_2: Any?=null,
    val phone_number: String?=null,
    val phone_number_2: Any?=null,
    val phone_verified_at: Any?=null,
    val profile_image: String?=null,
    val profile_type: String?=null,
    val status: String?=null,
    val updated_at: String?=null,
    val user: User?=null,
    val user_id: Int?=null,
    val uuid: String?=null
):Serializable