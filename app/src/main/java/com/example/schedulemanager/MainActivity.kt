package com.example.schedulemanager

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.schedulemanager.logic.Repository
import com.example.schedulemanager.logic.model.Event
import com.example.schedulemanager.logic.model.EventData
import com.example.schedulemanager.logic.model.Plan
import com.example.schedulemanager.logic.model.TYPE
import com.example.schedulemanager.ui.theme.ScheduleManagerTheme
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
    }
    private val tag = "test"

    override fun onStart() {
        super.onStart()

    }
}