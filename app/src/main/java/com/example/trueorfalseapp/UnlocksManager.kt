package com.example.trueorfalseapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log


//Aufrufen bei MainActivity?

object UnlocksManager {

     val unlockMap = mapOf(
         "quatsch" to false,
    ).toMutableMap()
    var currentlyUnlocked= ""

     fun saveUnlock(context: Context, map: Map<String, Boolean>) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("Unlocks", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        val mapAsString = map.entries.joinToString(separator = ",") { "${it.key}:${it.value}" }

        editor.putString("unlockMap", mapAsString)
        editor.apply()
    }
     fun getUnlock(context: Context): Map<String, Boolean>? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("Unlocks", Context.MODE_PRIVATE)
        val mapAsString = sharedPreferences.getString("unlockMap", null) ?: return null

        return mapAsString.split(",").map {
            val (key, value) = it.split(":")
            key to value.toBoolean()
        }.toMap()
    }

    fun checkForDuplicates(context: Context,key:String): Boolean? {
        val allEntries=getUnlock(context)
        val currentUnlock= allEntries?.get(key)
        return currentUnlock
    }

     fun updateUnlock(context: Context, key: String, value: Boolean) {
        val unlockMap = getUnlock(context)?.toMutableMap() ?: mutableMapOf()
        unlockMap[key] = value
        saveUnlock(context, unlockMap)
    }
    fun currentUnlock(key:String){
       currentlyUnlocked=key
    }
}