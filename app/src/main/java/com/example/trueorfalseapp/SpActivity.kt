package com.example.trueorfalseapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sp)

        val classicButton= findViewById<Button>(R.id.buttonclassic)
        val eventButton= findViewById<Button>(R.id.buttonEvent)

        classicButton.setOnClickListener {
            val intent = Intent(this,ClassicActivity::class.java)
            startActivity(intent)
        }
        eventButton.setOnClickListener {
            Toast.makeText(this,"Currently not available",Toast.LENGTH_SHORT).show()
        }
    }
}