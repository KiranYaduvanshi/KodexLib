package com.kodextech.project.kodexlib.model

import com.kodextech.project.kodexlib.ui.main.calendar.adapter.TypeJobs
import java.io.Serializable

data class JobModel(
    val additional_details: String? = null,
    val advance_amount: String? = null,
    val pickup_flat_meta: ArrayList<PickFlatMeta>? = null,
    val approver: Any? = null,
    val approver_id: Any? = null,
    val booker_id: Int? = null,
    val charged_amount: String? = null,
    val min_hours_count: String? = null,
    val packing_fee: String? = null,
    val created_at: String? = null,
    val customer_signature_media_id: Int? = null,
    val deleted_at: Any? = null,
//    val drop_address: DropAddress? = null,
    val medias: ArrayList<MediaModel>? = null,
    val pickup_addresses: ArrayList<PickupAddress>? = null,
    val drop_addresses: ArrayList<PickupAddress>? = null,
    val medias_without_signature: ArrayList<MediaModel>? = null,
    val drop_address_id: Any? = null,
    val invoice: InvoiceModel? = null,
    val email: String? = null,
    val invoice_count: String? = null,
    val end_time: Any? = null,
    val first_name: String? = null,
    val id: Int? = null,
    val is_approved: Boolean? = null,
    val job_status: String? = null,
    val is_notify: String? = null,
    val last_name: String? = null,
    val phone_code: String? = null,
    val phone_numb: String? = null,
    val pickup_address: PickupAddress? = null,
    val drop_address: PickupAddress? = null,
    val pickup_address_id: Int? = null,
    val price_amount: String? = null,
    val price_nature: String? = null,
    val floor_no: String? = null,
    val has_lift: String? = null,
    val approx_bags_count: String? = null,
    val approx_boxes_count: String? = null,
    val men_count: String? = null,
    val van_count: String? = null,
    val has_requested_insurance: String? = null,
    val white_goods: String? = null,
    val van_model_name: String? = null,
    val van_meta: ArrayList<ResponseVanModel>? = null,
    val priority: String? = null,
    val service: String? = null,
    val signature: Signature? = null,
    val start_time: String? = null,
    val updated_at: String? = null,
    val uuid: String? = null,
    val worker: WorkerModel? = null,
    val booker: Booker? = null,
    val worker_id: Int? = null,
    val worker_remarks: String? = null,
    val job_start_time: String? = null,
    val job_end_time: String? = null,
    val actual_hours: String? = null,
    val is_packing_service: String? = null,
    val is_any_dismantling: String? = null,
    var isSelected: Boolean? = false
) : Serializable {
    constructor(cellType: TypeJobs, sepDate: String) : this() {

        this.cellType = cellType
        this.separaterDate = sepDate
    }

    var cellType: TypeJobs? = TypeJobs.CELL
    var separaterDate: String? = ""
}