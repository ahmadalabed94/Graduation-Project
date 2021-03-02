package com.app.carsharing

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.app.carsharing.glide.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_account_info.*
import kotlinx.android.synthetic.main.fragment_more.*
import kotlinx.android.synthetic.main.fragment_more.view.*
import java.io.ByteArrayOutputStream
import java.util.*


class More : Fragment() {

    private lateinit var userName:String

    var mItems = arrayOf("Account", "App Settings","Help", "About us", "Sign Out")
    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val storageInstance: FirebaseStorage by lazy{
        FirebaseStorage.getInstance()
    }
    private val currentUserStorageRef:StorageReference
    get() = storageInstance.reference.child(FirebaseAuth.getInstance().currentUser?.uid.toString())

    private val firestoreInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val currentUserDucRef:DocumentReference
    get() = firestoreInstance.document("Users/${FirebaseAuth.getInstance().currentUser?.uid.toString()}")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v = inflater.inflate(R.layout.fragment_more, container, false)

        getUserInfo { user ->
            userName = user.name
            txtUserName.text = user.name
            if(user.imageProfile.isNotEmpty()){
                GlideApp.with(activity!!)
                    .load(storageInstance.getReference(user.imageProfile))
                    .into(v.imgUser)}
        }

        var myAdabter = ArrayAdapter<String>(activity!!, android.R.layout.simple_list_item_1, mItems)
        v.lstMore.adapter = myAdabter
        v.lstMore.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long) {
                val txt = view as TextView
                when (position) {
                    0 -> {
                        startActivity(Intent(activity,AccountInfo::class.java))
                    }
                    1 -> {
                        Toast.makeText(activity,position.toString(),Toast.LENGTH_SHORT).show()
                    }
                    2 -> {
                        Toast.makeText(activity,position.toString(),Toast.LENGTH_SHORT).show()
                    }
                    3 -> {
                        Toast.makeText(activity,position.toString(),Toast.LENGTH_SHORT).show()
                    }
                    4 -> {
                        mAuth.signOut()
                        val intent = Intent(activity, SignIn::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }
                    else -> {
                    }
                }
            }
        })
        return v
    }


    private fun getUserInfo(onComplete:(User) -> Unit){
        currentUserDucRef.get().addOnSuccessListener {
            onComplete(it.toObject(User::class.java)!!)
        }
    }


}
