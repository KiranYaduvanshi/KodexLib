package ir.shahabazimi.instagrampicker.gallery

import java.util.*

interface GalleySelectedListener {
    fun onSingleSelect(address: String?)
    fun onMultiSelect(addresses: ArrayList<String>)
}