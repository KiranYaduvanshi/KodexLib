package com.kodextech.project.kodexlib.model

import java.io.Serializable

data class LocationDataModel(
    var location_id: String? = null,
    var location_lat: String? = null,
    var location_lng: String? = null,
    var location_name: String? = null,
    var location_city: String? = null,
    var location_state: String? = null,
    var location_country: String? = null,
    var location_places_id: String? = null
):Serializable