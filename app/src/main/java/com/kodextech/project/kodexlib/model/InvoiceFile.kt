package com.kodextech.project.kodexlib.model

import java.io.Serializable

data class InvoiceFile(
    val created_at: String?=null,
    val deleted_at: Any?=null,
    val id: Int?=null,
    val model_id: Int?=null,
    val model_name: String?=null,
    val path: String?=null,
    val ratio: String?=null,
    val tag: String?=null,
    val thumbnail: String?=null,
    val title: String?=null,
    val type: String?=null,
    val updated_at: String?=null,
    val uploader_id: Int
) :Serializable