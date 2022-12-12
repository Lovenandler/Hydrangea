package com.hello.app

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hello.app.eventbus.UpdateCartEvent
import com.hello.app.listener.ICartLoadListener
import com.hello.app.listener.IRecyclerClickListener
import com.hello.app.model.CartModel
import com.hello.app.model.Items
import kotlinx.android.synthetic.main.items_main.view.*
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MyAdapter(private val itemsList: ArrayList<Items>, private val context: Context) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    private lateinit var mListener: onItemClickListener
    interface onItemClickListener{
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(clickListener: onItemClickListener){
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.items_main,
            parent,false)
        return MyViewHolder(itemView, mListener)

    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = itemsList[position]
        holder.favouriteItem.setOnCheckedChangeListener{ _, isChecked ->
          if (isChecked){
            Toast.makeText(context, "Добавлено в любимое", Toast.LENGTH_SHORT).show()
              FirebaseDatabase.getInstance()
                  .getReference("Favourites")
                  .child("UNIQUE_USER_ID") //сюда передавать userid
                  .child(currentitem.name!!)
                  .setValue(currentitem)
                  .addOnSuccessListener {
                      EventBus.getDefault().postSticky(UpdateCartEvent())
                  }
          }else{
            Toast.makeText(context, "Не добавлено", Toast.LENGTH_SHORT).show()
              FirebaseDatabase.getInstance()
                  .getReference("Favourites")
                  .child("UNIQUE_USER_ID")
                  .child(currentitem.name!!)
                  .removeValue()
          }

        }
        holder.titleItem.text = currentitem.name
        holder.priceItem.text = StringBuilder("₽").append(currentitem.price)

        Glide.with(context)
            .load(currentitem.image)
            .into(holder.imageItem)



    }


    override fun getItemCount(): Int {

        return itemsList.size
    }
    class MyViewHolder(itemView : View, clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView){

        val titleItem : TextView = itemView.findViewById(R.id.txtNameCart)
        val priceItem : TextView = itemView.findViewById(R.id.txtPriceCart)
        val favouriteItem: CheckBox = itemView.findViewById(R.id.checkBoxFavourite)
        val imageItem : ImageView = itemView.findViewById(R.id.imageViewItem)
        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(bindingAdapterPosition)
            }
        }
    }



}

