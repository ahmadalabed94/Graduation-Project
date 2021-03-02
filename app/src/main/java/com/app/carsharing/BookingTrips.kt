package com.app.carsharing

import android.app.AlertDialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.carsharing.glide.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.book_trip.view.*
import kotlinx.android.synthetic.main.fragment_booking_trips.*
import kotlinx.android.synthetic.main.fragment_booking_trips.view.*
import kotlinx.android.synthetic.main.fragment_created_trips.*


class BookingTrips : Fragment() {

    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    var mRef: DatabaseReference? = null
    var mTripList: ArrayList<Trips>? = null
    var mRef2: DatabaseReference? = null
    private val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    private val currentUserStorageRef: StorageReference
        get() = storageInstance.reference.child(FirebaseAuth.getInstance().currentUser?.uid.toString())

    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v = inflater.inflate(R.layout.fragment_booking_trips, container, false)
        val database = FirebaseDatabase.getInstance()
        mRef = database.getReference("Trips")
        mTripList = ArrayList()
        mRef2 = database.getReference("BookTrip")

        v.listBookingTrips.setOnItemClickListener { parent, view, position, id ->
            val alertBuilder = AlertDialog.Builder(context)
            var view = layoutInflater.inflate(R.layout.book_trip, null)
            val alertDialog = alertBuilder.create()
            alertDialog.setView(view)
            alertDialog.show()

            var trip = mTripList?.get(position)!!
            view.btnBookTrip.text = "Cancel Book"
            view.btnBookTrip.setBackgroundResource(R.drawable.btn_delete_account)
            val info = db.collection("Users").document(trip.createdBy.toString())
            info.get().addOnSuccessListener {
                val userInfo = it.toObject(User::class.java)
                view.txtUserCreator.text = userInfo?.name.toString()
                view.txtEmailUserCreator.text = userInfo?.email.toString()
                if (userInfo?.imageProfile!!.isNotEmpty()) {
                    GlideApp.with(activity!!)
                        .load(storageInstance.getReference(userInfo?.imageProfile))
                        .into(view.imgUserCreator) } }
            view.btnBookTrip.setOnClickListener {
                mRef2!!.child(trip.tripId.toString()).child(mAuth.currentUser?.uid.toString()).removeValue()
                    var edtTrip = Trips(trip.tripId.toString(), trip.from, trip.to, trip.dateTrip,
                            trip.timeTrip, (trip.seats+1), trip.fare, trip.createdBy.toString())
                    mRef!!.child(trip.tripId.toString()).setValue(edtTrip)
                onStart()
                alertDialog.dismiss()
            }
            false
        }
        return v
    }

    override fun onStart() {
        super.onStart()
        mRef?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                mTripList?.clear()
                for (x in p0!!.children) {
                    var trip = x.getValue(Trips::class.java)
                    mRef2?.child(trip!!.tripId.toString())!!.addValueEventListener(object : ValueEventListener
                    {
                        override fun onCancelled(p0: DatabaseError) {
                        }
                        override fun onDataChange(p0: DataSnapshot) {
                            for (x2 in p0!!.children) {
                                var x3 = x2.getValue(String::class.java)
                                if (x3.toString().equals(mAuth.currentUser?.uid.toString())) {
                                    mTripList!!.add(0, trip!!) } }
                            var tripAdapter = CreatedTripAdapter(context!!.applicationContext, mTripList!!)
                            listBookingTrips.adapter = tripAdapter
                            prgsBarBookingTrips.visibility = View.INVISIBLE } })
                }
            }
        })
    }

}
