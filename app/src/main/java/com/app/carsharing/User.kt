package com.app.carsharing

data class User (val name:String,val birthDay:String,val sex:String,val imageProfile:String,val email:String){
    constructor():this("","","","","")
}