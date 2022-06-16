package com.kodextech.project.kodexlib.ui.main.communication

import androidx.databinding.DataBindingUtil
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.ui.main.communication.adapter.EmialCommunicationAdapter
import com.kodextech.project.kodexlib.ui.main.communication.adapter.SmsCommunicationAdapter
import com.kodextech.project.kodexlib.databinding.ActivityCommunicationBinding

class CommunicationActivity : BaseActivity() {

    private var binding: ActivityCommunicationBinding? = null
    private var emialCommunicationAdapter: EmialCommunicationAdapter? = null
    private var smsCommunicationAdapter: SmsCommunicationAdapter? = null

    override fun onSetupViewGroup() {
        mViewGroup = binding?.rlCommunication
    }

    override fun setupContentViewWithBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_communication)
        setEmailAdapter()
        binding?.ivBack?.setOnClickListener { onBackPressed() }
        binding?.tvemail?.setOnClickListener {

            binding?.tvemail?.setBackgroundColor(getColor(R.color.blue))
            binding?.tvemail?.setTextColor(getColor(R.color.white))
            binding?.tvsms?.setBackgroundColor(getColor(R.color.gray))
            binding?.tvsms?.setTextColor(getColor(R.color.cusCol))
           setEmailAdapter()
        }
        binding?.tvsms?.setOnClickListener {
            binding?.tvsms?.setBackgroundColor(getColor(R.color.blue))
            binding?.tvsms?.setTextColor(getColor(R.color.white))
            binding?.tvemail?.setBackgroundColor(getColor(R.color.gray))
            binding?.tvemail?.setTextColor(getColor(R.color.cusCol))
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

    fun setEmailAdapter(){
        emialCommunicationAdapter = EmialCommunicationAdapter(this)
        binding?.rvMails?.adapter = emialCommunicationAdapter
    }

    fun setSmsAdapter(){
        smsCommunicationAdapter = SmsCommunicationAdapter(this)
        binding?.rvMails?.adapter = smsCommunicationAdapter
    }
}