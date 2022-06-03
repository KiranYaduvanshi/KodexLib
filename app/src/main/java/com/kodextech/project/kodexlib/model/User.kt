package com.kodextech.project.kodexlib.model

import java.io.Serializable

data class User(
    val created_at: String?=null,
    val deleted_at: Any?=null,
    val email: String?=null,
    val email_verified_at: String?=null,
    val id: Int?=null,
    val is_social: Int?=null,
    val profile: Profile?=null,
    val profile_id: Int?=null,
    val profile_type: String?=null,
    val social_email: Any?=null,
    val social_id: Any?=null,
    val social_type: Any?=null,
    val updated_at: String?=null,
    val username: String?=null,
    val uuid: String?=null,
    var isClicked: Boolean?=false,

): Serializable