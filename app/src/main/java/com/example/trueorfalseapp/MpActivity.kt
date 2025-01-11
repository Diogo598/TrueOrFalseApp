package com.example.trueorfalseapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.example.trueorfalseapp.Game
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


class MpActivity: AppCompatActivity() {

    private lateinit var sessionIDText: TextView
    private lateinit var queueTimeRunnable: Runnable
    private lateinit var postWaitingLayout:LinearLayout
    private lateinit var postEndLayout:LinearLayout
    private lateinit var waitingLayout:ConstraintLayout
    private lateinit var gameLayout:ConstraintLayout
    private lateinit var postLayout:ConstraintLayout
    private lateinit var timetextview:TextView
    private lateinit var cancelButton: Button
    private var queueStartTime: Long = 0L
    private var opponentFound: Boolean = false
    private var handler = Handler(Looper.getMainLooper())
    private var statements= ArrayList<ClassicActivity.Statement>()
    private var currentStatementIndex=-1
    private var currentQuestion = 0

    private lateinit var postHeadline:TextView
    private lateinit var postWinnerAvatar:ImageView
    private lateinit var postWinnerName:TextView
    private lateinit var postWinnerTitle:TextView
    private lateinit var postWinnerWins:TextView
    private lateinit var postWinnerDraws:TextView
    private lateinit var postWinnerLooses:TextView

    private lateinit var streakTextView: TextView
    private lateinit var statementTextView: TextView
    private lateinit var otherPlayerTextView:TextView
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var questionTimerRunnable: Runnable
    private var questionStartTime: Long = 0L
    private lateinit var textViewTime: TextView
    private lateinit var player1ImageViews: List<ImageView>
    private lateinit var player2ImageViews: List<ImageView>
    private val totalQuestions = 5
    private var correctAnswers = 0
    private var wrongAnswers = 0
    private var opponentScore: Int = 0
    private lateinit var gameRef:DocumentReference
    private var currentRound=0
    private var player=false
    private var receivedSessionID: Int = 0
    private var receivedPlayer: Int = 0
    private var player1round=0
    private var player2round=0
    private var currentIndices: List<Int> = emptyList()
    private var currentPosition=-1
    private val playerPositions = mutableMapOf<Int, Int>()
    private var queestarted=0
    private var playersList: List<Player>? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mp_game)

        postWaitingLayout=findViewById(R.id.postWaitingLayout)
        postEndLayout=findViewById(R.id.postEndLayout)
        postLayout=findViewById(R.id.postLayout)
        waitingLayout=findViewById(R.id.waitingLayout)
        gameLayout=findViewById(R.id.gamelayout)

        timetextview=findViewById(R.id.timetextview)
        sessionIDText=findViewById(R.id.sessionIdText)
        cancelButton=findViewById(R.id.buttonCancel)
        streakTextView=findViewById(R.id.streakTextView)
        statementTextView=findViewById(R.id.statementTextView)

        postHeadline=findViewById(R.id.postHeadlineTextview)
        postWinnerAvatar=findViewById(R.id.postWinnerAvatar)
        postWinnerName=findViewById(R.id.postWinnerName)
        postWinnerTitle=findViewById(R.id.postWinnerTitle)
        postWinnerWins=findViewById(R.id.postWinnerWins)
        postWinnerDraws=findViewById(R.id.postWinnerDraws)
        postWinnerLooses=findViewById(R.id.postWinnerLooses)

        trueButton=findViewById(R.id.trueButton)
        falseButton=findViewById(R.id.falseButton)
        textViewTime = findViewById(R.id.textViewTime)

        player1ImageViews = initializeImageViews("imageViewResultP1_", 5)
        player2ImageViews = initializeImageViews("imageViewResultP2_", 5)

        receivedSessionID = intent.getIntExtra("CurrentSession", 0)
        receivedPlayer = intent.getIntExtra("PLAYER_ROLE", 0)

        sessionIDText.text= receivedSessionID.toString()

        Log.d("FirstScopeCCCCCCC", currentIndices.toString())
        fetchDataAndInitialize(lifecycleScope,receivedSessionID){indicies ->
            Log.d("Coroutine done ","$indicies")
        }


        trueButton.setOnClickListener {
            checkAnswer(receivedSessionID,receivedPlayer,"true")
        }
        falseButton.setOnClickListener {
            checkAnswer(receivedSessionID,receivedPlayer,"false")
        }

        cancelButton.setOnClickListener {
            stopQuee()
            val intent=Intent(this,MultiplayerMenuActivity::class.java)
            startActivity(intent)
        }
    }

    fun fetchDataAndInitialize(
        scope: CoroutineScope,
        receivedSessionID: Int,
        onCompletion: (List<String>) -> Unit // Optional callback if needed
    ) {
        scope.launch(Dispatchers.Main) {
            FireBaseManager.getIndicies(receivedSessionID) { indices ->
                currentIndices = indices ?: emptyList()
                currentPosition = 0
                Log.d("endCCCCCCC", currentIndices.toString())

                // Firebase setup
                val sessionID = receivedSessionID.toString()
                val db = FirebaseFirestore.getInstance()
                gameRef = db.collection("games").document(sessionID)

                // Additional processing
                trackAndHandleGameState(receivedSessionID)
                startQuee()
                val temp = currentIndices
                Log.d("CCCCCCCCCCCC", temp.toString())

            }
        }
    }
    private fun displayNextStatement(receivedPlayer: Int) {
        //Player 1 = 1
        //PLayer 2 = 0
        val currentPosition = playerPositions.getOrPut(receivedPlayer){0}
        Log.d("Display Function got Triggered Index is ",currentPosition.toString())

        if (!statements.isNullOrEmpty()&&currentIndices.isNotEmpty()) {

            val nextPosition = (currentPosition + 1)%currentIndices.size
            playerPositions[receivedPlayer]= nextPosition

            currentStatementIndex= currentIndices[nextPosition]
            Log.d("CurrentIndex",currentStatementIndex.toString())

            //Display
            statementTextView.text=statements[currentStatementIndex].text
            statementTextView.gravity=Gravity.CENTER

            //Start Timer
            startQuestionTimer()
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
                    val statement = ClassicActivity.Statement(parts[0], parts[1].toBoolean())
                    statements.add(statement)
                }
            }
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun startQuestionTimer() {
        questionStartTime = System.currentTimeMillis()
        questionTimerRunnable = Runnable {
            val elapsedTime = System.currentTimeMillis() - questionStartTime
            val remainingTime = 10000 - elapsedTime
            if (remainingTime > 0) {
                textViewTime.text = "${remainingTime / 1000} s"
                handler.postDelayed(questionTimerRunnable, 100)
            } else {
                // Consider unanswered question as wrong
                //  Timeout
                Log.d("RemainingTime is ",remainingTime.toString())
                checkAnswer(receivedSessionID,receivedPlayer,"timeout")

            }
        }
        handler.post(questionTimerRunnable)
    }

    private fun stopQuestionTimer() {
        handler.removeCallbacks(questionTimerRunnable)
    }
    private fun trackAndHandleGameState(sessionId: Int) {

        // Start tracking answers with the session ID
        FireBaseManager.trackGameState(sessionId,
            onAnswerChange = { playerId, lastAnswer ->
                var currentImageView = player1ImageViews[player1round]
                Log.d("PLayer id from waitingupdate is",playerId.toString())
                // Handle the result here (e.g., update UI)
                if (playerId == 1) {
                    Log.d("Listener",player1round.toString())
                    if (lastAnswer == "false") {
                        currentImageView.setImageResource(R.drawable.redcircle)
                        player1round++
                    }
                    if (lastAnswer == "true") {
                        currentImageView.setImageResource(R.drawable.greencircle)
                        player1round++
                    }
                    if (player1round == 5) {
                        FireBaseManager.updatePlayerWaiting(sessionId, playerId)
                        player1round=0

                    }
                }
                if (playerId == 2) {
                    Log.d("Listener",player2round.toString())
                    currentImageView = player2ImageViews[player2round]
                    if (lastAnswer == "false") {
                        currentImageView.setImageResource(R.drawable.redcircle)
                        player2round++
                    }
                    if (lastAnswer == "true") {
                        currentImageView.setImageResource(R.drawable.greencircle)
                        player2round++
                    }
                    if (player2round == 5) {
                        FireBaseManager.updatePlayerWaiting(sessionId, playerId)
                        player2round=0

                    }
                }
            },
            onBothPlayersWaiting = {
                determineWinner()
            }
        )
    }

    private fun checkAnswer(receivedSessionID:Int,receivedPlayer:Int,usersAnswer: String) {
        Log.d("Fun Check","CHECK FUN TRIGGER")
        if (currentStatementIndex != -1 && currentStatementIndex < statements.size-1) {
            val currentStatement = statements[currentStatementIndex]
            var userAnswer = usersAnswer.toBoolean()

            if (userAnswer == currentStatement.isCorrect && usersAnswer != "timeout") {
                //Correct
                //Supply Firbase with answer
                //correctAnswers++
               FireBaseManager.updatePlayerLastAnswer(receivedSessionID, receivedPlayer, "true")
                currentRound++
            } else {
                //Wrong
                //Supply Firbase with answer

                FireBaseManager.updatePlayerLastAnswer(receivedSessionID, receivedPlayer, "false")
                currentRound++
            }

            if(currentRound>4) {
                changeLayouts(gameLayout,false)

                changeLayouts(postWaitingLayout,true)
                stopQuestionTimer()
                return
            }
            Log.d("CurrentRound is ",currentRound.toString())
            currentQuestion++
            //changeView(currentRound)
            streakTextView.text=currentQuestion.toString()
            // Display the next statement
            displayNextStatement(receivedPlayer)
        }
    }

    private fun determineWinner() {
        Log.d("DETERMINE_WINNER", "Function called")
        FireBaseManager.getCorrectAnswers(receivedSessionID) { answers ->
            runOnUiThread {
                changeLayouts(postWaitingLayout, false)
                val player1Correct = answers.first
                val player2Correct = answers.second

                // Get the players
                FireBaseManager.getPlayer(receivedSessionID) { it->
                    it?.let {
                        //Initialize Shared preferences.
                        val sharedPreferences=getSharedPreferences("ProfilePreferences", Context.MODE_PRIVATE)
                        val username = sharedPreferences.getString("Username","AverageJoe")
                        val currentDraws= sharedPreferences.getInt("OnlineDraws",0)
                        val currentWins= sharedPreferences.getInt("OnlineWins",0)
                        val currentLooses=sharedPreferences.getInt("OnlineLooses",0)
                        var updatedDraws=currentDraws
                        var updatedWins=currentWins
                        var updatedLooses=currentLooses


                        // Access the players from the game object
                        val player1 = it[0]
                        val player2 = it[1]

                        // Now you can access player data

                        //Player1 Data
                        val playerName1 = player1.playerName
                        val playerAvatar1=player1.playerAvatarId
                        val playerTitle1= player1.playerTitle
                        val playerWins1 = player1.wins
                        val playerDraws1= player1.draws
                        val playerLooses1=player1.looses

                        //Player2 Data
                        val playerName2 = player2.playerName
                        val playerAvatar2=player2.playerAvatarId
                        val playerTitle2= player2.playerTitle
                        val playerWins2 = player2.wins
                        val playerDraws2= player2.draws
                        val playerLooses2=player2.looses

                        // Implement winner logic
                        if (player1Correct == player2Correct) {
                            Log.d("ONLINERESULT", "Draw function gets triggered")
                            postWinnerName.text = "Draw"
                            updatedDraws= currentDraws + 1

                        } else if (player1Correct > player2Correct) {
                            //Player1 Won
                            Log.d("result", "$playerName1 Won")
                            postHeadline.text = "$playerName1 Won"
                            postWinnerAvatar.setImageResource(playerAvatar1.toInt())
                            postWinnerName.text = playerName1
                            postWinnerTitle.text=playerTitle1
                            postWinnerWins.text=playerWins1.toString()
                            postWinnerDraws.text=playerDraws1.toString()
                            postWinnerLooses.text=playerLooses1.toString()

                            if(username==playerName1){
                                 updatedWins=currentWins+1
                                Log.d("ONLINERESULT", "Player1 Win UpdatedWins = $updatedWins")
                            }
                            else{
                                 updatedLooses = currentLooses +1
                                Log.d("ONLINERESULT", "Player1 Win Player2Looses = $updatedLooses")
                            }
                        } else if (player1Correct < player2Correct) {

                            //Player2 won
                            Log.d("result", "$playerName2 Won")
                            postHeadline.text = "$playerName2 Won"

                            postWinnerAvatar.setImageResource(playerAvatar2.toInt())
                            postWinnerName.text = playerName2
                            postWinnerTitle.text=playerTitle2
                            postWinnerWins.text=playerWins2.toString()
                            postWinnerDraws.text=playerDraws2.toString()
                            postWinnerLooses.text=playerLooses2.toString()
                            if(username==playerName2){
                                 updatedWins=currentWins+1
                                Log.d("ONLINERESULT", "Player2 Win UpdatedWins = $updatedWins")
                            }
                            else{
                                 updatedLooses = currentLooses +1
                                Log.d("ONLINERESULT", "Player2 Win Player1Looses = $updatedLooses")
                            }
                        }
                        val editor = sharedPreferences.edit()
                        editor.putInt("OnlineDraws",updatedDraws)
                        editor.putInt("OnlineWins",updatedWins)
                        editor.putInt("OnlineLooses",updatedLooses)
                        Log.d("ONLINESTATS", "Draws = $updatedDraws ,Wins = $updatedWins , Looses = $updatedLooses")
                        editor.apply()

                        // Ensure layouts are updated after the winner is determined
                        changeLayouts(postEndLayout, true)
                    }
                }
            }
        }
    }


        /*
        Log.d("GameResult", "It Should proc here")
        Log.d("GameResult", correctAnswers.toString())
        Log.d("GameResult", opponentScore.toString())
        if (correctAnswers > opponentScore) {
            Log.d("GameResult", "You win!")
        } else if (correctAnswers < opponentScore) {
            Log.d("GameResult", "You lose!")
        } else {
            Log.d("GameResult", "It's a tie!")
        }
        // Show result screen or handle end of game*/
    //Quee start
    private fun startQuee() {
        Log.d("StartQueeFunctionGotTriggered","START QUEE TRIGGER")
        queueStartTime = System.currentTimeMillis()
        handler = Handler(Looper.getMainLooper())


        // Set up Firestore listener to check for opponent and game state updates
        gameRef.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                Log.w("FirestoreListener", "Listen failed.", firebaseFirestoreException)
                return@addSnapshotListener
            }
        if (queestarted==0){
            if (documentSnapshot != null && documentSnapshot.exists()) {
                val game = documentSnapshot.toObject(Game::class.java)
                game?.let {
                    opponentFound = it.players.size > 1 // Assuming opponent is added to players list
                    if (it.gameState == GameState.STARTED&&(1==1)or(1==1)) {
                        Log.d("com.example.trueorfalseapp.GameState", "com.example.trueorfalseapp.Game started")
                        stopQuee()
                        changeLayouts(waitingLayout,false)
                        changeLayouts(gameLayout,true)
                        loadStatements()
                        displayNextStatement(receivedPlayer)
                        queestarted++
                    }
                }

                }
            }
        }

        // Set up a runnable to update the timer text view every second
        queueTimeRunnable = object : Runnable {
            override fun run() {
                val elapsedTime = System.currentTimeMillis() - queueStartTime
                val seconds = elapsedTime / 1000
                timetextview.text = "$seconds s"

                if (!opponentFound) {
                    handler.postDelayed(this, 1000)
                }
            }
        }
        handler.post(queueTimeRunnable)
    }
    private fun stopQuee(){
        handler.removeCallbacks(queueTimeRunnable)
    }
    private fun changeLayouts(layout:ViewGroup,isVisible: Boolean){
       layout.visibility= if(isVisible) View.VISIBLE else View.GONE
    }
    private fun initializeImageViews(baseId: String, count: Int): List<ImageView> {
        val imageViews = mutableListOf<ImageView>()
        for (i in 1..count) {
            val resId = resources.getIdentifier("$baseId$i", "id", packageName)
            val imageView = findViewById<ImageView>(resId)
            imageViews.add(imageView)
        }
        return imageViews
    }
    private fun handleTimeout(){
        // Check which player is currently active and update Firebase with "False"
        FireBaseManager.updatePlayerLastAnswer(receivedSessionID, receivedPlayer, "False")

        // Update the UI to reflect the timeout, such as showing a timeout message or image
        var currentImageView = if (receivedPlayer == 1) player1ImageViews[currentRound] else player2ImageViews[currentRound]
        currentImageView.setImageResource(R.drawable.redcircle) // Example timeout image or indication

        // Proceed with the next statement
        displayNextStatement(receivedPlayer)

    }

    private fun changeView(currentRound:Int){

    }

    /*private fun setPlayer():String{
        if (player==false){
            val player1="player1"
            player=true
            return player1
        }
        else{
            var player2="player2"
            return player2
        }
    }*/
}