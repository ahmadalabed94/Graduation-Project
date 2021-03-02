package com.app.carsharing

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private val createdTrips = CreatedTrips()
    private val bookingTrips = BookingTrips()
    private val search = Search()
    private val more = More()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnView.setOnNavigationItemSelectedListener(this@MainActivity)
        setFragment(createdTrips)
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.bottom_navigation_created_trip -> {
               setFragment(createdTrips)
                return true
            }
            R.id.bottom_navigation_book_trip -> {
                setFragment(bookingTrips)
                return true
            }
            R.id.bottom_navigation_search -> {
                setFragment(search)
                return true
            }
            R.id.bottom_navigation_more -> {
                setFragment(more)
                return true
            }
            else -> return false
        }

    }
    private fun setFragment(fragment: Fragment) {
        val fr = supportFragmentManager.beginTransaction()//.addToBackStack(null)
        fr.replace(R.id.coordinatorLayout_main, fragment)
        fr.commit()
    }


}



//*
// override fun onBackPressed() {
//        super.onBackPressed()
//
//    }
// */
/*  if (createdTrips.isHidden)
            setFragment(createdTrips)
        else {
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(1)
        }*/