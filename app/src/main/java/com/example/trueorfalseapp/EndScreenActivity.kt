package com.example.trueorfalseapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EndScreenActivity : AppCompatActivity() {
    private lateinit var totalGamesPlayed:TextView
    private lateinit var currentstreak:TextView
    private lateinit var bestStreak:TextView
    private lateinit var coinsearned:TextView
    private lateinit var totalcoinsAdd:TextView
    var filenameAchievements="achievements.json"
    var filenameUserdata="userdata.json"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_endscreen)
        val streak = intent.getIntExtra("STREAK_KEY",0)
        val jsonManager=JsonManager
        val totalcoins= jsonManager.getUserDataCoins(this,filenameUserdata)
        val gamesplayed= jsonManager.getUserDataGamesPlayed(this,filenameUserdata)
        val beststreak= jsonManager.getUserDataHighScore(this,filenameUserdata)
        val currentlyGamesPlayed = 1
        val earnedCoins= streak*10
        var allGames=gamesplayed+1
        var allCoins=totalcoins+earnedCoins

        totalGamesPlayed=findViewById(R.id.gamesplayedTextView)
        currentstreak=findViewById(R.id.streakTextView)
        bestStreak=findViewById(R.id.highscoreStreakTextView)
        coinsearned=findViewById(R.id.coinsEarnedTextView)
        totalcoinsAdd=findViewById(R.id.totalCoinsTextView)

        totalGamesPlayed.text=allGames.toString()
        currentstreak.text=streak.toString()
        bestStreak.text=beststreak.toString()
        coinsearned.text=earnedCoins.toString()
        totalcoinsAdd.text=allCoins.toString()

        //Loggin Data to see if it works
        Log.d("Actuall UserData is:","\n$totalcoins\n$gamesplayed\n$beststreak\n$streak")


        fun resetAchievements(){
            val liste = arrayOf<Int>(1, 2, 3, 4, 5 ,6 ,7, 8)
            for (number in liste){
                jsonManager.updateEntryAchievementDataJson(this, filenameAchievements, number, false)
            }
        }

        fun achievementPop(){
            val achievementPop=Toast.makeText(this,"You unlocked an Achievement!",Toast.LENGTH_SHORT)
            achievementPop.show()
        }
        //Achievements

        if (allGames==1){
            val isUnlocked=jsonManager.getUnlocksFromEntryAchievementDataJson(this,1)

            if(isUnlocked==false){
                jsonManager.updateEntryAchievementDataJson(this,filenameAchievements,1,true)
                achievementPop()

            }

        }
        if (allGames==5){
            val isUnlocked=jsonManager.getUnlocksFromEntryAchievementDataJson(this,2)

            if(isUnlocked==false){
                achievementPop()
                jsonManager.updateEntryAchievementDataJson(this,filenameAchievements,2,true)
            }
        }
        if (allGames==10){
            val isUnlocked=jsonManager.getUnlocksFromEntryAchievementDataJson(this,3)

            if(isUnlocked==false){
                achievementPop()
                jsonManager.updateEntryAchievementDataJson(this,filenameAchievements,3,true)
            }
        }
        if (allGames==25){
            val isUnlocked=jsonManager.getUnlocksFromEntryAchievementDataJson(this,4)

            if(isUnlocked==false){
                achievementPop()
                jsonManager.updateEntryAchievementDataJson(this,filenameAchievements,4,true)
            }
        }
        if (allGames==50){
            val isUnlocked=jsonManager.getUnlocksFromEntryAchievementDataJson(this,5)

            if(isUnlocked==false){
                achievementPop()
                jsonManager.updateEntryAchievementDataJson(this,filenameAchievements,5,true)
            }
        }
        if (beststreak>3){
            val isUnlocked=jsonManager.getUnlocksFromEntryAchievementDataJson(this,6)

            if(isUnlocked==false){
                achievementPop()
                jsonManager.updateEntryAchievementDataJson(this,filenameAchievements,6,true)
            }

        }
        if (beststreak>10){
            val isUnlocked=jsonManager.getUnlocksFromEntryAchievementDataJson(this,7)

            if(isUnlocked==false){
                achievementPop()
                jsonManager.updateEntryAchievementDataJson(this,filenameAchievements,7,true)
            }
        }
        if (beststreak>25){
            val isUnlocked=jsonManager.getUnlocksFromEntryAchievementDataJson(this,8)
            jsonManager.updateEntryAchievementDataJson(this,filenameAchievements,8,true)

            if(isUnlocked==false){
                achievementPop()
                jsonManager.updateEntryAchievementDataJson(this,filenameAchievements,8,true)
            }
        }

        //reset Achievements
        //resetAchievements()


        // reset UserData
        //jsonManager.updateUserData(this,filenameUserdata,0,0,27)


        jsonManager.updateUserData(this,filenameUserdata,earnedCoins,streak,currentlyGamesPlayed)

        val tryAgainButton= findViewById<Button>(R.id.tryAgainButton)
        tryAgainButton.setOnClickListener {
            val intent = Intent(this,ClassicActivity::class.java)
            startActivity(intent)
        }

        val goHomeButton = findViewById<Button>(R.id.homeButton)
        goHomeButton.setOnClickListener {
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }
}