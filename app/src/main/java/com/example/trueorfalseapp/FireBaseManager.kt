package com.example.trueorfalseapp

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlin.random.Random
import kotlin.random.nextInt

object FireBaseManager {
    private var winnerDetermined = false
    var combinedListener: ListenerRegistration? = null
    //var indicies: List<Int> = emptyList()

    fun createLobby(context: Context): Pair<Int, Int> {
        val player = createPlayer(context,1)
        val (game, sessionId) = createGame(player)
        saveToFireBase(game)
        return Pair(sessionId, 1)

    }

    fun checkAndJoinLobby(context: Context, userinput: Int) {
        FirebaseFirestore.getInstance()
            .collection("games")
            .document(userinput.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    joinGame(context, userinput)
                } else {
                    Log.d("JoinGame", "com.example.trueorfalseapp.Game does not exist.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("JoinGame", "Error fetching game data", e)
            }
    }

    fun joinGame(context: Context, sessionID:Int) {
        val joiningPlayer = createPlayer(context,2)
        addJoiningPlayer(context, sessionID, joiningPlayer)
    }

    fun createPlayer(context: Context,playerRole:Int): Player {
        val sharedPreferences =
            context.getSharedPreferences("ProfilePreferences", Context.MODE_PRIVATE)


        //Online User Data
        val username = sharedPreferences.getString("Username", "AverageJoe")
        val selectedAvatarResId =
            sharedPreferences.getInt("selectedAvatarResId", R.drawable.defaultavatar)
        val title = sharedPreferences.getString("selectedTitle", "Novice")
        val wins=sharedPreferences.getInt("OnlineWins",0)
        val draws=sharedPreferences.getInt("OnlineDraws",0)
        val looses=sharedPreferences.getInt("OnlineLooses",0)


        val player = Player(
            player = playerRole,
            playerName = username ?: "AverageJoe",
            correctAnswers=0,
            playerAvatarId = selectedAvatarResId.toString(),
            playerTitle = title ?: "Novice",
            wins = wins,
            draws = draws,
            looses = looses,
            waiting = false
        )

        return player
    }

    fun createGame(player: Player): Pair<Game, Int> {
        val sessionId = Random.nextInt(1..1000).toString()
        val game = Game(

            sessionId = sessionId,
            indices = createIndicies(),
            players = listOf(player),
            gameState = GameState.WAITING,
            startTime = System.currentTimeMillis()
        )
        return Pair(game, sessionId.toInt())
    }

    fun saveToFireBase(game: Game) {
        FirebaseFirestore.getInstance()
            .collection("games")
            .document(game.sessionId)
            .set(game)
            .addOnSuccessListener {
                Log.d("Firestore", "Lobby created with ID: ${game.sessionId}")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error creating lobby", e)
            }
    }
    fun updatePlayerWaiting(sessionId: Int,currentPlayer: Int){
        Log.d("FireBaseManager","WaitingShouldGetUpdated here")
        val db = FirebaseFirestore.getInstance()
        val gameRef = db.collection("games").document(sessionId.toString())
        // Retrieve the game document
        gameRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val game = document.toObject(Game::class.java)
                    game?.let { foundGame ->
                        //Access and modify firebase values of player here.
                        val players = foundGame.players.toMutableList()
                        //Players by index
                        val player1 = players[0] // Player at index 0
                        val player2 = players[1] // Player at index 1
                        Log.d("whats the player",currentPlayer.toString())
                        if(currentPlayer==1){
                            val updatedPlayer1=player1.copy(waiting = true)
                            players[0]=updatedPlayer1
                        }
                        else{
                            val updatedPlayer2=player2.copy(waiting = true)
                            players[1]=updatedPlayer2
                        }
                        //Save Players
                        foundGame.players=players

                        /// Save the updated Game object back to Firebase
                        FirebaseFirestore.getInstance()
                            .collection("games")
                            .document(sessionId.toString())
                            .set(foundGame)
                    }

                } else {
                    Log.d("Firestore", "Game document does not exist.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error retrieving game document", e)
            }

    }

    fun updatePlayerLastAnswer(sessionId: Int, currentPlayer: Int, lastAnswer: String) {
        val db = FirebaseFirestore.getInstance()
        val gameRef = db.collection("games").document(sessionId.toString())


        // Retrieve the game document
        gameRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val game = document.toObject(Game::class.java)
                    game?.let { foundGame ->
                        //Access and modify firebase values of player here.
                        val players = foundGame.players.toMutableList()
                        //Players by index
                        val player1 = players[0] // Player at index 0
                        val player2 = players[1] // Player at index 1
                        Log.d("whats the player",currentPlayer.toString())
                        if(currentPlayer==1){
                            var correctAnswers=player1.correctAnswers
                            if(lastAnswer=="true"){
                                Log.d("FirbaseLastAnswer",lastAnswer)
                                correctAnswers++
                            }
                            val updatedPlayer1=player1.copy(lastAnswer=lastAnswer, correctAnswers = correctAnswers)
                            players[0]=updatedPlayer1
                        }
                        else{
                            var correctAnswers=player2.correctAnswers
                            if(lastAnswer=="true"){
                                correctAnswers++
                            }
                            val updatedPlayer2=player2.copy(lastAnswer=lastAnswer, correctAnswers = correctAnswers)
                            players[1]=updatedPlayer2
                        }
                        //Save Players
                        foundGame.players=players

                        /// Save the updated Game object back to Firebase
                        FirebaseFirestore.getInstance()
                            .collection("games")
                            .document(sessionId.toString())
                            .set(foundGame)
                    }

                } else {
                    Log.d("Firestore", "Game document does not exist.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error retrieving game document", e)
            }
    }
    fun getCorrectAnswers(sessionId: Int, callback: (Pair<Int,Int>)->Unit) {
        val db = FirebaseFirestore.getInstance()
        val gameRef = db.collection("games").document(sessionId.toString())

        gameRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val game = document.toObject(Game::class.java)
                    game?.let { foundGame ->
                        //Access and modify firebase values of player here.
                        val players = foundGame.players.toMutableList()
                        //Players by index
                        val player1 = players[0] // Player at index 0
                        val player2 = players[1] // Player at index 1
                        val correctAnswers1 = player1.correctAnswers
                        val correctAnswers2 = player2.correctAnswers
                        callback(Pair(correctAnswers1,correctAnswers2))
                    }
                }
            }
    }
    fun getPlayer(sessionId: Int,callback: (List<Player>?)->Unit){

        val db = FirebaseFirestore.getInstance()
        val gameRef = db.collection("games").document(sessionId.toString())

        gameRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val game = Game.fromDocument(document.data ?: emptyMap())
                    game?.let { foundGame ->
                        // Access and return the player at the specified index
                        val players = foundGame.players
                        if (players.size >= 2) {
                            // Return the list of players
                            callback(players)
                        } else {
                            callback(null) // Index out of bounds
                        }
                    }
                } else {
                    callback(null) // Document does not exist
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(null) // Handle failure
            }
    }


    fun getIndicies(sessionId: Int, callback:(List<Int>?)->Unit){

        val db = FirebaseFirestore.getInstance()
        val gameRef = db.collection("games").document(sessionId.toString())

        gameRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val index = document.get("indices") as? List<Int>
                    callback(index)
                } else
                    callback(null)
            }

    }


    fun createIndicies():List<Int>{
        var indexList=(1..833).toList().shuffled()
        return indexList.take(5)
    }
    fun trackGameState(
        sessionId: Int,
        onAnswerChange: (Int, String) -> Unit,
        onBothPlayersWaiting: () -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val gameRef = db.collection("games").document(sessionId.toString())

        combinedListener = gameRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("Firestore", "Listen failed.", error)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val game = snapshot.toObject(Game::class.java)
                game?.let { foundGame ->
                    val players = foundGame.players.toMutableList()

                    var shouldUpdateFirestore = false

                    // Check for lastAnswer changes and trigger callback if needed
                    if (players.isNotEmpty()) {
                        val player1 = players[0]
                        if (player1.lastAnswer != "default") {
                            onAnswerChange(1, player1.lastAnswer)
                            players[0] = player1.copy(lastAnswer = "default") // Reset to "default"
                            shouldUpdateFirestore = true
                        }
                    }

                    if (players.size > 1) {
                        val player2 = players[1]
                        if (player2.lastAnswer != "default") {
                            onAnswerChange(2, player2.lastAnswer)
                            players[1] = player2.copy(lastAnswer = "default") // Reset to "default"
                            shouldUpdateFirestore = true
                        }
                    }

                    // Check if both players are in the waiting state
                    if (players.size >= 2) {
                        val player1 = players[0]
                        val player2 = players[1]
                        if (player1.waiting && player2.waiting && !winnerDetermined) {
                            winnerDetermined = true
                            onBothPlayersWaiting()
                        }
                    }

                    // Update Firestore if any changes were made
                    if (shouldUpdateFirestore) {
                        gameRef.set(foundGame.copy(players = players))
                    }
                }
            } else {
                Log.d("Firestore", "No such game document.")
            }
        }
    }


    fun addJoiningPlayer(context: Context, sessionID: Int, joiningPlayer: Player) {

        val db = FirebaseFirestore.getInstance()
        val gameRef = db.collection("games").document(sessionID.toString())

        gameRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val game = document.toObject(Game::class.java)
                game?.let { foundGame ->
                    val updatedPlayers = foundGame.players.toMutableList()
                    updatedPlayers.add(joiningPlayer)

                    // Update game state and add the player
                    foundGame.players = updatedPlayers
                    foundGame.gameState = GameState.STARTED

                    gameRef.set(foundGame)
                        .addOnSuccessListener {
                            Log.d("JoinGame", "com.example.trueorfalseapp.Player added to the game")

                            // Start MpActivity with necessary data
                            val intent = Intent(context, MpActivity::class.java).apply {
                                putExtra("CurrentSession", sessionID)
                                putExtra("GAME_STATE", foundGame.gameState.name)
                            }

                            context.startActivity(intent)
                        }
                        .addOnFailureListener { e ->
                            Log.e("JoinGame", "Error updating game data", e)
                        }
                }
            } else {
                Log.d("JoinGame", "com.example.trueorfalseapp.Game does not exist.")
            }
        }.addOnFailureListener { e ->
            Log.e("JoinGame", "Error fetching game data", e)
        }
    }
    /*//Firebase istasynchron daher coroutines
    suspend fun getIndecies(sessionId: Int): List<Int>? {
        val db = FirebaseFirestore.getInstance()
        val gameRef = db.collection("games").document(sessionId.toString())

        return try {
            val document = gameRef.get().await()
            if (document.exists()) {
                val gameData = document.data
                if (gameData != null) {
                    val game = Game.fromDocument(gameData)
                    game.indices  // Return indices list
                } else {
                    null  // Document data is null
                }
            } else {
                null  // Document doesn't exist
            }
        } catch (e: Exception) {
            Log.e("GameData", "Error fetching indices", e)
            null
        }
    }*/
}