package com.app.carsharing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_trip.*

class AddTrip : AppCompatActivity(), TextWatcher {

    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    var mRef:DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_trip)

        val database = FirebaseDatabase.getInstance()
        mRef = database.getReference("Trips")

        edtTxtChangeListener()

        btnAddNewTrip.setOnClickListener {
            createTrip()
            startActivity(Intent(this@AddTrip,MainActivity::class.java))
        }
    }

    private fun createTrip() {
        val TripId = mRef!!.push().key
        val from = edtTxtFromAddTrip.text.toString()
        val to = edtTxtToAddTrip.text.toString()
        val dateTrip = edtTxtDateAddTrip.text.toString()
        val timeTrip = edtTxtTimeAddTrip.text.toString()
        val seats:Int = edtTxtNumOfSeatsAddTrip.text.toString().toInt()
        val fare:Double = edtTxtFareAddTrip.text.toString().toDouble()
        val createdBy = mAuth.currentUser?.uid
        var newTrip = Trips(TripId,from,to,dateTrip,timeTrip,seats,fare,createdBy)
        mRef!!.child(TripId!!).setValue(newTrip)
    }


    private fun edtTxtChangeListener() {
        edtTxtFromAddTrip.addTextChangedListener(this@AddTrip)
        edtTxtToAddTrip.addTextChangedListener(this@AddTrip)
        edtTxtDateAddTrip.addTextChangedListener(this@AddTrip)
        edtTxtTimeAddTrip.addTextChangedListener(this@AddTrip)
        edtTxtNumOfSeatsAddTrip.addTextChangedListener(this@AddTrip)
        edtTxtFareAddTrip.addTextChangedListener(this@AddTrip)
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        enableBtnAddNewTrip()
    }

    private fun enableBtnAddNewTrip() {
        btnAddNewTrip.isEnabled = edtTxtFromAddTrip.text.trim().isNotEmpty()
                && edtTxtToAddTrip.text.trim().isNotEmpty() && edtTxtDateAddTrip.text.trim().isNotEmpty()
                && edtTxtTimeAddTrip.text.trim().isNotEmpty() && edtTxtNumOfSeatsAddTrip.text.trim().isNotEmpty()
                && edtTxtFareAddTrip.text.trim().isNotEmpty()
    }
}
