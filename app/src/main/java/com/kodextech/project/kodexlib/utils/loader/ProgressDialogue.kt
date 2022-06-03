package com.kodextech.glitterupsapp.utils.loader

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.github.ybq.android.spinkit.style.Circle
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseDialogueFragment


class ProgressDialogue : BaseDialogueFragment() {

    var txtProgress: AppCompatTextView? = null
    var mCircle: Circle? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }


    override fun onSetupArguments() {

    }

    override fun onBindItemListenerOrViewVariables() {
        mCircle = Circle()
        mCircle?.setBounds(0, 0, 100, 100)
        mCircle?.color = Color.BLUE
        mCircle?.color = R.color.blue
        isCancelable = true


    }

    override fun setupContentViewWithBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.loader_view, container, false)
    }

    override fun onRecycleBeforeDestroy() {

    }

    override fun onBecameInvisibleToUser() {

    }

    override fun onBecameVisibleToUser() {

    }

    override fun onResume() {
        super.onResume()
        startAnim()
    }

    override fun onPause() {
        super.onPause()
        stopAnim()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopAnim()
    }

    private fun startAnim() {
//        avi?.show()
        mCircle?.start()

    }

    private fun stopAnim() {
//        avi?.hide()
        mCircle?.stop()

    }
}