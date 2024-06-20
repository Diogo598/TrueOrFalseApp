package com.example.trueorfalseapp

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.Random

class ClassicActivity : AppCompatActivity() {
    private lateinit var streakTextView: TextView
    private lateinit var statementTextView: TextView
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var adLayout:FrameLayout
    private var statements=ArrayList<Statement>()
    private var currentStatementIndex=-1
    private var streak = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classic)

        streakTextView=findViewById(R.id.streakTextView)
        statementTextView=findViewById(R.id.statementTextView)
        trueButton=findViewById(R.id.trueButton)
        falseButton=findViewById(R.id.falseButton)
        adLayout=findViewById(R.id.adLayout)

        loadStatements()
        loadBanner()
        displayNextStatement()

        trueButton.setOnClickListener { checkAnswer(true) }
        falseButton.setOnClickListener { checkAnswer(false) }

        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@ClassicActivity) {}
        }
    }
    private fun loadStatements() {
        try {
            val inputStream: InputStream = assets.open("statements.txt")
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val parts = line!!.split("=")
                if (parts.size == 2) {
                    val statement = Statement(parts[0], parts[1].toBoolean())
                    statements.add(statement)
                }
            }
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    private fun displayNextStatement() {
        if (statements.isNotEmpty()) {
            currentStatementIndex = Random().nextInt(statements.size)
            statementTextView.text = statements[currentStatementIndex].text
            statementTextView.gravity = Gravity.CENTER
        }
    }
    private fun checkAnswer(userAnswer: Boolean) {
        if (currentStatementIndex != -1 && currentStatementIndex < statements.size) {
            val currentStatement = statements[currentStatementIndex]
            if (userAnswer == currentStatement.isCorrect) {
                // streak+1
                streak++
                streakTextView.text=streak.toString()
            } else {
                val intent = Intent(this,EndScreenActivity::class.java)
                intent.putExtra("STREAK_KEY",streak)
                startActivity(intent)

            }
            // Display the next statement
            displayNextStatement()
        }
    }

    // Determine the screen width (less decorations) to use for the ad width.
// If the ad hasn't been laid out, default to the full screen width.
    private val adSize: AdSize
        get() {
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = adLayout.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    private fun loadBanner() {

        // Create a new ad view.
        val adView = AdView(this)
        val adSize=adSize
        adView.adUnitId = "ca-app-pub-2477791478485674/8855808309"


        adLayout.addView(adView)

        adView.setAdSize(adSize)
        // Create an ad request.
        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        adView.loadAd(adRequest)
    }
    data class Statement(val text: String, val isCorrect: Boolean)
}