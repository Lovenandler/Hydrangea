package com.hello.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.hello.app.databinding.ActivitySignUpBinding


class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    /*private lateinit var firebaseDatabase: FirebaseDatabase*/
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.alreadySignInText.setOnClickListener{
            val intent = Intent(this, SignInFirebase::class.java)
            startActivity(intent)
            finish()
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        binding.SignUpBtn.setOnClickListener{
            var email = binding.emailEnterSignUp.text.toString()
            var password = binding.passwordEnterSignUp.text.toString()
            var confirmPassword = binding.confirmPasswordEnterSignUp.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
                if(password == confirmPassword){
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                        if(it.isSuccessful){

                            val userID = databaseReference.push().key!!
                            val user = User(userID, email, password)
                            databaseReference.child(userID).setValue(user)
                                .addOnCompleteListener{
                                    Toast.makeText(this, "Данные добавлены", Toast.LENGTH_SHORT).show()
                                }.addOnFailureListener{
                                    Toast.makeText(this, "У тебя ничего не вышло", Toast.LENGTH_SHORT).show()
                                }
                                val intent = Intent(this, SignInFirebase::class.java)
                                startActivity(intent)
                                finish()
                        }else{
                            Toast.makeText(this,it.exception.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
                }else{
                    Toast.makeText(this,"Пароли не совпадают", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Заполните поля", Toast.LENGTH_SHORT).show()
            }
        }
    }

}