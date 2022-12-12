package com.hello.app.adapter

import android.app.AlertDialog
import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.hello.app.Cart
import com.hello.app.MyAdapter
import com.hello.app.R
import com.hello.app.eventbus.UpdateCartEvent
import com.hello.app.listener.ICartLoadListener
import com.hello.app.model.CartModel
import com.hello.app.model.Items
import org.greenrobot.eventbus.EventBus
import org.w3c.dom.Text
import java.lang.StringBuilder
import java.util.ArrayList

class MyCartAdapter(private val cartList : ArrayList<CartModel>,  private val context: Context): RecyclerView.Adapter<MyCartAdapter.MyCartViewHolder>() {
    private lateinit var mListener: onItemClickListener
    interface onItemClickListener{
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(clickListener: onItemClickListener){
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCartViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cart_item,
            parent,false)
        return MyCartViewHolder(itemView, mListener)

    }
    override fun onBindViewHolder(holder: MyCartViewHolder, position: Int) {

        val currentitem = cartList[position]

        holder.titleItem.text = currentitem.name
        holder.priceItem.text = StringBuilder("₽").append(currentitem.totalPrice)
        Glide.with(context)
            .load(currentitem.image)
            .into(holder.imageItem)
        holder.quantityItem.text = currentitem.quantity.toString()
        holder.btnPlus.setOnClickListener{_ ->
            currentitem.quantity+=1
            currentitem.totalPrice = (currentitem.quantity * currentitem.price!!.toFloat()).toInt()
            holder.quantityItem.text = StringBuilder("").append(currentitem.quantity)
            updateFirebase(currentitem)
        }
        holder.btnMinus.setOnClickListener{_ ->
            if(currentitem.quantity > 1){
                currentitem.quantity-=1
                currentitem.totalPrice = (currentitem.quantity * currentitem.price!!.toFloat()).toInt()
                holder.quantityItem.text = StringBuilder("").append(currentitem.quantity)
                updateFirebase(currentitem)
            }
        }
        holder.btnDelete.setOnClickListener{_ ->
            val dialog = AlertDialog.Builder(context)
                .setTitle("Удалить")
                .setMessage("Вы действительно хотите удалить из корзины?")
                .setNegativeButton("Нет") {dialog, _ -> dialog.dismiss()}
                .setPositiveButton("Да") {dialog, _ ->
                    notifyItemRemoved(position)
                    FirebaseDatabase.getInstance()
                        .getReference("Cart")
                        .child("UNIQUE_USER_ID")
                        .child(currentitem.name!!)
                        .removeValue()
                        .addOnSuccessListener { EventBus.getDefault().postSticky(UpdateCartEvent()) }
                }
                .create()
            dialog.show()

        }

    }

    fun getItem(position: Int): CartModel {
        return cartList[position]
    }

    private fun updateFirebase(currentItem: CartModel) {
        FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID") //сюда передавать userid
            .child(currentItem.name!!)
            .setValue(currentItem)
            .addOnSuccessListener {
                EventBus.getDefault().postSticky(UpdateCartEvent())
            }
    }

    override fun getItemCount(): Int {

        return cartList.size
    }
    class MyCartViewHolder(itemView : View, clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView){

        val titleItem : TextView = itemView.findViewById(R.id.txtName)
        val btnDelete : ImageView = itemView.findViewById(R.id.btnDelete)
        val btnPlus : ImageView = itemView.findViewById(R.id.btnPlus)
        val btnMinus : ImageView = itemView.findViewById(R.id.btnMinus)
        val priceItem : TextView = itemView.findViewById(R.id.txtPrice)
        val imageItem : ImageView = itemView.findViewById(R.id.cartImageItem)
        val quantityItem : TextView = itemView.findViewById(R.id.txtQuantity)
        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(bindingAdapterPosition)
            }
        }
    }




}