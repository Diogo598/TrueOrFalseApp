package com.example.trueorfalseapp


import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private var unlockMananger=UnlocksManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val singleplayerButton = findViewById<Button>(R.id.buttonsp)
        singleplayerButton.setOnClickListener{
            val intent= Intent(this,ClassicActivity::class.java)
            startActivity(intent)
        }

        var multiplayerButton=findViewById<Button>(R.id.buttonMultiplayer)
            multiplayerButton.setOnClickListener {
                Toast.makeText(this,"Currently not implemented",Toast.LENGTH_SHORT).show()
            }

        val optionsButton = findViewById<Button>(R.id.buttonoptions)
        optionsButton.setOnClickListener{
            val intent= Intent(this,ShopActivity::class.java)
            startActivity(intent)
        }
        val profileButton = findViewById<Button>(R.id.buttonProfile)
        profileButton.setOnClickListener{
            val intent= Intent(this,ProfileActivity::class.java)
            startActivity(intent)
        }
        val achievementclass= JsonManager

        var achievementEntry=achievementclass.getEntryFromGameTimeAchievement(this,5)

        var unlocked=achievementclass.readUnlocked(achievementEntry)

        checkUnlocks()



    }
    private fun checkUnlocks() {
        val status = unlockMananger.getUnlock(this)

        if (status != null){
            return
        }

        Log.d("CURRENTUNLOCKS", status.toString())

        val unlockKeys = arrayOf<String>(
            "c1",
            "c2",
            "c3",
            "c4",
            "c5",
            "c6",
            "c7",
            "uc1",
            "uc2",
            "uc3",
            "r1",
            "r2",
            "s1"
        )
            unlockKeys.map { key ->
            val unlocked=status?.get(key) ?: false
            Log.d("ProfileActivity", "$key unlocked: $unlocked")
            unlocked
        }

        // initialise all to false
        for (unlocks in unlockKeys){
            unlockMananger.updateUnlock(this, unlocks, false)
        }

    }
}