package com.example.trueorfalseapp

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class AchievementActivity : AppCompatActivity() {
    private lateinit var achievement1image: ImageView
    private lateinit var drawimage:Drawable
    private var jsonManager=JsonManager
    private var totaltrophies=8

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievement)

        for (i in 1..totaltrophies) {
            checkTrophyById(i)
        }


    }
    fun locktrophy(trophyId:Int){

        val defaultDrawable=R.drawable.achfdefault
        val drawImage = ContextCompat.getDrawable(this, defaultDrawable)

        val imageViewId = resources.getIdentifier("imageViewach${trophyId}", "id", packageName)
        val achievementImage = findViewById<ImageView>(imageViewId)
        achievementImage?.setImageDrawable(drawImage)
    }

    fun unlocktrophy(trophyId:Int){
        val actualId = "ach${trophyId}t"
        val resourceId = resources.getIdentifier(actualId, "drawable", packageName)
        val drawImage = ContextCompat.getDrawable(this, resourceId)

        val imageViewId = resources.getIdentifier("imageViewach${trophyId}", "id", packageName)
        val achievementImage = findViewById<ImageView>(imageViewId)
        achievementImage.setImageDrawable(drawImage)
    }

    fun checkTrophyById (trophyId: Int){

        var trophy =jsonManager.getEntryFromGameTimeAchievement(this,trophyId)

       if (trophy.unlocked==true){
           unlocktrophy(trophyId)
       }
        else {
            locktrophy(trophyId)
       }
    }
}