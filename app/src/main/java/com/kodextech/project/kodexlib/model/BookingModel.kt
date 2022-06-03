package com.kodextech.project.kodexlib.model

import java.io.Serializable

data class BookingModel(
    val models: List<JobModel>? = null,
    val total_count: Int? = null
) : Serializable