package com.app.carsharing

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.carsharing.glide.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.book_trip.view.*
import kotlinx.android.synthetic.main.fragment_created_trips.*
import kotlinx.android.synthetic.main.fragment_more.*
import kotlinx.android.synthetic.main.fragment_more.view.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*


class Search : Fragment() {

    private val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    private val currentUserStorageRef: StorageReference
        get() = storageInstance.reference.child(FirebaseAuth.getInstance().currentUser?.uid.toString())

    private val db = Firebase.firestore

    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    var mRef: DatabaseReference? = null
    var mTripList: ArrayList<Trips>? = null

    var mRef2: DatabaseReference? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v = inflater.inflate(R.layout.fragment_search, container, false)
        val database = FirebaseDatabase.getInstance()
        mRef = database.getReference("Trips")
        mTripList = ArrayList()

        mRef2 = database.getReference("BookTrip")


        v.btnSearch.setOnClickListener {
            searchTrips()
        }
        v.listOfSearchTrip.setOnItemClickListener { parent, view, position, id ->
            val alertBuilder = AlertDialog.Builder(context)
            var view = layoutInflater.inflate(R.layout.book_trip, null)
            val alertDialog = alertBuilder.create()
            alertDialog.setView(view)
            alertDialog.show()

            var trip = mTripList?.get(position)!!
            val info = db.collection("Users").document(trip.createdBy.toString())
            info.get().addOnSuccessListener {
                val userInfo = it.toObject(User::class.java)
                view.txtUserCreator.text = userInfo?.name.toString()
                view.txtEmailUserCreator.text = userInfo?.email.toString()
                if (userInfo?.imageProfile!!.isNotEmpty()) {
                    GlideApp.with(activity!!)
                        .load(storageInstance.getReference(userInfo?.imageProfile))
                        .into(view.imgUserCreator)
                }

            }
            mRef2!!.child(trip.tripId!!.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        Toast.makeText(context!!.applicationContext, p0.message, Toast.LENGTH_SHORT).show() }

                    override fun onDataChange(p0: DataSnapshot) {
                        for (x in p0.children) {
                            var x2 = x.getValue(String::class.java)
                            if (x2.toString().equals(mAuth.currentUser?.uid.toString())) {
                                view.btnBookTrip.text = "Cancel Book"
                                view.btnBookTrip.setBackgroundResource(R.drawable.btn_delete_account) } } } })

            view.btnBookTrip.setOnClickListener {
                when (view.btnBookTrip.text) {
                    "Book Trip" -> {
                        if (trip.seats > 0) {
                            mRef2!!.child(trip.tripId.toString()).child(mAuth.currentUser?.uid.toString()).setValue(mAuth.currentUser?.uid.toString())
                            val edtTrip = Trips(trip.tripId.toString(), trip.from, trip.to,
                                trip.dateTrip, trip.timeTrip, (trip.seats-1), trip.fare, trip.createdBy.toString())
                            mRef!!.child(trip.tripId!!.toString()).setValue(edtTrip)
                            searchTrips()
                        } else {
                            Toast.makeText(context!!.applicationContext, "you cant book this trip", Toast.LENGTH_SHORT).show() } }
                    "Cancel Book" -> {
                        mRef2!!.child(trip.tripId.toString()).child(mAuth.currentUser?.uid.toString()).removeValue()
                        val edtTrip = Trips(trip.tripId.toString(), trip.from, trip.to,
                            trip.dateTrip, trip.timeTrip, (trip.seats+1), trip.fare, trip.createdBy.toString())
                        mRef!!.child(trip.tripId!!.toString()).setValue(edtTrip)
                        searchTrips() }
                    else -> { } }
                alertDialog.dismiss() }
            false }
        return v }

    private fun searchTrips() {

        if (searchFrom.text.trim().toString().isEmpty() && searchTo.text.trim().toString().isEmpty()) {
            mRef?.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }
                override fun onDataChange(p0: DataSnapshot) {
                    mTripList?.clear()
                    for (x in p0.children) {
                        var trip = x.getValue(Trips::class.java)
                        if (trip!!.createdBy != mAuth.currentUser?.uid)
                            mTripList!!.add(0, trip) }
                    var tripAdapter = CreatedTripAdapter(context!!.applicationContext, mTripList!!)
                    listOfSearchTrip.adapter = tripAdapter } })
        } else if (!searchFrom.text.trim().toString().isEmpty() && searchTo.text.trim().toString().isEmpty()) {
            mRef?.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }
                override fun onDataChange(p0: DataSnapshot) {
                    mTripList?.clear()
                    for (x in p0.children) {
                        var trip = x.getValue(Trips::class.java)
                        if (trip!!.from.contains(searchFrom.text.toString())
                            && trip.createdBy != mAuth.currentUser?.uid)
                            mTripList!!.add(0, trip) }
                    var tripAdapter = CreatedTripAdapter(context!!.applicationContext, mTripList!!)
                    listOfSearchTrip.adapter = tripAdapter } })
        } else if (searchFrom.text.trim().toString().isEmpty()
            && !searchTo.text.trim().toString().isEmpty()
        ) {
            mRef?.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }
                override fun onDataChange(p0: DataSnapshot) {
                    mTripList?.clear()
                    for (x in p0.children) {
                        var trip = x.getValue(Trips::class.java)
                        if (trip!!.to.contains(searchTo.text.toString())
                            && trip.createdBy != mAuth.currentUser?.uid)
                            mTripList!!.add(0, trip) }
                    var tripAdapter = CreatedTripAdapter(context!!.applicationContext, mTripList!!)
                    listOfSearchTrip.adapter = tripAdapter } })
        } else {
            mRef?.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }
                override fun onDataChange(p0: DataSnapshot) {
                    mTripList?.clear()
                    for (x in p0.children) {
                        var trip = x.getValue(Trips::class.java)
                        if (trip!!.from.contains(searchFrom.text.toString())
                            && trip.to.contains(searchTo.text.toString())
                            && trip.createdBy != mAuth.currentUser?.uid)
                            mTripList!!.add(0, trip) }
                    var tripAdapter = CreatedTripAdapter(context!!.applicationContext, mTripList!!)
                    listOfSearchTrip.adapter = tripAdapter } }) }
    }


}
