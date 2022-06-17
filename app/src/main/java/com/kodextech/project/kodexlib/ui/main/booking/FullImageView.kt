package com.kodextech.project.kodexlib.ui.main.booking

import android.util.Log
import androidx.databinding.DataBindingUtil
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivityFullImageViewBinding

class FullImageView : BaseActivity() {

    var binding: ActivityFullImageViewBinding? = null
    var array: ArrayList<SlideModel>? = ArrayList()

    override fun onSetupViewGroup() {
        mViewGroup = binding?.imageViewContent
    }

    override fun setupContentViewWithBinding() {
        statusBarColor(getColor(R.color.blue))
        binding = DataBindingUtil.setContentView(this, R.layout.activity_full_image_view)

        val from = intent.getStringExtra("from")


        AddBooking.scrollImageList.forEachIndexed {
                index, mediaModel ->
            array?.add(
                SlideModel(
                    "http://moderns.modernmover.co.uk/modern-movers/public/uploads/" + mediaModel.path,
                    "",
                    ScaleTypes.CENTER_CROP
                )
            )

        }




        binding?.imageSlider?.setImageList(ArrayList(array))


    }

    override fun onRecycleBeforeDestroy() {

    }

    override fun onBecameInvisibleToUser() {

    }

    override fun onBecameVisibleToUser() {

    }

    override fun setupLoader() {

    }

}