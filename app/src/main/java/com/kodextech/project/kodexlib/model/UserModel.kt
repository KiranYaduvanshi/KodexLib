package com.kodextech.project.kodexlib.model

import java.io.Serializable

data class UserModel(
    val access_token: String? = null,
    val expires_at: String? = null,
    val token_type: String? = null,
    val user: User? = null
) : Serializable