package com.kodextech.project.kodexlib.model

import java.io.Serializable

data class InvoiceModel(
    val charged_amount: String? = null,
    val created_at: String? = null,
    val deleted_at: Any? = null,
    val id: Int? = null,
    val invoice_file: InvoiceFile? = null,
    val invoice_file_id: Int? = null,
    val job: JobModel? = null,
    val job_id: Int? = null,
    val price_amount: String? = null,
    val status: String? = null,
    val updated_at: String? = null,
    val uuid: String? = null
) : Serializable