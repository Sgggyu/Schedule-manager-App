package com.example.schedulemanager

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.room.Transaction
import com.example.schedulemanager.databinding.ActivityMainBinding
import com.example.schedulemanager.logic.Repository
import com.example.schedulemanager.ui.event.EventFragment
import com.example.schedulemanager.ui.plan.PlanFragment
import java.time.LocalDateTime
import java.util.Date

class MainActivity : BaseActivity() {
    lateinit var binding: ActivityMainBinding
    var currentTime = LocalDateTime.now()
    private var currentFragment: Fragment? = null
    val fragmentManager: FragmentManager = supportFragmentManager
    val eventFragment = EventFragment()
    val planFragment = PlanFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intentType = intent?.getStringExtra("intentType")

        when(intentType){
            "fromPlanNotification" -> {
                if (Repository.isPlanSaved()){
                    Repository.clearPlanInfo()
                }
                val planId = intent?.getIntExtra("planId",0) ?: 0
                val planName = intent?.getStringExtra("planName") ?: ""
                val planType = intent?.getIntExtra("planType",0) ?: 0
                val type = intent?.getStringExtra("type")?:""
                val duration = intent?.getIntExtra("duration",0) ?: 0
                Repository.savePlanInfo(planId,planName,planType,duration)
                Log.v("test","MainActivity,isPlanSaved: ${Repository.isPlanSaved()}")
            }

            else -> {

            }
        }
        binding.bottomNavigation
        binding.bottomNavigation.setOnItemSelectedListener {

            when(it.itemId){
                R.id.item_event -> {
                    switchFragment(eventFragment).commit()
                    true
                }
                R.id.item_plan ->{
                    switchFragment(planFragment).commit()
                    true
                }

                R.id.item_summary ->{
                    Toast.makeText(this,"我的总结 暂未开放", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        binding.bottomNavigation.selectedItemId = R.id.item_event
    }


    override fun onStart() {
        super.onStart()
    }


    //由于eventFragment加载时间过长使用的一种优化策略
    @SuppressLint("CommitTransaction")
    private fun switchFragment(targetFragment: Fragment): FragmentTransaction {
        val transaction = fragmentManager.beginTransaction()
        if(!targetFragment.isAdded){
            if(currentFragment != null){
                transaction.hide(currentFragment!!)}
            transaction.add(R.id.frame_fragment_container,targetFragment,targetFragment.javaClass.name)
        }else{
            transaction.hide(currentFragment!!)
                .show(targetFragment)
        }
        currentFragment = targetFragment
        return transaction
    }
}