package com.example.schedulemanager

import android.R.attr.uiOptions
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity: AppCompatActivity() {

    override fun onDestroy() {
        super.onDestroy()
        Log.v("test", "BaseActivity onDestroy")
    }

    override fun onResume() {
        super.onResume()

    }
}