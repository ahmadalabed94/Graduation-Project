package com.app.carsharing

data class Trips(val tripId:String? , val from:String , val to:String , val dateTrip:String ,
                 val timeTrip:String , val seats:Int , val fare:Double,val createdBy:String?) {
    constructor() : this("","","","","",0,0.0,"") {}
}