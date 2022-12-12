package com.hello.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.hello.app.adapter.MyCartAdapter
import com.hello.app.databinding.ActivityOrderFlowersBinding
import com.hello.app.eventbus.UpdateCartEvent
import com.hello.app.model.CartModel
import kotlinx.android.synthetic.main.cart_item.view.*
import org.greenrobot.eventbus.EventBus
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.arrayListOf
import kotlin.collections.forEach
import kotlin.collections.set


class orderFlowers : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityOrderFlowersBinding
    private lateinit var PaymentBtn: Button
    private lateinit var databaseReference: DatabaseReference
    private lateinit var addressFullName: TextView
    private lateinit var orderRecyclerview : RecyclerView
    private lateinit var cartArrayList : ArrayList<CartModel>
    private lateinit var mapImgView : ImageView
    private lateinit var dbrefOrder : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderFlowersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addressFullName = findViewById(R.id.addressViewEnterTxt)
        orderRecyclerview = findViewById(R.id.ordersRecyclerView)
        orderRecyclerview.layoutManager = LinearLayoutManager(this)
        orderRecyclerview.setHasFixedSize(true)
        cartArrayList = arrayListOf<CartModel>()
        setValuesToViews()
        getUserData()
        mapImgView = findViewById(R.id.mapImageView)
        val string = intent.extras?.getString("email").toString()
        val stringUser = intent.extras?.getString("username").toString()
        mapImgView.setOnClickListener{
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("email", string)
            startActivity(intent)
        }


        PaymentBtn = findViewById(R.id.payBtn)
        PaymentBtn.setOnClickListener{
            var email = "aliskerova754@gmail.com"

            val ItemName = orderRecyclerview.txtName.text.toString()
            val intent = Intent(this, MainActivity::class.java)

            //TODO дизайн пофиксить
            addToCart(cartArrayList, stringUser)
            sendEmail(email, cartArrayList, string, addressFullName.text.toString())

        }
    }

    private fun addToCart(cartArray: ArrayList<CartModel>, addressUser: String) {
        FirebaseDatabase.getInstance().getReference("Order").child(addressUser).setValue(cartArray)
    }

    private fun sendEmail(email: String?, itemArray: ArrayList<CartModel>, address: String?, addressOrder: String?) {
        val mIntent = Intent(Intent.ACTION_SENDTO).setData("mailto: $address".toUri())
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
            putExtra(Intent.EXTRA_SUBJECT, "Чек по заказу")
            var items = ""
            var price = 0
            itemArray.forEach {
                items += "Букет: ${it.name}\n Количество: ${it.quantity}\n Цена: ${it.totalPrice}\n "+ "\n"
                price += it.totalPrice
            }
            putExtra(Intent.EXTRA_TEXT, items+"Адрес доставки: $addressOrder\n"+"Сумма заказа: $price")
            selector = mIntent
        }
//        mIntent.type = "message/rfc822"
//        mIntent.putExtra(Intent.EXTRA_SUBJECT, "mama")

//        mIntent.putExtra(Intent.EXTRA_EMAIL, address)
//        mIntent.putExtra(Intent.EXTRA_TEXT, /*itemArray.toString()*/ "papa")
//        mIntent.data = Uri.parse("mailto: $address")


        // TODO стринг адекватные
        // TODO перевод значений
        // TODO адекватные названия переменных (убрать предупреждения)

        try{
//            startActivity(Intent.createChooser(mIntent, "Choose Email Client"))
            startActivity(Intent.createChooser(emailIntent, "Choose Email Client"))

        }catch (e: Exception){
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getUserData() {

        dbrefOrder = FirebaseDatabase.getInstance().getReference("Cart").child("UNIQUE_USER_ID")

        dbrefOrder.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    cartArrayList.clear()
                    for (userSnapshot in snapshot.children){


                        val item = userSnapshot.getValue(CartModel::class.java)
                        cartArrayList.add(item!!)

                    }
                    val adapter = MyCartAdapter(cartArrayList, this@orderFlowers)
                    orderRecyclerview.adapter = adapter
                    adapter.setOnItemClickListener(object: MyCartAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val intent = Intent(this@orderFlowers, ItemInfo::class.java)
                            intent.putExtra("name",cartArrayList[position].name)
                            intent.putExtra("price",cartArrayList[position].price)
                            startActivity(intent)
                        }
                    })


                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

    }
    private fun setValuesToViews() {
        addressFullName.text = intent.getStringExtra("address").toString()
    }


}