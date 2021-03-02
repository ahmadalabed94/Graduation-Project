package com.app.carsharing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_show_users.*

class ShowUsers : AppCompatActivity() {


    private val db = Firebase.firestore
    var mUserList: ArrayList<User>? = null
    var mRef2: DatabaseReference? = null
    var tripId:String?=null
    var count=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_users)
        val database = FirebaseDatabase.getInstance()
        mUserList = ArrayList()
        mRef2 = database.getReference("BookTrip")
        val data = intent
        tripId = data.extras!!.getString("Trip_Id")

    }

    override fun onStart() {
        super.onStart()
        mRef2?.child(tripId.toString())!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                mUserList?.clear()
                for (x in p0!!.children) {
                    var userId = x.getValue(String::class.java)
                    val info = db.collection("Users").document(userId.toString())
                    info.get().addOnSuccessListener { documentSnapshot ->
                        mUserList!!.add(documentSnapshot.toObject<User>()!!)
                        listViewOfUsers.text = listViewOfUsers.text.toString() +
                                "\n"+ mUserList!![count].name+"\n"+mUserList!![count].email+"\n"+"----------"
                        ++count
                    }
                }
            }
        })

    }
}
