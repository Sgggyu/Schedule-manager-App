package com.example.schedulemanager

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.schedulemanager.databinding.ActivityMainBinding
import com.example.schedulemanager.ui.event.EventFragment
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
        val transaction: FragmentTransaction? = fragmentManager.beginTransaction()
        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.item_event ->{
                    transaction?.replace(R.id.frame_fragment_container, EventFragment())
                    true
                }
                R.id.item_plan ->{
                    Toast.makeText(this,"选定计划 暂未开放", Toast.LENGTH_SHORT).show()
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