package com.kodextech.project.kodexlib.model

import java.io.Serializable

data class Address(
    val address1: String? = null,
    val address2: Any? = null,
    val city: Any? = null,
    val country: Any? = null,
    val created_at: String? = null,
    val deleted_at: Any? = null,
    val id: Int? = null,
    val is_default: Int? = null,
    val lat: Any? = null,
    val lng: Any? = null,
    val places_id: Any? = null,
    val profile_id: Int? = null,
    val state: Any? = null,
    val title: Any? = null,
    val updated_at: String? = null,
    val uuid: String? = null,
    val zip: Any? = null
) : Serializable