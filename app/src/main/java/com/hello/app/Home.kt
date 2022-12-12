package com.hello.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.hello.app.databinding.FragmentHomeBinding
import com.hello.app.eventbus.UpdateCartEvent
import com.hello.app.listener.ICartLoadListener
import com.hello.app.listener.IItemLoadListener
import com.hello.app.model.CartModel
import com.hello.app.model.Items
import com.nex3z.notificationbadge.NotificationBadge
import kotlinx.android.synthetic.main.fragment_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import com.hello.app.R
import com.hello.app.databinding.ActivityItemInfoBinding.inflate
import com.hello.app.databinding.ActivityOrderFlowersBinding.inflate


class Home : Fragment(), IItemLoadListener, ICartLoadListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var context1: Context
    private lateinit var dbref : DatabaseReference
    private lateinit var userRecyclerview : RecyclerView
    private lateinit var userArrayList : ArrayList<Items> //itemModels
    private lateinit var navBar : BottomNavigationView
    var notificationBadge: NotificationBadge? = null
    lateinit var itemLoadListener: IItemLoadListener
    lateinit var cartLoadListener: ICartLoadListener
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if (EventBus.getDefault().hasSubscriberForEvent(UpdateCartEvent::class.java))
            EventBus.getDefault().removeStickyEvent(UpdateCartEvent::class.java)
        EventBus.getDefault().unregister(this)
    }
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onUpdateCartEvent(event: UpdateCartEvent){
        countCartFromFirebase()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        context1 = requireContext()
        val menu = PopupMenu(context1, view)


        userRecyclerview = binding.recyclerViewItemsMain
        userRecyclerview.layoutManager = LinearLayoutManager(context1)
        userRecyclerview.setHasFixedSize(true)
        userArrayList = arrayListOf<Items>()
        init()
        //loadItemFromFirebase()
        getUserData()
        countCartFromFirebase()
        binding.button1.setOnClickListener{
            val popupMenu = PopupMenu(context1, it)
            popupMenu.setOnMenuItemClickListener { item ->
                when(item.itemId){
                    R.id.sortUp -> {
                        Toast.makeText(context1, "По возрастанию", Toast.LENGTH_SHORT).show()
                        userArrayList.sortBy { it.price }
                        //TODO пофиксить sort
                        //TODO спросить как адекватно почистить предыдущее
                        userRecyclerview.adapter?.notifyDataSetChanged()
                        //getUserData()
                        true
                    }
                    R.id.sortDown -> {
                        Toast.makeText(context1, "По убыванию", Toast.LENGTH_SHORT).show()
                        userArrayList.sortByDescending { it.price }
                        //getUserData()
                        userRecyclerview.adapter?.notifyDataSetChanged()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.inflate(R.menu.popup_menu)
            popupMenu.show()
        }

        return binding.root
    }



        private fun countCartFromFirebase() {
        val cartModels: MutableList<CartModel> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (cartSnapshot in snapshot.children){
                        val cartModel = cartSnapshot.getValue(CartModel::class.java)
                        cartModel!!.key = cartSnapshot.key
                        cartModels.add(cartModel)
                    }
                    cartLoadListener.onLoadCartSuccess(cartModels as ArrayList<CartModel>)
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener.onLoadCartFailed(error.message)
                }

            })
    }


    private fun getUserData() {
        dbref = FirebaseDatabase.getInstance().getReference("Items")
        dbref.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    for (userSnapshot in snapshot.children){

                        val item = userSnapshot.getValue(Items::class.java)
                        item!!.key = userSnapshot.key
                        userArrayList.add(item)

                    }
                    itemLoadListener.onItemLoadSuccess(userArrayList)




                }else{
                    itemLoadListener.onItemLoadFailed("Items not exists")
            }

            }

            override fun onCancelled(error: DatabaseError) {

            }



        })

    }
    private fun init(){
        itemLoadListener = this
        cartLoadListener = this
        userRecyclerview.apply { layoutManager = GridLayoutManager(context1, 2) }
    }

    override fun onItemLoadSuccess(ItemModelList: ArrayList<Items>?) {
        val adapter = ItemModelList?.let { MyAdapter(ItemModelList, context1) }
        userRecyclerview.adapter = adapter

        adapter?.setOnItemClickListener(object: MyAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val intent = Intent(context1, ItemInfo::class.java)
                intent.putExtra("name",ItemModelList[position].name)
                intent.putExtra("price",ItemModelList[position].price)
                intent.putExtra("structure",ItemModelList[position].structure)
                intent.putExtra("image", ItemModelList[position].image.toString())
                startActivity(intent)
            }
        })
    }

    override fun onItemLoadFailed(message: String?) {
        if (message != null) {
            Snackbar.make(HomeFrame, message, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onLoadCartSuccess(cartModelList: ArrayList<CartModel>) {

        var cartSum = 0
        for (cartModel in cartModelList) cartSum+=cartModel.quantity
        notificationBadge?.setNumber(cartSum)
    }

    override fun onLoadCartFailed(message: String?) {
        if (message != null) {
            Snackbar.make(HomeFrame, message, Snackbar.LENGTH_LONG).show()
        }
    }


}