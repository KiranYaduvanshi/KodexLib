package com.kodextech.project.kodexlib.model

import java.io.Serializable


data class Statics(
     var rides    : Int? = null,
     var earnings : Int? = null,
     var hours    : Int? = null,
     var profit   : Int? = null,
     var loss     : Int? = null

) : Serializable