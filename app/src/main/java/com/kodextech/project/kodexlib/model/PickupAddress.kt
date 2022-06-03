package com.kodextech.project.kodexlib.model

import java.io.Serializable

data class PickupAddress(
    var address1: String?=null,
    var address2: Any?=null,
    var city: Any?=null,
    var country: Any?=null,
    var created_at: String?=null,
    var has_lift: String?=null,
    var deleted_at: Any?=null,
    var id: Int?=null,
    var pickup_flat_meta: ArrayList<FlatData>?=null,
    var flats_data: ArrayList<FlatData>?=null,
    var drop_flat_meta: ArrayList<FlatData>?=null,
    var is_default: Int?=null,
    var lat: Any?=null,
    var lng: Any?=null,
    var places_id: Any?=null,
    var profile_id: Int?=null,
    var state: Any?=null,
    var title: Any?=null,
    var updated_at: String?=null,
    var uuid: String?=null,
    var zip: Any ?=null
): Serializable