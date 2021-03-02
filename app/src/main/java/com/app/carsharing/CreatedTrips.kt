package com.app.carsharing



import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.edit_trip.view.*
import kotlinx.android.synthetic.main.fragment_created_trips.*
import kotlinx.android.synthetic.main.fragment_created_trips.view.*
import kotlin.Exception


class CreatedTrips : Fragment() {

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
        var v = inflater.inflate(R.layout.fragment_created_trips, container, false)
        val database = FirebaseDatabase.getInstance()
        mRef = database.getReference("Trips")
        mTripList = ArrayList()
        mRef2 = database.getReference("BookTrip")

        v.btnAddTrip.setOnClickListener {
            startActivity(Intent(activity, AddTrip::class.java))
        }
        v.listOfCreatedTrip.setOnItemClickListener { parent, view, position, id ->
            val alertBuilder = AlertDialog.Builder(context)
            var view = layoutInflater.inflate(R.layout.edit_trip,null)
            val alertDialog = alertBuilder.create()
            alertDialog.setView(view)
            alertDialog.show()
            var trip = mTripList?.get(position)!!

            view.edtTxtFromEditTrip.setText(trip.from)
            view.edtTxtToEditTrip.setText(trip.to)
            view.edtTxtDateEditTrip.setText(trip.dateTrip)
            view.edtTxtTimeEditTrip.setText(trip.timeTrip)
            view.edtTxtNumOfSeatsEditTrip.setText(trip.seats.toString())
            view.edtTxtFareEditTrip.setText(trip.fare.toString())

            view.createdListBtnEdit.setOnClickListener {
                try{
                    val tripId = mRef?.child(trip.tripId.toString())
                    val from = view.edtTxtFromEditTrip.text.toString()
                    val to = view.edtTxtToEditTrip.text.toString()
                    val dateTrip = view.edtTxtDateEditTrip.text.toString()
                    val timeTrip = view.edtTxtTimeEditTrip.text.toString()
                    val seats = view.edtTxtNumOfSeatsEditTrip.text.toString().toInt()
                    val fare = view.edtTxtFareEditTrip.text.toString().toDouble()
                    val createdBy = mAuth.currentUser?.uid
                    val afterUpdate = Trips(trip.tripId,from,to,dateTrip,timeTrip,seats,fare,createdBy)
                tripId?.setValue(afterUpdate)
                }catch (e:Exception){Toast.makeText(context!!.applicationContext,e.message.toString(),Toast.LENGTH_LONG).show()}
                alertDialog.dismiss()
            }
            view.createdListBtnDel.setOnClickListener {
                try {
                    mRef?.child(trip.tripId!!)?.removeValue()
                    mRef2?.child(trip.tripId!!)?.removeValue()
                }catch (e:Exception){Toast.makeText(context!!.applicationContext,e.message.toString(),Toast.LENGTH_LONG).show()}
                alertDialog.dismiss()
            }
            view.createdListBtnShowUsers.setOnClickListener {
                var intent = Intent(context,ShowUsers::class.java)
                intent.putExtra("Trip_Id", trip.tripId.toString())
                startActivity(intent)
                alertDialog.dismiss()
            }
            false
        }
        return v
    }




    override fun onStart() {
        super.onStart()
        loadingTrips()
    }

    fun loadingTrips(){
        mRef?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                mTripList?.clear()
                for (x in p0!!.children) {
                    var trip = x.getValue(Trips::class.java)
                    if (trip!!.createdBy.equals(mAuth.currentUser?.uid)) {
                        mTripList!!.add(0, trip!!)
                    }
                }
                var tripAdapter = CreatedTripAdapter(context!!.applicationContext, mTripList!!)
                listOfCreatedTrip.adapter = tripAdapter
                prgsBarCreatedTrips.visibility = View.INVISIBLE
            }
        })
    }

}
