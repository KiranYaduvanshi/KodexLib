package com.kodextech.project.kodexlib.ui.main

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kodextech.project.kodexlib.R
import ir.shahabazimi.instagrampicker.InstagramPicker
import ir.shahabazimi.instagrampicker.classes.SingleListener
import java.io.File

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        view.findViewById<TextView>(R.id.message).setOnClickListener {
            val `in` = InstagramPicker(requireActivity())//(activity)

            `in`.show(0,0,object :SingleListener{
                override fun selectedPic(address: String?) {
                    val filePath = Uri.parse(address)//File(address)
                    val file = File(filePath.path)//filePath.toFile()
//                    val key = "${localAppUser?.id}${
//                        UUID.randomUUID().toString()
//                    }" // TODO: Get username from auth
                }

            })//show(0, 0)
        }
    }

}