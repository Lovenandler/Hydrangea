package com.hello.app

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.hello.app.databinding.ActivitySignInFirebaseBinding


class SignInFirebase : AppCompatActivity() {
    private lateinit var binding: ActivitySignInFirebaseBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInFirebaseBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        setContentView(binding.root)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("426565248460-f4ufn61kfp8r9t146n5fru1b04rqccek.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        binding.GoogleSignInBtn.setOnClickListener {
            signIn()
        }

        binding.SignUpMessage.setOnClickListener{
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            finish()
        }
        binding.SignInBtn.setOnClickListener{
            val email = binding.emailEnterSignIn.text.toString()
            val password = binding.passwordEnterSignIn.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                    if(it.isSuccessful){
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra(EXTRA_EMAIL, email)
                        intent.putExtra(EXTRA_NAME, email)
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(this,it.exception.toString(), Toast.LENGTH_LONG).show()
                    }
                }

            }else{
                Toast.makeText(this, "Заполните поля", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = firebaseAuth.currentUser
                    //updateUI(user)
                    val intent = Intent(this, MainActivity::class.java)
                    if (user != null) {
                        intent.putExtra(EXTRA_NAME, user.displayName)
                        intent.putExtra(EXTRA_EMAIL, user.email)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val fragmentManager: FragmentManager = supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            val myFragment = UserProfile()
            val bundle = Bundle()
            bundle.putString("message", user.displayName)
            myFragment.arguments = bundle
            //fragmentTransaction.add(R.id.fragmentContainerView, myFragment).commit()


        //fragmentTransaction.show(myFrag)

            /*val bundle = Bundle()
            bundle.putString(EXTRA_NAME, user.displayName)
            val myFrag = UserProfile()
            myFrag.arguments = bundle*/
        }
    }

    companion object {
        const val RC_SIGN_IN = 1001
        const val EXTRA_NAME = "EXTRA_NAME"
        const val EXTRA_EMAIL = "EXTRA_EMAIL"
    }
}