package com.hello.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hello.app.R
import com.hello.app.eventbus.UpdateCartEvent
import com.hello.app.listener.ICartLoadListener
import com.hello.app.listener.IRecyclerClickListener
import com.hello.app.model.CartModel
import com.hello.app.model.Items

class ItemInfoAdapter (private val itemsList: ArrayList<Items>,
                       private val context: Context,
                       private val cartListener: ICartLoadListener
) : RecyclerView.Adapter<ItemInfoAdapter.MyItemViewHolder>() {
    class MyItemViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var imageItemView: ImageView? = null
        var titleItem: TextView? = null
        var priceItem: TextView? = null
        private var clickListener: IRecyclerClickListener? = null
        fun setClickListener(clickListener: IRecyclerClickListener) {
            this.clickListener = clickListener
        }

        init {
            titleItem = itemView.findViewById(R.id.txtNameCart) as TextView
            priceItem = itemView.findViewById(R.id.txtPriceCart) as TextView
            imageItemView = itemView.findViewById(R.id.imageViewItem) as ImageView
            itemView.setOnClickListener(this)

        }

        override fun onClick(v: View?) {
            clickListener?.onItemClickListener(v, adapterPosition)
        }


    }
    private lateinit var mListener: onItemClickListener
    var ItemsFilterList = ArrayList<Items>()
    interface onItemClickListener{
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(clickListener: onItemClickListener){
        mListener = clickListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemInfoAdapter.MyItemViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.items_main,
            parent,false)
        return MyItemViewHolder(itemView/*, mListener*/)

    }

    override fun onBindViewHolder(holder: MyItemViewHolder, position: Int) {

        val currentitem = itemsList[position]


        holder.titleItem?.text = StringBuilder().append(currentitem.name)
        holder.priceItem?.text = StringBuilder("₽").append(currentitem.price)
        Glide.with(context)
            .load(currentitem.image)
            .into(holder.imageItemView!!)
        holder.setClickListener(object: IRecyclerClickListener{
            override fun onItemClickListener(view: View?, position: Int) {
                addToCart(currentitem)
            }

        })


    }

    private fun addToCart(currentitem: Items) {
        val userCart = FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")
        userCart.child(currentitem.key!!)
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val cartModel = snapshot.getValue(CartModel::class.java)
                        val updateData: MutableMap<String, Any> = HashMap()
                        cartModel!!.quantity = cartModel.quantity+1
                        updateData["quantity"] = cartModel.quantity
                        updateData["totalPrice"] = cartModel.quantity * cartModel.price!!.toFloat()
                        userCart.child(currentitem.key!!)
                            .updateChildren(updateData)
                            .addOnSuccessListener {
                                org.greenrobot.eventbus.EventBus.getDefault().postSticky(
                                    UpdateCartEvent()
                                )
                                cartListener.onLoadCartFailed("Added")
                            }
                            .addOnFailureListener{e-> cartListener.onLoadCartFailed(e.message)}
                    }else{
                        val cartModel = CartModel()
                        cartModel.key = currentitem.key
                        cartModel.name = currentitem.name
                        cartModel.image = currentitem.image
                        cartModel.price = currentitem.price
                        cartModel.quantity = 1
                        cartModel.totalPrice = currentitem.price!!.toFloat().toInt()
                        userCart.child(currentitem.key!!)
                            .setValue(cartModel)
                            .addOnSuccessListener {
                                org.greenrobot.eventbus.EventBus.getDefault().postSticky(UpdateCartEvent())
                                cartListener.onLoadCartFailed("Successful")
                            }
                            .addOnFailureListener { e-> cartListener.onLoadCartFailed(e.message) }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    cartListener.onLoadCartFailed(error.message)
                }
            })
    }


    override fun getItemCount(): Int {

        return itemsList.size
    }


}