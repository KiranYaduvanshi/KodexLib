package com.kodextech.project.kodexlib.model

data class Signature(
    val created_at: String,
    val deleted_at: Any,
    val id: Int,
    val model_id: Int,
    val model_name: String,
    val path: String,
    val ratio: String,
    val tag: String,
    val thumbnail: String,
    val title: String,
    val type: String,
    val updated_at: String,
    val uploader_id: Int
)