package com.hello.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import com.hello.app.SignInFirebase.Companion.EXTRA_EMAIL
import com.hello.app.SignInFirebase.Companion.EXTRA_NAME
import com.hello.app.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navBarBinding : BottomNavigationView

    private lateinit var navBar : NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment2 = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navHostFragment = navHostFragment2
        navBar = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigation_view)
        setupWithNavController(bottomNavigationView, navBar)


        var str = intent.extras?.getString(EXTRA_NAME)
        val email = intent.extras?.getString(EXTRA_EMAIL)
        val bundle = Bundle()
        bundle.putString("qwe", str)
        val myFrag = UserProfile()
        myFrag.arguments = bundle
        val bundleCart = Bundle()
        bundleCart.putString("valueEmail", email)
        bundleCart.putString("qwe", str)
        val myFragCart = Cart()
        myFragCart.arguments = bundleCart
        /*val intent = Intent(this, orderFlowers::class.java)
        intent.putExtra("email", email)*/
        //startActivity(intent)

        navBarBinding = binding.navigationView
        navBarBinding.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home2 -> replaceFragment(Home())
                R.id.search -> replaceFragment(Search())
                R.id.cartMenu -> replaceFragment(myFragCart)
                R.id.userMenu -> replaceFragment(myFrag)
                else -> {

                }

            }
            true
        }
    }
    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment)
        fragmentTransaction.commit()
        fragmentTransaction.show(fragment)

    }
}
