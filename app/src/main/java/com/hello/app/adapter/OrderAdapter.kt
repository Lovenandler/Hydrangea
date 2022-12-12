package com.hello.app.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.hello.app.R
import com.hello.app.eventbus.UpdateCartEvent
import com.hello.app.model.CartModel
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder
import java.util.ArrayList

class OrderAdapter (private val cartList : ArrayList<CartModel>, private val context: Context): RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
    private lateinit var mListener: onItemClickListener
    interface onItemClickListener{
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(clickListener: onItemClickListener){
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.cart_item,
            parent,false)
        return OrderViewHolder(itemView, mListener)

    }
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {

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
    class OrderViewHolder(itemView : View, clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView){

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