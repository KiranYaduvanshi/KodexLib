package com.kodextech.project.kodexlib.ui.main.quatation

import CustomerSearchModel
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.coroutineScope
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivitySearchCustomerBinding
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import com.kodextech.project.kodexlib.utils.generateList
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import org.json.JSONArray
import java.util.*
import kotlin.concurrent.timerTask


class SearchCustomerActivity : BaseActivity(), SearchCustomerAdapter.SelectName {


    private var binding: ActivitySearchCustomerBinding? = null
    private var customerAdapter: SearchCustomerAdapter? = null
    private var mData = ArrayList<CustomerSearchModel>()

    override fun onSetupViewGroup() {
        mViewGroup = binding?.customerNameLL

    }

    override fun setupContentViewWithBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_customer)
        searchData()
        binding?.ivBack?.setOnClickListener {
             onBackPressed();
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

    private fun setCustomerAdapter() {
        customerAdapter = SearchCustomerAdapter(this, mData, this)
        binding?.customerNameListRv?.adapter = customerAdapter
    }

    private fun getCustomerList(customerName: String) {
        showLoading()
        NetworkClass.callApi(URLApi.getCustomerNameList(name = customerName), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {

                hideLoading()
                val json = JSONArray(response ?: "")
                val data = generateList(json.toString(), Array<CustomerSearchModel>::class.java)
                //  Toast.makeText(binding?.root?.context, "response--- "+response, Toast.LENGTH_SHORT).show()

                Log.i("data", "------" + data)
                mData.addAll(data)
                setCustomerAdapter()
            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                Toast.makeText(binding?.root?.context, "Error--- " + response, Toast.LENGTH_SHORT)
                    .show()
                Log.i("Search", "onErrorResponse: $error")

            }
        })
    }


    fun EditText.textChanges(): Flow<CharSequence?> {
        return callbackFlow<CharSequence?> {
            val listener = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) = Unit
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) = Unit

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    trySend(s)
                }
            }
            addTextChangedListener(listener)
            awaitClose { removeTextChangedListener(listener) }
        }.onStart { emit(text) }
    }

    val timer = Timer()
    fun delayFunctoin() {
        timer.cancel();
        timer.schedule(timerTask {
            Toast.makeText(
                binding?.root?.context,
                "Search",
                Toast.LENGTH_SHORT
            ).show()
        }, 2000);
    }


    private fun searchData() {
//        binding?.searchEt?.setOnEditorActionListener(object : TextView.OnEditorActionListener {
//            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//
//                    getCustomerList(v?.text.toString())
//                  //  Toast.makeText(this@SearchCustomerActivity, binding?.searchEt?.text.toString()+" ${v?.text.toString()}", Toast.LENGTH_SHORT).show()
//                    return true
//                }
//                return false
//            }
//        })


        binding?.searchEt?.textChanges()?.debounce(1000)?.distinctUntilChanged()?.onEach {
//            Toast.makeText(
//                binding?.root?.context,
//                ""+it.toString(),
//                Toast.LENGTH_SHORT
//            ).show()

            if (it.toString() == "") {

            } else {
                getCustomerList(it.toString())
                customerAdapter?.notifyDataSetChanged()
            }

        }?.launchIn(lifecycle.coroutineScope)

//      bi
        //      nding?.searchEt?.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//               // getCustomerList(s.toString())
//
//            }
//            override fun afterTextChanged(s: Editable) {
//                binding?.searchEt?.requestFocus()
//
//                if (binding?.searchEt?.text?.isEmpty() == true){
//                    mData.clear()
//                    customerAdapter?.notifyDataSetChanged()
//
//
//                }
//                else{
//                    getCustomerList(s.toString())
//                    customerAdapter?.notifyDataSetChanged()
//
//                }
//             //   binding?.searchEt?.setSelection(s.toString().length)
//
//
//            }
//        })

    }

    override fun onClickName(name: String, email: String) {

        val intent = Intent()
        intent.putExtra("name", name)
        intent.putExtra("email", email)
        setResult(RESULT_OK, intent)
        finish()

    }


}