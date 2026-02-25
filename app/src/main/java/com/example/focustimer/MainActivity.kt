package com.example.focustimer

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "MainActivity OnCreate(Bundle?) called")
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Focus Timer"
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "MainActivity onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "MainActivity onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "MainActivity onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "MainActivity onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MainActivity onDestroy() called")
    }
}