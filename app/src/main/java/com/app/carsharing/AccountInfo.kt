package com.app.carsharing

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.app.carsharing.glide.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_account_info.*
import java.io.ByteArrayOutputStream
import java.util.*

class AccountInfo : AppCompatActivity() {

    private lateinit var userName:String
    private lateinit var userBirthDay:String
    private lateinit var userSex:String

    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    private val currentUserStorageRef: StorageReference
        get() = storageInstance.reference.child(FirebaseAuth.getInstance().currentUser?.uid.toString())

    private val firestoreInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val currentUserDucRef: DocumentReference
        get() = firestoreInstance.document("Users/${FirebaseAuth.getInstance().currentUser?.uid.toString()}")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_info)

        getUserInfo { user ->
            userName = user.name
            userBirthDay = user.birthDay
            userSex = user.sex

            userNameAccount.text = user.name
            birthDayAccount.text = user.birthDay
            if(user.imageProfile.isNotEmpty()){
            GlideApp.with(this@AccountInfo)
                .load(storageInstance.getReference(user.imageProfile))
                .into(imgUserAccount)}
        }

        btnAddImg.setOnClickListener {
            selectImage()
        }
    }

    fun selectImage() {
        val imgInt = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
        }
        startActivityForResult(Intent.createChooser(imgInt, "select image"), 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imgUserAccount.setImageURI(data.data)
            // Get the data from an ImageView as bytes
            imgUserAccount.isDrawingCacheEnabled = true
            imgUserAccount.buildDrawingCache()
            val bitmap = (imgUserAccount.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            uploadProfileImage(data){path ->
                val userFieldMap = mutableMapOf<String,Any>()
                userFieldMap["name"] = userName
                userFieldMap["birthDay"] = userBirthDay
                userFieldMap["Sex"] = userSex
                userFieldMap["imageProfile"] = path
                currentUserDucRef.update(userFieldMap)
            }
        }
    }

    private fun uploadProfileImage(data: ByteArray,onSuccess:(imagePath:String) -> Unit) {
      val ref = currentUserStorageRef .child("profilePictures/${UUID.nameUUIDFromBytes(data)}")
        ref.putBytes(data).addOnCompleteListener {
            if(it.isSuccessful){
                onSuccess(ref.path)
            }
            else
            {
                Toast.makeText(this@AccountInfo,"Errore : ${it.exception?.message.toString()}",Toast.LENGTH_SHORT).show() }
        }
    }

    private fun getUserInfo(onComplete:(User) -> Unit){
        currentUserDucRef.get().addOnSuccessListener {
            onComplete(it.toObject(User::class.java)!!)
        }
    }

}
