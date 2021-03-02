package com.app.carsharing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUp : AppCompatActivity(), TextWatcher {

    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestoreInestance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val currentUserDocRef: DocumentReference
        get() = firestoreInestance.document("Users/${mAuth.currentUser?.uid.toString()}")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        txtCar.animate().alpha(1.0f).duration=3000
        txtSharing.animate().alpha(1.0f).duration=3000

        edtTxtName.addTextChangedListener(this@SignUp)
        edtTxtEmail.addTextChangedListener(this@SignUp)
        edtTxtPassword.addTextChangedListener(this@SignUp)
        edtTxtBirthDay.addTextChangedListener(this@SignUp)

        btnSignUp.setOnClickListener {
            validData()
        }

    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        enableBtnSignUp()
    }

    fun enableBtnSignUp() {
        btnSignUp.isEnabled =
            edtTxtName.text.trim().isNotEmpty() && edtTxtEmail.text.trim().isNotEmpty()
                    && edtTxtPassword.text.trim().isNotEmpty() && edtTxtBirthDay.text.trim()
                .isNotEmpty()
                    && (rdMale.isChecked || rdFemale.isChecked) }

    fun validData() {
        if (edtTxtName.text.isEmpty()) {
            edtTxtName.error = "Name Required"
            edtTxtName.requestFocus()
            return@validData
        }
        if (edtTxtEmail.text.isEmpty()) {
            edtTxtEmail.error = "Email Required"
            edtTxtEmail.requestFocus()
            return@validData
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(edtTxtEmail.text).matches()) {
            edtTxtEmail.error = "Not valid email"
            edtTxtEmail.requestFocus()
            return@validData
        }
        if (edtTxtPassword.text.length < 6) {
            edtTxtPassword.error = "password is short"
            edtTxtPassword.requestFocus()
            return@validData
        }
        if (edtTxtBirthDay.text.isEmpty()) {
            edtTxtBirthDay.error = "BirthDay Required"
            edtTxtBirthDay.requestFocus()
            return@validData
        }

        signUp(edtTxtEmail.text.trim().toString(), edtTxtPassword.text.trim().toString())
    }

    fun getSex(): String {
        if (rdMale.isChecked)
            return "Male"
        else if (rdFemale.isChecked)
            return "Female"
        else return "unknown"
    }


    fun signUp(email: String, password: String) {
          progressBarSignUp.visibility = View.VISIBLE
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {task->
            val newUser = User(
                edtTxtName.text.trim().toString(),
                edtTxtBirthDay.text.trim().toString(),
                getSex(),
                "",
                edtTxtEmail.text.trim().toString() )
            currentUserDocRef.set(newUser)
            if (task.isSuccessful) {
                progressBarSignUp.visibility = View.INVISIBLE
                val intent = Intent(this@SignUp, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            } else {
                progressBarSignUp.visibility = View.INVISIBLE
                Toast.makeText(this@SignUp, task.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }


}


/*  fun uploadData(name: String?, birthDay: String?, sex: String?) {
      val newUser = hashMapOf(
          "Id" to mAuth.currentUser?.uid.toString(), "Name" to name,
          "Email" to mAuth.currentUser?.email.toString(),
          "Birth Day" to birthDay, "Sex" to sex
      )
      db.collection("Users").document(mAuth.currentUser?.uid.toString())
          .set(newUser)
          .addOnSuccessListener { documentReference ->
              Log.d(TAG, "DocumentSnapshot added with ID: ${mAuth.currentUser?.uid.toString()}")
          }
          .addOnFailureListener { e ->
              Log.w(TAG, "Errore", e)
          }
  }*/


/*
*
*   .addOnCompleteListener {
            if (it.isSuccessful){

            }else{

            }
        }
* */

//firestoreInestance.collection("Users").document(mAuth.currentUser?.uid.toString())


