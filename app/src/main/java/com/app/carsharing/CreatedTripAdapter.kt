package com.app.carsharing

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.created_trip_list.view.*


class CreatedTripAdapter(context: Context,tripList:ArrayList<Trips>)
    : ArrayAdapter<Trips>(context,0,tripList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
       val view = LayoutInflater.from(context).inflate(R.layout.created_trip_list,parent,false)
        val trip: Trips? = getItem(position)
        view.createdListFrom.text = "from: "+trip?.from
        view.createdListTo.text = "to: "+trip?.to
        view.createdListDate.text = "date: "+trip?.dateTrip
        view.createdListTime.text = "time: "+trip?.timeTrip
        view.createdListSeats.text = "Availabel seats: "+trip?.seats.toString()
        view.createdListFare.text = "fare: "+trip?.fare.toString()
        return view
    }
}