package com.example.trueorfalseapp
import android.content.Context
import com.example.trueorfalseapp.JsonManager
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import kotlin.random.Random

private var actualQuantity=1
private var actualPrice=0
private var unlocksManager= UnlocksManager
private val jsonManager=JsonManager
class ShopActivity : AppCompatActivity() {

   /* private var unlockList = listOf<String>(
        "c1",
        "c2",
        "c3"
        )*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)
        var availableCoins=jsonManager.getUserDataCoins(this,"userdata.json")
        var textViewQuantiy = findViewById<TextView>(R.id.textViewQuantity)
        var buttonAdd= findViewById<Button>(R.id.buttonAdd)
        var buttonSub= findViewById<Button>(R.id.buttonSubtract)
        var textViewCostLabel = findViewById<TextView>(R.id.textViewCostLabel)
        var textViewTotalCost = findViewById<TextView>(R.id.textViewTotalCost)
        var buttonOdds = findViewById<Button>(R.id.buttonOdds)
        var layoutOdds=findViewById<RelativeLayout>(R.id.relativeOdds)
        var textViewCurrentCoins=findViewById<TextView>(R.id.textViewCurrentCoins)
        var buyButton=findViewById<Button>(R.id.buttonBuy)
        var homeButton=findViewById<Button>(R.id.buttonHome)
        var adRemoveButton=findViewById<Button>(R.id.buttonAdsRemove)

       var filenameUserdata="userdata.json"
       var earnedCoins= jsonManager.getUserDataCoins(this,filenameUserdata)


        textViewCurrentCoins.text=availableCoins.toString();
        showActualCoins(textViewCostLabel,textViewTotalCost)

       adRemoveButton.setOnClickListener {
           Toast.makeText(this,"Coming Soon!",Toast.LENGTH_SHORT).show()
       }

        buyButton.setOnClickListener {
            var totalCost =textViewTotalCost.text.toString().toInt()
            if (totalCost< 0){
            Toast.makeText(this,"You dont have enough Coins",Toast.LENGTH_SHORT).show()
            }
            else{
                openCase()
                //Only one Case at the Moment, dont hardcode(earnedcoin) in future
                jsonManager.updateUserData(this,filenameUserdata,-100,0,0)
                startActivity(Intent(this,Animation::class.java))
            }
        }
       homeButton.setOnClickListener {
           startActivity(Intent(this,MainActivity::class.java))
       }

        buttonAdd.setOnClickListener {
            Toast.makeText(this,"This feature will be working in the future",Toast.LENGTH_SHORT).show()
           //Can't open multiple Cases at the moment, will be added in the future
            /* actualQuantity += 1
            textViewQuantiy.text = actualQuantity.toString()
            showActualCoins(textViewCostLabel,textViewTotalCost)
            checkIfNegativ(textViewTotalCost)*/
        }
        buttonSub.setOnClickListener {
            Toast.makeText(this,"This feature will be working in the future",Toast.LENGTH_SHORT).show()
            /*if(actualQuantity>1){
            actualQuantity -= 1
            textViewQuantiy.text = actualQuantity.toString()
            showActualCoins(textViewCostLabel,textViewTotalCost)
            checkIfNegativ(textViewTotalCost)
            }
            else{
                Toast.makeText(this,"Quantity cant be lower than 1",Toast.LENGTH_SHORT).show()
            }*/
        }
        buttonOdds.setOnClickListener {
            changeOddsVisibiltiy(layoutOdds)
        }

    }
   private fun changeOddsVisibiltiy(layoutOdds:RelativeLayout){
        if (layoutOdds.visibility == View.VISIBLE){
            layoutOdds.visibility = View.GONE
        }
        else {
           layoutOdds.visibility= View.VISIBLE
        }
    }

    private fun showActualCoins(textViewCostLabel: TextView,textViewTotalCost:TextView) {
        var availableCoins=jsonManager.getUserDataCoins(this,"userdata.json")
        actualPrice = actualQuantity * 100
        if (actualPrice <= availableCoins) {
            textViewCostLabel.text = "After this your Coins will be"
            textViewTotalCost.text = (availableCoins - actualPrice).toString()
        } else {
            textViewCostLabel.text = "You are missing a few Coins"
            textViewTotalCost.text = (availableCoins - actualPrice).toString()
        }
    }
    private fun checkIfNegativ(textViewCost: TextView){
        val cost=textViewCost.text.toString().toInt()
        if (cost< 0){
            textViewCost.setTextColor(Color.RED)
        }
        else{
           textViewCost.setTextColor(Color.parseColor("#808080"))
        }
    }
    private fun openCase(){
        var randNumber=Random.nextInt(100)
        var currentMap= unlocksManager.getUnlock(this)
        Log.d("CurrentMap","This returns$currentMap")

        if (currentMap != null) {
            if (randNumber in 1..10 && unlocksManager.checkForDuplicates(this,"c1") == false) {
                Log.d("caseunbox", "c1 unlocked")
                unlocksManager.getUnlock(this)
                unlocksManager.updateUnlock(this, "c1", true)
                unlocksManager.currentUnlock("c")
            }

            else if (randNumber in 11..20 && unlocksManager.checkForDuplicates(this,"c2") == false) {
                Log.d("caseunbox", "c2 unlocked")
                unlocksManager.updateUnlock(this, "c2",true)
                unlocksManager.currentUnlock("c")
            }
            else if (randNumber in 21..30&& unlocksManager.checkForDuplicates(this,"c3") == false){ //&& !currentMap.contains("c3")) {
                Log.d("caseunbox", "c3 unlocked")
                unlocksManager.updateUnlock(this, "c3", true)
                unlocksManager.currentUnlock("c")
            }
            else if (randNumber in 31..40 && unlocksManager.checkForDuplicates(this,"c4") == false){
                Log.d("caseunbox", "c4 unlocked")
                unlocksManager.updateUnlock(this, "c4", true)
                unlocksManager.currentUnlock("c")
            }
            else if (randNumber in 41..50 && unlocksManager.checkForDuplicates(this,"c5") == false) {
                Log.d("caseunbox", "c5 unlocked")
                unlocksManager.updateUnlock(this, "c5", true)
                unlocksManager.currentUnlock("c")
            }
            else if (randNumber in 51..60 && unlocksManager.checkForDuplicates(this,"c6") == false) {
                Log.d("caseunbox", "c6 unlocked")
                unlocksManager.updateUnlock(this, "c6", true)
                unlocksManager.currentUnlock("c")
            }
            else if (randNumber in 61..70 && unlocksManager.checkForDuplicates(this,"c7") == false) {
                Log.d("caseunbox", "c7 unlocked")
                unlocksManager.updateUnlock(this, "c7", true)
                unlocksManager.currentUnlock("c")
            }
            else if (randNumber in 71..76 && unlocksManager.checkForDuplicates(this,"uc1") == false){
                Log.d("caseunbox", "uc1 unlocked")
                unlocksManager.updateUnlock(this, "uc1", true)
                unlocksManager.currentUnlock("uc")
            }
            else if (randNumber in 77..83 && unlocksManager.checkForDuplicates(this,"uc2") == false){
                Log.d("caseunbox", "uc2 unlocked")
                unlocksManager.updateUnlock(this, "uc2", true)
                unlocksManager.currentUnlock("uc")
            }
            else if (randNumber in 84..90 && unlocksManager.checkForDuplicates(this,"uc3") == false) {
                Log.d("caseunbox", "uc3 unlocked")
                unlocksManager.updateUnlock(this, "uc3", true)
                unlocksManager.currentUnlock("uc")
            }
            else if (randNumber in 91..94 && unlocksManager.checkForDuplicates(this,"r1") == false){
                Log.d("caseunbox", "r1 unlocked")
                unlocksManager.updateUnlock(this, "r1", true)
                unlocksManager.currentUnlock("r1")
            }
            else if (randNumber in 95..98 && unlocksManager.checkForDuplicates(this,"r2") == false) {
                Log.d("caseunbox", "r2 unlocked")
                unlocksManager.updateUnlock(this, "r2", true)
                unlocksManager.currentUnlock("r2")
            }
            else if (randNumber in 98..100&& unlocksManager.checkForDuplicates(this,"s1") == false){
                Log.d("caseunbox", "special unlocked")
                unlocksManager.updateUnlock(this, "s1", true)
                unlocksManager.currentUnlock("s")
            }
            else{
                unlocksManager.currentUnlock("d")
                //Give back 80 Coins for Duplicates -> One Case effectively costs 20
                jsonManager.updateUserData(this, "userdata.json", 80, 0, 0)
                Log.d("caseunbox", "Duplicate because the random number was $randNumber")
            }
        }
            else {
                unlocksManager.updateUnlock(this,"d",true)
            }
    }
   /* private fun loadUnlocks(entry:String): Boolean{
        val sharedPreferences=getSharedPreferences("UnlocksPreferences",Context.MODE_PRIVATE)
        val unlocks = sharedPreferences.getBoolean(entry,false)
        return unlocks*/
}




        /*for (label in unlockList){
            val unlock = loadUnlocks(label)
            unlocksMap[label] = unlock
        }*/


