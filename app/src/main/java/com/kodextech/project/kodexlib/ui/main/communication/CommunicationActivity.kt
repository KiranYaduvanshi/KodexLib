package com.kodextech.project.kodexlib.ui.main.communication

import android.app.Dialog
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.telephony.SmsManager
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivityCommunicationBinding
import com.kodextech.project.kodexlib.model.Data
import com.kodextech.project.kodexlib.model.Sms
import com.kodextech.project.kodexlib.model.SmsCommunicationModel
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import com.kodextech.project.kodexlib.ui.main.communication.adapter.EmialCommunicationAdapter
import com.kodextech.project.kodexlib.ui.main.communication.adapter.SmsCommunicationAdapter
import com.kodextech.project.kodexlib.ui.main.communication.adapter.emailClickInterface
import com.kodextech.project.kodexlib.ui.main.communication.adapter.viewSmsSelect
import com.kodextech.project.kodexlib.utils.generateList
import org.json.JSONArray
import org.json.JSONObject

class CommunicationActivity : BaseActivity(), emailClickInterface, viewSmsSelect {

    private var binding: ActivityCommunicationBinding? = null
    private var emialCommunicationAdapter: EmialCommunicationAdapter? = null
    private var smsCommunicationAdapter: SmsCommunicationAdapter? = null
    private var mData = ArrayList<Data>()
    private var smsData = ArrayList<SmsCommunicationModel>()
    private var sentReceiver: BroadcastReceiver? = null


    override fun onSetupViewGroup() {
        mViewGroup = binding?.rlCommunication
    }

    override fun setupContentViewWithBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_communication)
        binding?.ivBack?.setOnClickListener { onBackPressed() }
        getEmailCommunicationList()

        binding?.tvemail?.setOnClickListener {
            binding?.tvemail?.setBackgroundColor(getColor(R.color.blue))
            binding?.tvemail?.setTextColor(getColor(R.color.white))
            binding?.tvsms?.setBackgroundColor(getColor(R.color.gray))
            binding?.tvsms?.setTextColor(getColor(R.color.cusCol))
            getEmailCommunicationList()
        }
        binding?.tvsms?.setOnClickListener {
            binding?.tvsms?.setBackgroundColor(getColor(R.color.blue))
            binding?.tvsms?.setTextColor(getColor(R.color.white))
            binding?.tvemail?.setBackgroundColor(getColor(R.color.gray))
            binding?.tvemail?.setTextColor(getColor(R.color.cusCol))
            getSMSCommunicationList()

            setSmsAdapter()
        }
    }

    override fun onRecycleBeforeDestroy() {
    }

    override fun onBecameInvisibleToUser() {
    }

    override fun onBecameVisibleToUser() {
    }

    override fun setupLoader() {
    }

    fun setEmailAdapter() {
        emialCommunicationAdapter = EmialCommunicationAdapter(this, mData, this)
        binding?.rvMails?.adapter = emialCommunicationAdapter
    }

    fun setSmsAdapter() {
        smsCommunicationAdapter = SmsCommunicationAdapter(this, smsData, this)
        binding?.rvMails?.adapter = smsCommunicationAdapter
    }

    private fun getEmailCommunicationList() {
        showLoading()
        NetworkClass.callApi(URLApi.getEmailCommunationApi(), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
                Log.i("Check", "response " + response)
                val json = JSONArray(response ?: "")
                val data = generateList(json.toString(), Array<Data>::class.java)
                // Toast.makeText(binding?.root?.context, "data "+data[0].Date, Toast.LENGTH_SHORT).show()
                // Toast.makeText(binding?.root?.context, "response"+response, Toast.LENGTH_SHORT).show()
                mData.addAll(data)
                setEmailAdapter()

            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                showBarToast(error ?: "")
            }

        })
    }

    private fun getSMSCommunicationList() {
        showLoading()
        NetworkClass.callApi(URLApi.getSmsCommunationApi(), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
                Log.i("Check", "response " + response)
                val json = JSONArray(response ?: "")
                val data = generateList(json.toString(), Array<SmsCommunicationModel>::class.java)
                smsData.addAll(data)
                setSmsAdapter()

            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                showBarToast(error ?: "")
            }

        })
    }

    override fun resendEmail(id: Int?) {
            showLoading()
            NetworkClass.callApi(URLApi.resendEmail(id), object : Response {
                override fun onSuccessResponse(response: String?, message: String) {
                    hideLoading()
                    showBarToast("Email Sent")
                }

                override fun onErrorResponse(error: String?, response: String?) {
                    hideLoading()
                    showBarToast(error ?: "")
                }

            })

    }





    override fun onEmailClick(orderId: String, position: Int) {
        val intent = Intent(this, ViewEmailActivity::class.java)
        intent.putExtra("orderId", orderId)
        startActivity(intent)
    }

    override fun onClickViewSms(sms: String, positon: Int) {
        viewSmsDialog(sms)
    }

    override fun onResendSms(sms: String, phone: String, positon: Int, id: Int?) {
        sendSmsApi(id, sms, phone)
    }


    private fun sendSmsApi(id: Int?, msg: String, phone: String) {
        showLoading()
        NetworkClass.callApi(URLApi.saveSMS(
            booking_id = id,
            sms_text = msg
        ), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
//                showBarToast(message)
                val json = JSONObject(response ?: "")
                val obj = Gson().fromJson(json.toString(), Sms::class.java)
//                 Toast.makeText(binding?.root?.context, "response"+response, Toast.LENGTH_SHORT).show()
                Log.i("Res", "reposne " + response)
//                sendSMS(msg, phone)
                sendSMS(msg,phone)
            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                //  Toast.makeText(binding?.root?.context, "error"+response, Toast.LENGTH_SHORT).show()

                showBarToast(error ?: "")
                Log.i("Res", "error " + response)

            }
        })
    }

    fun sendSMS(smsContent: String , phone: String) {
//        val no = phoneCode + phoneNo
        val sms = SmsManager.getDefault()

        Log.i("SMS", "sendSMS: " + smsContent)

        val parts: java.util.ArrayList<String> = sms.divideMessage(smsContent)
        var msgParts = parts.size

        val mSendIntent = Intent("CTS_SMS_SEND_ACTION")
        Log.i("SMS", "sendSMS: Step 2")
        val no = phone
        val uri = Uri.parse("smsto:$no")
        val SENT = "SMS_SENT"
        val DELIVERED = "SMS_DELIVERED"
//        val sentPI: PendingIntent = PendingIntent.getBroadcast(
//            this, 0,
//            Intent(SENT), 0
//        )
//        val deliveredPI: PendingIntent = PendingIntent.getBroadcast(
//            this, 0,
//            Intent(DELIVERED), 0
//        )
//
//        //---when the SMS has been sent---
//        registerReceiver(object : BroadcastReceiver() {
//            override fun onReceive(arg0: Context?, arg1: Intent?) {
//                when (getResultCode()) {
//                    RESULT_OK -> Toast.makeText(
//                        baseContext, "SMS sent",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> Toast.makeText(
//                        baseContext, "Generic failure",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    SmsManager.RESULT_ERROR_NO_SERVICE -> Toast.makeText(
//                        baseContext, "No service",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    SmsManager.RESULT_ERROR_NULL_PDU -> Toast.makeText(
//                        baseContext, "Null PDU",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    SmsManager.RESULT_ERROR_RADIO_OFF -> Toast.makeText(
//                        baseContext, "Radio off",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }, IntentFilter(SENT))
//
//        //---when the SMS has been delivered---
//        registerReceiver(object : BroadcastReceiver() {
//            override fun onReceive(arg0: Context?, arg1: Intent?) {
//                when (getResultCode()) {
//                    RESULT_OK -> Toast.makeText(
//                        baseContext, "SMS delivered",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    RESULT_CANCELED -> Toast.makeText(
//                        baseContext, "SMS not delivered",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }, IntentFilter(DELIVERED))

        sentReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                var anyError = false
                when (resultCode) {
                    RESULT_OK -> Toast.makeText(
                        baseContext, "SMS sent",
                        Toast.LENGTH_SHORT
                    ).show()
                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> Toast.makeText(
                        baseContext, "Generic failure",
                        Toast.LENGTH_SHORT
                    ).show()
                    SmsManager.RESULT_ERROR_NO_SERVICE -> Toast.makeText(
                        baseContext, "No service",
                        Toast.LENGTH_SHORT
                    ).show()
                    SmsManager.RESULT_ERROR_NULL_PDU -> Toast.makeText(
                        baseContext, "Null PDU",
                        Toast.LENGTH_SHORT
                    ).show()
                    SmsManager.RESULT_ERROR_RADIO_OFF -> Toast.makeText(
                        baseContext, "Radio off",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                msgParts--
                if (msgParts === 0) {
                    binding?.root?.context?.unregisterReceiver(sentReceiver)
                    Log.d("smsresiver", "send ok")
                    if (anyError) {
                        Toast.makeText(
                            context, "sending sms fail",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        registerReceiver(
            sentReceiver, IntentFilter(
                "CTS_SMS_SEND_ACTION"
            )
        )
        val sentIntents = java.util.ArrayList<PendingIntent>()

        for (i in parts.indices) {
            sentIntents.add(
                PendingIntent.getBroadcast(
                    this@CommunicationActivity, 0, mSendIntent,
                    0
                )
            )
        }


        Log.i("SMS", "sendSMS: intent ")

//        sms.sendTextMessage(no, null, smsContent, sentPI, deliveredPI)
//        sms.sendTextMessage("+917973072913", null, smsContent, sentPI, deliveredPI)

        sms.sendMultipartTextMessage(no, null, parts, sentIntents, null)
    }

//    private fun sendSMS(smsContent: String, phone: String) {
//
//        val no = phone
//        val uri = Uri.parse("smsto:$no")
//        try {
//            val smsManager: SmsManager = SmsManager.getDefault()
//            smsManager.sendTextMessage(no, null, smsContent, null, null)
//        } catch (ex: Exception) {
//            Toast.makeText(
//                applicationContext, ex.message.toString(),
//                Toast.LENGTH_LONG
//            ).show()
//            ex.printStackTrace()
//            Log.i("SMS", ex.toString());
//        }
//        val intent = Intent(Intent.ACTION_SENDTO, uri)
//        intent.putExtra("sms_body", smsContent)
//        startActivity(intent)
//    }

    fun viewSmsDialog(sms: String) {

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.view_sms_dialog)
        val smsTv = dialog.findViewById<AppCompatTextView>(R.id.smsTv)
        val crossImg = dialog.findViewById<AppCompatImageView>(R.id.ivCross)

        smsTv.setText(sms)
        crossImg.setOnClickListener { dialog.dismiss() }
        dialog.setCancelable(false)
        dialog.show()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setGravity(Gravity.CENTER)

    }

}