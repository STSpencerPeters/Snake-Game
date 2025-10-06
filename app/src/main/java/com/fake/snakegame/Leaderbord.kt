package com.fake.snakegame

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class LeaderboardActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val listView = ListView(this)
        setContentView(listView)

        db.collection("users")
            .orderBy("score", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { result ->
                val list = result.map {
                    "${it.getString("username")} - ${it.getLong("score") ?: 0}"
                }
                listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
            }
    }
}