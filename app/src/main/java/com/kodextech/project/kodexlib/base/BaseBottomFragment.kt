package com.kodextech.project.kodexlib.base

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kodextech.project.kodexlib.utils.HideUtil


abstract class BaseBottomFragment : BottomSheetDialogFragment() {
    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private lateinit var mContext: Context
    private lateinit var mActivity: BaseActivity

    fun currentActivity(): BaseActivity = mActivity
    fun currentContext(): Context = mContext
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        mActivity = context as BaseActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setupContentViewWithBinding(inflater, container, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onSetupArguments()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HideUtil.init(mActivity)
        onBindItemListenerOrViewVariables()
    }

    abstract fun onSetupArguments()
    abstract fun onBindItemListenerOrViewVariables()
    abstract fun setupContentViewWithBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View

    abstract fun onRecycleBeforeDestroy()
    abstract fun onBecameInvisibleToUser()
    abstract fun onBecameVisibleToUser()


    override fun onDestroy() {
        onRecycleBeforeDestroy()
        super.onDestroy()
    }

    override fun onResume() {
        onBecameVisibleToUser()
        super.onResume()
    }

    override fun onPause() {
        onBecameInvisibleToUser()
        super.onPause()
    }


    final override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            onBecameVisibleToUser()
        } else {
            onBecameInvisibleToUser()
        }
    }


    fun showLoading(text: String = "Please wait...") {
        mActivity.showLoading(text)
    }


    fun hideLoading() {
        mActivity.hideLoading()
    }
}