package com.hello.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.hello.app.adapter.MyCartAdapter
import com.hello.app.databinding.FragmentCartBinding
import com.hello.app.model.CartModel
import kotlinx.android.synthetic.main.fragment_cart.*


class Cart : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var contextCart: Context
    private lateinit var dbrefCart : DatabaseReference
    private lateinit var cartRecyclerview : RecyclerView
    private lateinit var cartArrayList : ArrayList<CartModel>
    private lateinit var orderBtn: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        contextCart = requireContext()
        val arg = arguments
        val strtext = arg?.getString("valueEmail").toString()
        val username = arg?.getString("qwe").toString()
        orderBtn = binding.orderBtn
        cartRecyclerview = binding.recyclerCart
        cartRecyclerview.layoutManager = LinearLayoutManager(contextCart)
        cartRecyclerview.setHasFixedSize(true)
        cartArrayList = arrayListOf<CartModel>()
        getUserData()

        orderBtn.setOnClickListener{
            val intent = Intent(contextCart, orderFlowers::class.java)
            intent.putExtra("email", strtext)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        return binding.root
    }
    private fun getUserData() {

        dbrefCart = FirebaseDatabase.getInstance().getReference("Cart").child("UNIQUE_USER_ID")

        dbrefCart.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    cartArrayList.clear()
                    for (userSnapshot in snapshot.children){


                        val item = userSnapshot.getValue(CartModel::class.java)
                        cartArrayList.add(item!!)

                    }
                    val adapter = MyCartAdapter(cartArrayList, contextCart)
                    cartRecyclerview.adapter = adapter

                    adapter.setOnItemClickListener(object: MyCartAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val intent = Intent(contextCart, ItemInfo::class.java)
                            intent.putExtra("name",cartArrayList[position].name)
                            intent.putExtra("price",cartArrayList[position].price)
                            intent.putExtra("image",cartArrayList[position].image.toString())
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
