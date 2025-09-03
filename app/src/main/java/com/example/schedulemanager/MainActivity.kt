package com.example.schedulemanager

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.schedulemanager.databinding.ActivityMainBinding
import com.example.schedulemanager.logic.Repository
import com.example.schedulemanager.ui.event.EventFragment
import com.example.schedulemanager.ui.plan.PlanFragment
import java.time.LocalDateTime
import java.util.Date

class MainActivity : BaseActivity() {
    lateinit var binding: ActivityMainBinding
    var currentTime = LocalDateTime.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fragmentManager: FragmentManager = supportFragmentManager
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
                R.id.item_event ->{
                    val transaction: FragmentTransaction? = fragmentManager.beginTransaction()
                    transaction?.replace(R.id.frame_fragment_container, EventFragment())
                    transaction?.commit()
                    true
                }
                R.id.item_plan ->{
                    val transaction: FragmentTransaction? = fragmentManager.beginTransaction()
                    transaction?.replace(R.id.frame_fragment_container, PlanFragment())
                    transaction?.commit()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                val startDate = Date(currentTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli())
            }
        }
        return true
    }

}