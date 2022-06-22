package com.kodextech.project.kodexlib

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.ui.main.communication.adapter.EmialCommunicationAdapter

class MultipleSelectionActivity : AppCompatActivity() {
    private var mulSelAdapter: MulSelAdapter? = null
    private var   listRv : RecyclerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiple_selection)

        listRv =findViewById(R.id.listRv)
        setAdapter()
    }

    fun setAdapter(){
        mulSelAdapter = MulSelAdapter(this)
        listRv?.adapter = mulSelAdapter



    }
}