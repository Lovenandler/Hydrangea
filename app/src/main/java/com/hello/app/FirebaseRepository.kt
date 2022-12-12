package com.hello.app

import android.app.Activity
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class FirebaseRepository(): AppCompatActivity() {

    /*private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Items")
    private val searchViewModel: SearchViewModel by viewModels()
//    constructor(searchViewModel: SearchViewModel) : this() {
//        this.SearchViewModel = searchViewModel
//    }
    fun searchItemFirebase(searchtxt: String, userDataList: Flow<ArrayList<UserData>>) {
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                lifecycleScope.launch {
                    //userDataList.clear()
                    for (dataChange in snapshot.children){
                        val userList = dataChange.getValue(UserData::class.java)
                        if (userList!!.getName().contains(searchtxt)){
                            userDataList.map {

                            }
                            //userDataList.add(userList)
                        }
                        searchViewModel?.setAdapter(userDataList)
                    }
                }
//                userDataList.clear()
//                for (dataChange in snapshot.children){
//                    val userList = dataChange.getValue(UserData::class.java)
//                    if (searchtxt.isEmpty()){
//                        userDataList.clear()
//                    }else if (userList!!.getName().contains(searchtxt)){
//                        userDataList.add(userList)
//                    }
//                    SearchViewModel?.setAdapter(userDataList)
//                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }*/
}
