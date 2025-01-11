package com.example.trueorfalseapp
import android.content.Context
import com.google.gson.Gson
import java.io.File
import java.io.FileInputStream
import java.io.IOException

object JsonManager {
    var achievementsjson="achievements.json"
    var userdatajson="userdata.json"

    data class UserData(
        val games_played:Int,
        val highscorestreak: Int,
        val coins: Int,
    )
    data class onlineData(
        val onlineGamesPlayed:Int,
        val onlineWins: Int
    )

    data class GameTimeAchievement(
        val nr: Int,
        val description: String,
        val condition: Int,
        var unlocked: Boolean
    )
    data class UserDataResponse(
        val userdata: List<UserData>)

    data class AchievementsResponse(
        val gametime_achievements: List<GameTimeAchievement>)

    private fun readAssetFileAchievements(context: Context,fileName: String):Array<AchievementsResponse>? {
        // Read the file and parse it
        val achievementDataResponse = readFileUsingGson(context, fileName, Array<AchievementsResponse>::class.java)

        // Print the parsed data
        if (achievementDataResponse != null) {
            //println(myData)
        } else {
            println("Failed to parse the file.")
        }

        return achievementDataResponse
    }

    private fun readAssetFileUserData(context: Context,fileName: String):Array<UserDataResponse>? {

        // Read the file and parse it
        val achievementDataResponse = readFileUsingGson(context, fileName, Array<UserDataResponse>::class.java)

        // Print the parsed data
        if (achievementDataResponse != null) {
            //println(myData)
        } else {
            println("Failed to parse the file.")
        }

        return achievementDataResponse
    }

    fun <T> readFileUsingGson(context: Context, fileName: String, clazz: Class<T>): T? {
        return try {
            context.assets.open(fileName).bufferedReader().use { reader ->
                Gson().fromJson(reader, clazz)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    fun saveJsonToFile(context: Context, jsonString: String, fileName: String) {
        try {
            // Open a file output stream
            val outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)

            // Write the JSON string to the output stream
            outputStream.write(jsonString.toByteArray())

            // Close the output stream
            outputStream.close()

            // Optionally, you can print a message indicating success
            println("JSON saved to $fileName")
        } catch (e: Exception) {
            // Handle exceptions (e.g., IOException)
            e.printStackTrace()
        }
    }

    fun openachievmentjson(context: Context, fileName: String):Array<AchievementsResponse>?{
        try {
            val file = File(context.filesDir, fileName)
            val inputStream = FileInputStream(file)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val jsonString = String(buffer, Charsets.UTF_8)
            val achievementlist = Gson().fromJson(jsonString,Array<AchievementsResponse>::class.java)

            return achievementlist
        }catch (e:Exception){
            val achievementlist=readAssetFileAchievements(context,achievementsjson)
            return achievementlist
        }
    }
    fun getGameTimeAchievements(achievementsResponse:Array<AchievementsResponse>?): List<GameTimeAchievement>? {
        var achievementsList= achievementsResponse?.get(0)?.gametime_achievements
        return achievementsList
    }

    fun getAchievementByNr(nr:Int,achievementlist:List<GameTimeAchievement>?):GameTimeAchievement {
        var actualNr = -1
        lateinit var desiredEntry:GameTimeAchievement
        println(achievementlist)

        if (achievementlist != null) {
            for (entry in achievementlist) {

                actualNr = entry.nr

                if (nr == actualNr) {
                    desiredEntry= entry
                    break
                }
            }
        }
        return desiredEntry
    }
    //Achievement reading
    fun readCondition(achievement:GameTimeAchievement):Int{
        var condition = achievement.condition
        return condition
    }
    fun readDescription(achievement:GameTimeAchievement):String{
        var description = achievement.description
        return description
    }
    fun readUnlocked(achievement:GameTimeAchievement):Boolean{
        var isUnlocked = achievement.unlocked
        return isUnlocked
    }
    fun getEntryFromGameTimeAchievement(context: Context,nr: Int):GameTimeAchievement{
        var achievementsj=achievementsjson
        var achievementsResponse= openachievmentjson(context,achievementsj)
        var achievementsList= getGameTimeAchievements(achievementsResponse)
        var achievementEntry=getAchievementByNr(nr,achievementsList)
        return achievementEntry
    }

    fun updateEntryAchievementDataJson(context: Context,fileName: String, nr: Int,unlocked: Boolean){
        //Lesen von JSON
        var achievementsResponse= openachievmentjson(context,fileName)
        val achievementlist=getGameTimeAchievements(achievementsResponse)
        var achievementEntry=getAchievementByNr(nr,achievementlist)
        achievementEntry.unlocked=unlocked

        saveAchievementDataJson(context,fileName,achievementlist)
        println("Achievements should Update here")
    }

    fun saveAchievementDataJson(context: Context,fileName: String,achievementlist: List<GameTimeAchievement>?){

        val achievementJson = listOf(
            mapOf(
                "gametime_achievements" to achievementlist
            )
        )
        val jsonString = Gson().toJson(achievementJson)

        saveJsonToFile(context,jsonString, fileName)
    }
    fun getUnlocksFromEntryAchievementDataJson(context: Context,nr: Int):Boolean{

        val achievementEntry=getEntryFromGameTimeAchievement(context,nr)
        val isUnlocked=readUnlocked(achievementEntry)

        return isUnlocked
    }



    //UserData
    fun openuserjson (context: Context, fileName: String):Array<UserDataResponse>?{
        try {
            val file = File(context.filesDir, fileName)
            val inputStream = FileInputStream(file)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val jsonString = String(buffer, Charsets.UTF_8)
            val userDataResponse = Gson().fromJson(jsonString,Array<UserDataResponse>::class.java)
            return userDataResponse
        }catch (e:Exception){
            val userDataResponse=readAssetFileUserData(context,fileName)
            return userDataResponse
        }



    }
    fun getUserData(userDataResponse:Array<UserDataResponse>?): List<UserData>? {
        var userDataList= userDataResponse?.get(0)?.userdata
        return userDataList
    }

    fun compareHighScore(currentHighScoreStreak: Int,userHighScoreStreak:Int):Boolean{
        if (currentHighScoreStreak>userHighScoreStreak){
            return true
        }
        else{
            return  false
        }

    }

    fun saveUserDataJson(context: Context,fileName: String,games_played:Int,highscorestreak: Int,coins: Int){

        val userDataJson = listOf(
            mapOf(
                "userdata" to listOf(UserData(games_played = games_played, highscorestreak = highscorestreak, coins = coins))
                )
        )
        val jsonString = Gson().toJson(userDataJson)

        saveJsonToFile(context,jsonString, fileName)
    }

    fun editEntryFromUserData(context: Context,fileName: String, userDataList:List<UserData>?, earnedcoins: Int,currentHighScoreStreak: Int,games_played: Int){
        var usercoin = userDataList?.get(0)?.coins?: 0
        var userHighScoreStreak = userDataList?.get(0)?.highscorestreak?: 0
        var usergamesplayed = userDataList?.get(0)?.games_played?: 0

        usergamesplayed+=games_played
        usercoin+=earnedcoins

        var isNewHighscore=compareHighScore(currentHighScoreStreak, userHighScoreStreak)

        if (isNewHighscore==true){
            userHighScoreStreak=currentHighScoreStreak
        }

        print("gamesplayed: $usergamesplayed")
        print("coins: $usercoin")
        print("highscore: $userHighScoreStreak")

        saveUserDataJson(context,fileName,usergamesplayed,userHighScoreStreak,usercoin)
    }


    fun updateUserData(context: Context,fileName: String, earnedcoins: Int,currentHighScoreStreak: Int,games_played: Int){

        val userDataResponse=openuserjson(context,fileName)
        val userDataList=getUserData(userDataResponse)
        editEntryFromUserData(context,fileName,userDataList,earnedcoins,currentHighScoreStreak,games_played)
    }

    //Userdata reading
    fun readUsergamesplayed(udentry:UserData):Int{
        var userGamesPlayed = udentry.games_played
        return userGamesPlayed
    }
    fun readUserHighscore(udentry:UserData):Int{
        var highscore = udentry.highscorestreak
        return highscore
    }
    fun readUserCoins(udentry: UserData):Int{
        var coins = udentry.coins
        return coins
    }
    fun getActualUserData(context: Context, userDataList: List<UserData>?):UserData {
        var userdata = userDataList?.get(0)
        if (userdata != null) {
            return userdata
        }
        else{
            throw Exception()
        }
    }
    fun getUserDataCoins(context: Context,fileName: String):Int{

        val userDataResponse=openuserjson(context,fileName)
        val userDataList=getUserData(userDataResponse)
        val userDataClass=getActualUserData(context,userDataList)
        val userDataCoins =readUserCoins(userDataClass)

        return userDataCoins
    }
    fun getUserDataGamesPlayed(context: Context,fileName: String):Int{

        val userDataResponse=openuserjson(context,fileName)
        val userDataList=getUserData(userDataResponse)
        val userDataClass=getActualUserData(context,userDataList)
        val userDataGamesplayed =readUsergamesplayed(userDataClass)

        return userDataGamesplayed
    }
    fun getUserDataHighScore(context: Context,fileName: String):Int{

        val userDataResponse=openuserjson(context,fileName)
        val userDataList=getUserData(userDataResponse)
        val userDataClass=getActualUserData(context,userDataList)
        val userDataHighscore =readUserHighscore(userDataClass)

        return userDataHighscore
    }

}