package com.kodextech.project.kodexlib.model

import java.io.Serializable



data class Expensis(
    var Fuel: Int? = null,
    var Labour: Int? = null,
    var VehicleMaintainence: Int? = null,
    var Equipment: Int? = null,
    var Other: Int? = null

):Serializable