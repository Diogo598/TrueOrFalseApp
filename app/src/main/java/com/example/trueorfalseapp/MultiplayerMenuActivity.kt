package com.example.trueorfalseapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MultiplayerMenuActivity: AppCompatActivity() {
    private lateinit var layoutJoin:LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multimenu)

        val createButton= findViewById<Button>(R.id.buttonCreate)
        val tryjoinButton=findViewById<Button>(R.id.buttontryJoin)
        val joinButton=findViewById<Button>(R.id.buttonJoin)



        createButton.setOnClickListener {
            val (sessionID,player)=FireBaseManager.createLobby(this)
            //FireBaseManager.createStatements(statements)
            val intent = Intent(this,MpActivity::class.java)
            intent.putExtra("CurrentSession",sessionID)
            intent.putExtra("PLAYER_ROLE",1)
            Log.d("sessionId",sessionID.toString())
            startActivity(intent) }


        tryjoinButton.setOnClickListener {
            showLayout()
        }

        joinButton.setOnClickListener {
            var userinputedit=findViewById<EditText>(R.id.sessionEditText)
            val userinput = userinputedit.text.toString().toInt()
            if (userinput!=null) {
               FireBaseManager.checkAndJoinLobby(this,userinput)
            }
        }

    }
    private fun showLayout(){
        layoutJoin=findViewById(R.id.joinLayout)
        layoutJoin.visibility= View.VISIBLE
    }
    data class Statement(val text: String, val isCorrect: Boolean)
}