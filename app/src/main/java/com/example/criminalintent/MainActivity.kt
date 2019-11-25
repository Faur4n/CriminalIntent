package com.example.criminalintent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import java.util.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity()
   // , CrimeListFragment.Callbacks
{


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nav_host_navigation)

//        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
//
//        if (currentFragment == null) {
//            val fragment = CrimeListFragment.newInstance()
//            supportFragmentManager
//                .beginTransaction()
//                .add(R.id.fragment_container, fragment)
//                .commit()
//        }

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment? ?: return
        val navController = host.navController


    }



//    override fun OnCrimeSelected(crimeId: UUID) {
//        val fragment = CrimeFragment.newInstance(crimeId)
//        supportFragmentManager
//            .beginTransaction()
//            .replace(R.id.fragment_container, fragment)
//            .addToBackStack(null)
//            .commit()
//    }
}
