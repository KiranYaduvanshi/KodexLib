package com.kodextech.project.kodexlib.model

import java.io.Serializable

class ResponseFlatModel(
    var floor_no: String? = null,
    var flat_no: String? = null,
    var id: String? = null,
    var uuid: String? = null,
    var deleted_at: String? = null,
    var created_at: String? = null,
    var updated_at: String? = null,
) : Serializable