package com.app.carsharing

import android.content.ComponentCallbacks2
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.TextSwitcher
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignIn : AppCompatActivity(), TextWatcher {

    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        imgSignIn.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).duration=3000

        edtTxtEmailSignIn.addTextChangedListener(this@SignIn)
        edtTxtPasswordSignIn.addTextChangedListener(this@SignIn)

        btnCreateNewAccount.setOnClickListener {
            startActivity(Intent(this@SignIn, SignUp::class.java))
        }

        btnSignIn.setOnClickListener {
            validData()
        }

    }


    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        btnSignIn.isEnabled =
            edtTxtEmailSignIn.text.isNotEmpty() && edtTxtPasswordSignIn.text.isNotEmpty()
    }

    fun validData() {
        if (edtTxtEmailSignIn.text.isEmpty()) {
            edtTxtEmailSignIn.error = "Email Required"
            edtTxtEmailSignIn.requestFocus()
            return@validData
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(edtTxtEmailSignIn.text).matches()) {
            edtTxtEmailSignIn.error = "Not valid email"
            edtTxtEmailSignIn.requestFocus()
            return@validData
        }
        if (edtTxtPasswordSignIn.text.length < 6) {
            edtTxtPasswordSignIn.error = "password is short"
            edtTxtPasswordSignIn.requestFocus()
            return@validData
        }

        signIn(
            edtTxtEmailSignIn.text.trim().toString(),
            edtTxtPasswordSignIn.text.trim().toString()
        )
    }

    fun signIn(email: String, password: String) {
        progressBarSignIn.visibility = View.VISIBLE
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {task->
            if (task.isSuccessful) {
                progressBarSignIn.visibility = View.INVISIBLE
                val intent = Intent(this@SignIn, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            } else {
                progressBarSignIn.visibility = View.INVISIBLE
                Toast.makeText(this@SignIn, task.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    override fun onStart(){
        super.onStart()
        if(mAuth.currentUser?.uid != null){
            val intent = Intent(this@SignIn, MainActivity::class.java)
            startActivity(intent)
        }
    }



}
