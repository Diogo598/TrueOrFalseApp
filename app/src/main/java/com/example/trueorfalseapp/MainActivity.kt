package com.example.trueorfalseapp


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var unlockMananger=UnlocksManager
    private lateinit var radioEnglish:RadioButton
    private lateinit var radioGerman: RadioButton
    private lateinit var languageOkButton:Button
    private lateinit var languageScrollView:ScrollView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        radioEnglish=findViewById(R.id.radioEnglish)
        radioGerman=findViewById(R.id.radioGerman)
        languageOkButton=findViewById(R.id.languageOkButton)
        languageScrollView=findViewById(R.id.languageScrollView)
        val singleplayerButton = findViewById<Button>(R.id.buttonsp)
        singleplayerButton.setOnClickListener{
            val intent= Intent(this,ClassicActivity::class.java)
            startActivity(intent)
        }
        var multiplayerButton=findViewById<Button>(R.id.buttonMultiplayer)
        multiplayerButton.setOnClickListener {
            //val intent= Intent(this,MultiplayerMenuActivity::class.java)
            //startActivity(intent)
            Toast.makeText(this,"Will be implemented soon",Toast.LENGTH_SHORT).show()
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
        val languageButton=findViewById<Button>(R.id.buttonLanguage)
        languageButton.setOnClickListener {
            changeLanguageLayout()
        }
        languageOkButton.setOnClickListener {
                val sharedPreferences = getSharedPreferences("LanguagePreferences", Context.MODE_PRIVATE)
            if (radioGerman.isChecked){
                sharedPreferences.edit().putString("selectedLanguage", "German").apply()
                Toast.makeText(this, "Language was successfully changed to German", Toast.LENGTH_SHORT).show()
            }
            else{
                sharedPreferences.edit().putString("selectedLanguage", "English").apply()
                Toast.makeText(this, "Language was successfully changed to English", Toast.LENGTH_SHORT).show()
            }
            changeLanguageLayout()
        }
        val achievementclass= JsonManager

        var achievementEntry=achievementclass.getEntryFromGameTimeAchievement(this,5)

        var unlocked=achievementclass.readUnlocked(achievementEntry)

        checkUnlocks()



    }
    private fun changeLanguageLayout(){
        if(languageScrollView.visibility== View.GONE){
        languageScrollView.visibility=View.VISIBLE
        }
        else{
            languageScrollView.visibility=View.GONE
        }
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
        unlockKeys.map { key -> val unlocked=status?.get(key) ?: false
            Log.d("ProfileActivity", "$key unlocked: $unlocked")
            unlocked
        }

        // initialise all to false
        for (unlocks in unlockKeys){
            unlockMananger.updateUnlock(this, unlocks, false)
        }

    }
}