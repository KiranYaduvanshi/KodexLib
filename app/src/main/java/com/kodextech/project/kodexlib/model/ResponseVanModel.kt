package com.kodextech.project.kodexlib.model

import java.io.Serializable

class ResponseVanModel(
    val id: String? = null,
    val uuid: String? = null,
    val job_id: String? = null,
    val model_name: String? = null,
    val qnty: String? = null,
    val deleted_at: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
) : Serializable