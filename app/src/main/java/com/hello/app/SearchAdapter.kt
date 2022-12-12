package com.hello.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hello.app.model.Items

class SearchAdapter (private val SearchItemsList : ArrayList<Items>) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(clickListener: onItemClickListener){
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.items_main,
            parent,false)
        return SearchViewHolder(itemView, mListener)

    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {

        val currentitem = SearchItemsList[position]

        holder.titleItem.text = currentitem.name
        holder.priceItem.text = currentitem.price

    }

    override fun getItemCount(): Int {

        return SearchItemsList.size
    }


    class SearchViewHolder(itemView : View, clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView){

        val titleItem : TextView = itemView.findViewById(R.id.txtName)
        val priceItem : TextView = itemView.findViewById(R.id.txtPrice)
        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(bindingAdapterPosition)
            }
        }
    }

}
