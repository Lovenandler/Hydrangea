package com.hello.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.hello.app.databinding.FragmentSearchBinding
import com.hello.app.model.Items
import kotlinx.android.synthetic.main.fragment_search.*


class Search() : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var contextSearch: Context
    private lateinit var dbrefSearch : DatabaseReference
    private lateinit var searchRecyclerview : RecyclerView
    private lateinit var searchArrayList : ArrayList<Items>
    private lateinit var searchBtn: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        contextSearch = requireContext()

        searchRecyclerview = binding.searchView

        searchRecyclerview.layoutManager = LinearLayoutManager(contextSearch)
        searchRecyclerview.setHasFixedSize(true)
        searchArrayList = arrayListOf<Items>()
        searchBtn = binding.SearchBtn
        searchBtn.setOnClickListener{
            val searchText = addressViewEnterTxt.text.toString().trim().lowercase()
            if (searchText.isNotEmpty()){
                getUserData(searchText)
            }
            else{
                Toast.makeText(contextSearch,"Введите название товара",Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }
    private fun getUserData(Search: String) {
        FirebaseDatabase.getInstance()
            .reference
            .child("Items")
            .orderByChild("name")
            //.equalTo(Search)
            .addValueEventListener(object:ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    searchArrayList.clear()
                    for (userSnapshot in snapshot.children){


                        val item = userSnapshot.getValue(Items::class.java)
                        if( Search == item?.name?.lowercase() || item?.name?.lowercase()?.contains(Search) == true){
                            searchArrayList.add(item)
                        }

                    }
                    val adapter = MyAdapter(searchArrayList, contextSearch)
                    searchRecyclerview.adapter = adapter
                    adapter.setOnItemClickListener(object: MyAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val intent = Intent(contextSearch, ItemInfo::class.java)
                            intent.putExtra("name",searchArrayList[position].name)
                            intent.putExtra("price",searchArrayList[position].price)
                            intent.putExtra("image", searchArrayList[position].image.toString())
                            startActivity(intent)
                        }
                    })


                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

    }


}




