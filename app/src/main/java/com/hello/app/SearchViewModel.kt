package com.hello.app

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_search.*

class SearchViewModel(private var SearchFragment: Search, private var activity: Activity): ViewModel() {
    /*private var firebaseRepository: FirebaseRepository = FirebaseRepository( this)

    private lateinit var adapterSearch: SearchAdapter
    fun searchItem(userDataList: ArrayList<UserData>){
        SearchFragment.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Toast.makeText(activity,"before text changed",Toast.LENGTH_LONG).show()
            }

            override fun onTextChanged(char: CharSequence?, start: Int, before: Int, count: Int) {
                val searchtxt = SearchFragment.searchEditText.text.toString()
                searchItemFirebase(searchtxt, userDataList)
            }

            override fun afterTextChanged(s: Editable?) {
                Toast.makeText(activity,"after text changed",Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun searchItemFirebase(searchtxt: String, userDataList: ArrayList<UserData>) {
        firebaseRepository.searchItemFirebase(searchtxt, userDataList)
    }

    fun setAdapter(userDataList: ArrayList<com.hello.app.UserData>) {
//        val layoutManager = LinearLayoutManager(activity)
//        SearchFragment.userList.layoutManager = layoutManager
        //Flow
        //LiveData
        adapterSearch = SearchAdapter()
        SearchFragment.userList.adapter = adapterSearch
        adapterSearch.setDataToAdapter(userDataList)
    }*/
}