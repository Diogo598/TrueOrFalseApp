package com.example.trueorfalseapp

data class Game(
    val sessionId: String = "",
    var players: List<Player> = emptyList(),
    var indices: List<Int> = emptyList(),
    var gameState: GameState = GameState.WAITING,
    val startTime: Long = 0L
) {
companion object {
    fun fromDocument(doc: Map<String, Any>): Game {
        val sessionId = doc["sessionId"] as String
        val players = (doc["players"] as List<Map<String, Any>>).map { playerDoc ->
            Player(
                playerName = playerDoc["playerName"] as String,
                correctAnswers=(playerDoc["correctAnswers"]as Long).toInt(),
                playerAvatarId = playerDoc["playerAvatarId"] as String,
                playerTitle = playerDoc["playerTitle"] as String,
                wins = (playerDoc["wins"] as Long).toInt(),
                draws = (playerDoc["draws"] as Long).toInt(),
                looses = (playerDoc["looses"] as Long).toInt(),
                waiting=(playerDoc["waiting"] as Boolean),
            )
        }
        val gameState = GameState.valueOf(doc["gameState"] as String)
        val startTime = doc["startTime"] as Long
        val indices= intArrayOf(0,0,0,0,0).toList()

        return Game(sessionId, players,indices, gameState, startTime)
    }
}
}
data class Player(
    val player:Int=0,
    val playerName: String = "",
    val correctAnswers:Int=0,
    val playerAvatarId: String = "",
    val playerTitle: String = "",
    val playerId:Int=0,
    val lastAnswer:String="",
    val wins: Int = 0,
    val draws: Int = 0,
    val looses: Int = 0,
    val waiting: Boolean=false
)

enum class GameState {
    WAITING,
    STARTED,
    FINISHED
}
