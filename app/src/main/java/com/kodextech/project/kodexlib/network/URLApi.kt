@file:Suppress("SpellCheckingInspection", "unused", "FunctionName")

package com.kodextech.project.kodexlib.network


import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kodextech.project.kodexlib.model.MediaModel
import org.json.JSONArray
import org.json.JSONObject


enum class ShareTypes(var value: String) {
    product("product"),
    post("post"),
    event("event"),
}


enum class UserType(var value: String) {
    Personal("personal"),
    Vendor("vendor")
}

@Suppress("UNUSED_PARAMETER")
object URLApi {


    private val TAG = URLApi::class.java.toString()

    const val SOCKET_URL = "http://moderns.modernmover.co.uk"

    //    const val SOCKET_URL = "http://45.56.122.34:1028"
  const val BaseUrl =
        "http://13.58.42.125/api/"//Base URL here
//        "http://45.33.19.125/modern-movers/api/" //Base URL here

//        "http://45.56.122.34/modern-movers/public/api/" //Base URL here
//        "http://kodextech.net/modern-movers/public/api/" //Base URL here
    private var path: String = ""
    private var params: JSONObject = JSONObject()
    var method: NetworkMethod = NetworkMethod.GET
    fun link(): String {
        return BaseUrl + path
    }

    fun param(): JSONObject {
        return params
    }

    final fun paramHas(): HashMap<String, Any>? {
        return Gson().fromJson<HashMap<String, Any>>(
            URLApi.param().toString(), object : TypeToken<HashMap<String?, Any?>?>() {}.type
        )
    }

    final fun paramHashMap(): HashMap<*, *> {
        return Gson().fromJson(params.toString(), HashMap::class.java)
    }


    fun shareDeepLink(id: String, type: ShareTypes, userKind: UserType): String {
        return when (type) {
            ShareTypes.product -> {
                BaseUrl + "products/${id}"
            }
            ShareTypes.post -> {
                if (userKind == UserType.Vendor) {
                    BaseUrl + "post-details/${id}"//"vendor/postdetails?id=\(id)"
                } else {
                    BaseUrl + "post-details/${id}"//"user/postdetails?id=\(id)"
                }
            }
            ShareTypes.event -> {
                BaseUrl + "details?id=${id}&type=${type.value}"
            }
        }

    }

    fun signup(
        email: String? = null,
        password: String? = null,
        password_confirmation: String? = null,
        first_name: String? = null,
        last_name: String? = null,
        device_type: String? = null,
        phone_code: String? = null,
        phone_number: String? = null,
        is_social: String? = null,
        profile_type: String? = null,
        address1: String? = null,
        city: String? = null,
        state: String? = null,
        country: String? = null,
        post_code: String? = null,
        lat: String? = null,
        lng: String? = null,
    ): URLApi {
        method = NetworkMethod.POST
        path = "auth/signup"
        params = JSONObject()
        params.put("email", email)
        params.put("password", password)
        params.put("password_confirmation", password_confirmation)
        params.put("first_name", first_name)
        params.put("last_name", last_name)
        params.put("device_type", device_type)
        params.put("phone_code", phone_code)
        params.put("phone_number", phone_number)
        params.put("is_social", is_social)
        params.put("profile_type", profile_type)
        params.put("address1", address1)
        params.put("city", city)
        params.put("state", state)
        params.put("country", country)
        params.put("post_code", post_code)
        params.put("lat", lat)
        params.put("lng", lng)

        return this

    }

    fun login(email: String, password: String, is_social: String): URLApi {
        method = NetworkMethod.POST
        path = "auth/login"
        params = JSONObject()
        params.put("email", email)
        params.put("password", password)
        params.put("is_social", is_social)
        return this
    }
    fun getEmailCommunationApi():URLApi{
        method =NetworkMethod.GET
        path ="get-email-communication"
        return this
    }

    fun signout(): URLApi {
        method = NetworkMethod.POST
        path = "auth/signout"
        params = JSONObject()
        return this
    }


    fun verifyUser(
        email: String? = null,
        activation_code: String? = null,
        is_social: String
    ): URLApi {
        method = NetworkMethod.POST
        path = "auth/verify-user"
        params = JSONObject()
        params.put("email", email)
        params.put("activation_code", activation_code)
        params.put("is_social", is_social)
        Log.i("MODERN_MOVERS", activation_code + "");
        return this
    }

    fun addBooking(
        job_uuid: String? = null,
        booker_uuid: String? = null,
        worker_uuid: String? = null,
        first_name: String? = null,
        last_name: String? = null,
        email: String? = null,
        phone_code: String? = null,
        phone_numb: String? = null,
        priority: String? = null,

        price_nature: String? = null,
        price_amount: String? = null,
        advance_amount: String? = null,

        service: String? = null,
        is_approved: String? = null,
        job_status: String? = null,
        start_time: String? = null,
        additional_details: String? = null,
        white_goods: String? = null,
        approx_boxes_count: String? = null,
        approx_bags_count: String? = null,
        floor_no: String? = null,
        has_requested_insurance: String? = null,
        has_assembling: String? = null,
        phone_code_2: String? = null,
        phone_numb_2: String? = null,


        worker_remarks: String? = null,
        signature_media: String? = null,

        pickup_addresses: String? = null,
//        pickup_places_id: String? = null,
//        pickup_address_title: String? = null,
//        pickup_address1: String? = null,
//        pickup_city: String? = null,
//        pickup_state: String? = null,
//        pickup_country: String? = null,
//        pickup_post_code: String? = null,
//        pickup_lat: String? = null,
//        pickup_lng: String? = null,

        drop_addresses: String? = null,
//        drop_places_id: String? = null,
//        drop_address_title: String? = null,
//        drop_address1: String? = null,
//        drop_city: String? = null,
//        drop_state: String? = null,
//        drop_country: String? = null,
//        drop_post_code: String? = null,
//        drop_lat: String? = null,
//        drop_lng: String? = null,

        has_lift: String? = null,
        uploaded_files: String? = null,

        men_count: String? = null,
//        van_count: String? = null,

//        van_model_name: String? = null,
        min_hours_count: String? = null,
//        vans: String? = null,
        vans_data: String? = null,
        packing_fee: String? = null,
        is_packing_service: String? = null,
        is_any_dismantling: String? = null,

        ):
            URLApi {
        method = NetworkMethod.POST
        path = "update-job"
        params = JSONObject()
        params.put("job_uuid", job_uuid)
        params.put("booker_uuid", booker_uuid)
        params.put("worker_uuid", worker_uuid)
        params.put("first_name", first_name)
        params.put("last_name", last_name)
        params.put("email", email)
        params.put("phone_code", phone_code)
        params.put("phone_numb", phone_numb)

        params.put("priority", priority)
        params.put("price_nature", price_nature)
        params.put("price_amount", price_amount)
        params.put("advance_amount", advance_amount)

        params.put("service", service)
        params.put("is_approved", is_approved)
        params.put("job_status", job_status)
        params.put("start_time", start_time)

        params.put("additional_details", additional_details)
        params.put("white_goods", white_goods)
        params.put("approx_boxes_count", approx_boxes_count)
        params.put("approx_bags_count", approx_bags_count)
        params.put("floor_no", floor_no)
        params.put("has_requested_insurance", has_requested_insurance)
        params.put("has_assembling", has_assembling)
        params.put("phone_code_2", phone_code_2)
        params.put("phone_numb_2", phone_numb_2)

        params.put("worker_remarks", worker_remarks)
        params.put("signature_media", signature_media)

        params.put("pickup_addresses", pickup_addresses)
//        params.put("pickup_places_id", pickup_places_id)
//        params.put("pickup_address_title", pickup_address_title)
//        params.put("pickup_address1", pickup_address1)
//        params.put("pickup_city", pickup_city)
//        params.put("pickup_state", pickup_state)
//        params.put("pickup_country", pickup_country)
//        params.put("pickup_post_code", pickup_post_code)
//        params.put("pickup_lat", pickup_lat)
//        params.put("pickup_lng", pickup_lng)

        params.put("drop_addresses", drop_addresses)
//        params.put("drop_places_id", drop_places_id)
//        params.put("drop_address_title", drop_address_title)
//        params.put("drop_address1", drop_address1)
//        params.put("drop_city", drop_city)
//        params.put("drop_state", drop_state)
//        params.put("drop_country", drop_country)
//        params.put("drop_post_code", drop_post_code)
//        params.put("drop_lat", drop_lat)
//        params.put("drop_lng", drop_lng)

        params.put("has_lift", has_lift)
        params.put("uploaded_files", uploaded_files)

        params.put("men_count", men_count)

//        params.put("van_count", van_count)
//        params.put("van_model_name", van_model_name)

        params.put("min_hours_count", min_hours_count)
        params.put("vans_data", vans_data)
        params.put("packing_fee", packing_fee)
        params.put("is_packing_service", is_packing_service)
        params.put("is_any_dismantling", is_any_dismantling)
//        params.put("vans", vans)

        return this

    }

    fun getBookingList(booked_by_uuid: String? = null): URLApi {
        method = NetworkMethod.POST
        path = "list-bookings"
        params = JSONObject()
        params.put("booked_by_uuid", booked_by_uuid)

        return this
    }

    fun getSpecificBooking(booking_uuid: String? = null): URLApi {
        method = NetworkMethod.POST
        path = "get-booking"
        params = JSONObject()
        params.put("booking_uuid", booking_uuid)

        return this
    }


    fun addDocument(nature: String? = null): URLApi {
        method = NetworkMethod.POST
        path = "add-document"
        params = JSONObject()
        params.put("nature", nature)
        return this
    }

    fun deleteDocument(medias: ArrayList<MediaModel>? = null, nature: String? = null): URLApi {
        method = NetworkMethod.POST
        path = "delete-document"
        params = JSONObject()

        val jArray = JSONArray()
        medias?.forEach {
            jArray.put(it.path)
        }

        params.put("medias", jArray)
        params.put("nature", nature)
        return this
    }

    fun generateGeneralEmail(
        worker_type: String? = null,
        subject: String? = null,
        body: String? = null,
        media: String? = null,
        email_media: String? = null
    ): URLApi {
        method = NetworkMethod.POST
        path = "generate-general-email"
        params = JSONObject()
        params.put("worker_type", worker_type)
        params.put("subject", subject)
        params.put("body", body)
        params.put("media", media)
        params.put("email_media", email_media)
        return this
    }

    fun getListWorker(): URLApi {
        method = NetworkMethod.POST
        path = "list-worker-types"
        params = JSONObject()

        return this
    }

    fun updateInvoicesStatus(invoice_uuid: String?, status: String?): URLApi {
        method = NetworkMethod.POST
        path = "update-invoice-status"
        params = JSONObject()

        params.put("invoice_uuid", invoice_uuid)
        params.put("status", status)

        return this
    }

    fun getInvoicePDF(job_uuid: String?): URLApi {
        method = NetworkMethod.POST
        path = "get-job-inovice-pdf"
        params = JSONObject()

        params.put("job_uuid", job_uuid)


        return this
    }

    fun getInvoicesList(): URLApi {
        method = NetworkMethod.POST
        path = "list-invoices"
        params = JSONObject()

        return this
    }


    fun addWorker(
        worker_uuid: String? = null,
        email: String? = null,
        password: String? = null,
        first_name: String? = null,
        device_type: String? = null,
        worker_type: String? = null,
        price_nature: String? = null,
        price_amount: String? = null,
    ): URLApi {
        method = NetworkMethod.POST
        path = "update-worker"
        params = JSONObject()
        params.put("worker_uuid", worker_uuid)
        params.put("email", email)
        params.put("password", password)
        params.put("first_name", first_name)
        params.put("device_type", device_type)
        params.put("worker_type", worker_type)
        params.put("price_nature", "hourly-price")
        params.put("price_amount", price_amount)
        return this
    }

    fun getUser(user_uuid: String? = null): URLApi {
        method = NetworkMethod.POST
        path = "get-user"
        params = JSONObject()
        params.put("user_uuid", user_uuid)
        return this
    }

    fun getCustomerList(): URLApi {
        method = NetworkMethod.POST
        path = "list-unique-customers"
        params = JSONObject()
        return this
    }

    fun getCustomerProfile(customer_uuid: String? = null, relations: String? = null): URLApi {
        method = NetworkMethod.POST
        path = "get-customer"
        params = JSONObject()
        params.put("customer_uuid", customer_uuid)
        params.put("relations", relations)
        return this
    }

    fun deleteBooking(job_uuid: String?): URLApi {
        method = NetworkMethod.POST
        path = "delete-booking"
        params = JSONObject()
        params.put("job_uuid", job_uuid)

        return this
    }

    fun deleteUser(user_uuid: String? = null): URLApi {
        method = NetworkMethod.POST
        path = "delete-user"
        params = JSONObject()
        params.put("user_uuid", user_uuid)
        return this
    }

    fun getJobList(
        worker_uuid: String? = null,
        job_status: String? = null,
        email: String? = null,
    ): URLApi {

        method = NetworkMethod.POST
        path = "list-jobs"
        params = JSONObject()
        params.put("worker_uuid", worker_uuid)

        params.put("job_status", job_status)
        params.put("email", email)

        return this
    }

    fun rateCustomer(
        customer_email: String? = null,
        customer_rating: String? = null,
        worker_rating: String? = null,
    ): URLApi {
        method = NetworkMethod.POST
        path = "update-customer-rating"
        params = JSONObject()
        params.put("customer_email", customer_email)
        params.put("customer_rating", customer_rating)
        params.put("worker_rating", worker_rating)

        return this
    }

    fun assignJob(
        job_uuid: String? = null,
        worker_uuid: String? = null,
    ): URLApi {
        method = NetworkMethod.POST
        path = "assign-job"
        params = JSONObject()
        params.put("job_uuid", job_uuid)
        params.put("worker_uuid", worker_uuid)
        return this
    }

    fun startJob(
        job_uuid: String? = null,
        signature_media: String? = null,
    ): URLApi {
        method = NetworkMethod.POST
        path = "start-job"
        params = JSONObject()
        params.put("job_uuid", job_uuid)
        params.put("signature_media", signature_media)
        return this
    }

    fun notifyByEmail(job_uuid: String? = null): URLApi {
        method = NetworkMethod.POST
        path = "notify-job-creation"
        params = JSONObject()
        params.put("job_uuid", job_uuid)
        return this

    }

    fun finishJob(
        job_uuid: String? = null,
        worker_remarks: String? = null,
        charged_amount: String? = null,
        is_paid: String? = null,
    ): URLApi {
        method = NetworkMethod.POST
        path = "finish-job"
        params = JSONObject()
        params.put("job_uuid", job_uuid)
        params.put("worker_remarks", worker_remarks)
        params.put("charged_amount", charged_amount)
        params.put("is_paid", is_paid)
        return this
    }

    fun getJobLoistByTimeNature(
        worker_uuid: String? = null,
        time_nature: String? = null,

        ): URLApi {

        method = NetworkMethod.POST
        path = "get-job-list-by-time-nature"
        params = JSONObject()
        params.put("worker_uuid", worker_uuid)
        params.put("time_nature", time_nature)

        return this
    }

    fun updateJobStatus(
        job_uuid: String? = null,
        status: String? = null,
        end_time: String? = null,
    ): URLApi {

        method = NetworkMethod.POST
        path = "update-job-status"
        params = JSONObject()
        params.put("job_uuid", job_uuid)
        params.put("status", status)
        params.put("end_time", end_time)


        return this
    }

    fun updateBookingStatus(
        booking_uuid: String? = null,
        status: String? = null,
    ): URLApi {

        method = NetworkMethod.POST
        path = "update-booking-status"
        params = JSONObject()
        params.put("booking_uuid", booking_uuid)
        params.put("status", status)


        return this
    }


    fun addJob(
        job_uuid: String? = null,
        customer_uuid: String? = null,
        driver_uuid: String? = null,
        price_nature: String? = null,
        minimum_amount: String? = null,
        price_amount: String? = null,

        job_type: String? = null,
        is_approved: String? = null,
        job_status: String? = null,
        start_time: String? = null,
        end_time: String? = null,
        charged_amount: String? = null,
        signature_media: String? = null,
        pickup_address1: String? = null,
        drop_address1: String? = null,
    ): URLApi {
        method = NetworkMethod.POST
        path = "update-job"
        params = JSONObject()
        params.put("job_uuid", job_uuid)
        params.put("customer_uuid", customer_uuid)
        params.put("driver_uuid", driver_uuid)
        params.put("price_nature", price_nature)
        params.put("minimum_amount", minimum_amount)
        params.put("price_amount", price_amount)
        params.put("job_type", job_type)
        params.put("is_approved", is_approved)
        params.put("job_status", job_status)
        params.put("start_time", start_time)
        params.put("end_time", end_time)
        params.put("charged_amount", charged_amount)
        params.put("signature_media", signature_media)
        params.put("pickup_address1", pickup_address1)
        params.put("drop_address1", drop_address1)
        return this
    }

    fun getExpensis(id :String):URLApi{
        method = NetworkMethod.GET
        path="get-expensive/"+id
        return this
    }


    fun getInvoice(job_uuid: String): URLApi {
        method = NetworkMethod.POST
        path = "get-job-inovice"
        params = JSONObject()
        params.put("job_uuid", job_uuid)

        return this
    }

    fun getSpecificJob(job_uuid: String): URLApi {
        method = NetworkMethod.POST
        path = "get-job"
        params = JSONObject()
        params.put("job_uuid", job_uuid)

        return this
    }

    fun validateToken(email: String, code: String): URLApi {
        method = NetworkMethod.POST
        path = "auth/validate-token"
        params = JSONObject()
        params.put("email", email)
        params.put("code", code)
        return this
    }


    object Randomized {
        fun generate(min: Int, max: Int): Int {
            return min + (Math.random() * (max - min + 1)).toInt()
        }
    }

    enum class PaymentMethod(val value: String) {
        STRIPE("stripe"),
        VESICASH("vesicash"),
        FREE("free"),
        KOINAHI("empty")
    }
}
