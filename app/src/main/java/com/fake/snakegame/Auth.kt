package com.fake.snakegame

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.fake.snakegame.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener { loginUser() }
        binding.btnRegister.setOnClickListener { registerUser() }
    }

    private fun registerUser() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val username = binding.etUsername.text.toString()

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            val userId = auth.currentUser!!.uid
            db.collection("users").document(userId)
                .set(mapOf("username" to username, "score" to 0))
            goToGame()
        }.addOnFailureListener {
            Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            goToGame()
        }.addOnFailureListener {
            Toast.makeText(this, "Login failed: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToGame() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}