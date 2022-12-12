package com.hello.app

import android.R
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.hello.app.SignInFirebase.Companion.EXTRA_NAME
import com.hello.app.databinding.FragmentUserProfileBinding


class UserProfile : Fragment() {
   private lateinit var binding: FragmentUserProfileBinding
   private lateinit var contextUser: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        contextUser = requireContext()
        val arg = arguments
        val strtext = arg?.getString("qwe").toString()
        binding.UserNameTxt.text = strtext.toString()
        binding.LogOutBtn.setOnClickListener{
            val intent = Intent(contextUser, SignInFirebase::class.java)
            FirebaseAuth.getInstance().signOut();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
        return binding.root
    }
}