package com.kodextech.project.kodexlib.model

import java.io.Serializable

data class MediaModel(
    val filename: String? = null,
    val path: String? = null,
    val ratio: Double? = null,
    val resolved_file_path: String? = null,
    val tag: String? = null,
    val thumbnail: String? = null,
    val title: String? = null,
    val type: String? = null,
    val file_url: String? = null,
) : Serializable