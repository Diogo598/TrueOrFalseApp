package com.example.trueorfalseapp
import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MyApplication:Application() {
      override fun onCreate() {
          super.onCreate()
          FirebaseApp.initializeApp(this)
          FirebaseFirestore.setLoggingEnabled(true)
      }
  }