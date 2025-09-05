package com.example.schedulemanager.extention

import android.util.Log
import androidx.fragment.app.Fragment

open class BaseFragment: Fragment() {
    val TAG = this.javaClass.simpleName
    override fun onStart() {
        super.onStart()
        Log.v(TAG,"${this.javaClass.simpleName} onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.v(TAG,"${this.javaClass.simpleName} onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.v(TAG,"${this.javaClass.simpleName} onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.v(TAG,"${this.javaClass.simpleName} onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG,"${this.javaClass.simpleName} onDestroy")
    }



}