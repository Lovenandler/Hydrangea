package com.hello.app

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.hello.app.databinding.ActivityItemInfoBinding
import com.hello.app.eventbus.UpdateCartEvent
import com.hello.app.model.CartModel
import com.hello.app.model.Items
import com.nex3z.notificationbadge.NotificationBadge
import kotlinx.android.synthetic.main.activity_item_info.*
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder


class ItemInfo : AppCompatActivity() {
    private lateinit var tvEmpId: TextView
    private lateinit var tvEmpPrice: TextView
    private lateinit var tvStructure: TextView
    private lateinit var tvImage: ImageView
    private lateinit var img: String
    private lateinit var dbref: DatabaseReference
    private lateinit var userArrayList: ArrayList<Items>
    private lateinit var Cart: CartModel

    private lateinit var binding: ActivityItemInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityItemInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tvImage = findViewById(R.id.ItemInfoImg)
        tvStructure = findViewById(R.id.includeInfo)
        tvEmpId = findViewById(R.id.NameTextInfo)
        tvEmpPrice = findViewById(R.id.PriceTextInfo)

        userArrayList = arrayListOf<Items>()
        setValuesToViews()
        var image = tvImage.toString()
        var name = tvEmpId.text.toString()
        var nameStructure = tvEmpId.text.toString().trim().lowercase()
        var structure = tvStructure.text.toString()
        var price = tvEmpPrice.text.toString().toFloat()
        var priceForOne = tvEmpPrice.text.toString().toFloat()
        var quantity = binding.countTxt.text.toString()

        val itemID = FirebaseDatabase.getInstance().reference.push().key!!
        Cart =
            CartModel(image, itemID, name, priceForOne.toString(), quantity.toInt(), price.toInt())
        addToCartBtn.setOnClickListener {
            FirebaseDatabase.getInstance().getReference("Cart")
                .child("UNIQUE_USER_ID").child(name)
                .setValue(Cart)
                .addOnSuccessListener {
                    Toast.makeText(this@ItemInfo, "Добавлено", Toast.LENGTH_LONG)
                        .show()
                }.addOnFailureListener {
                    Toast.makeText(this@ItemInfo, "Не добавлено", Toast.LENGTH_LONG)
                        .show()
                }
        }


    }


    private fun updateFirebase(currentItem: CartModel) {
        FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")
            .child(currentItem.name!!)
            .setValue(currentItem)
            .addOnSuccessListener {
                EventBus.getDefault().postSticky(UpdateCartEvent())
            }
    }


    private fun setValuesToViews() {
        tvEmpId.text = intent.getStringExtra("name")
        tvStructure.text = intent.getStringExtra("structure")
        tvEmpPrice.text = intent.getStringExtra("price")
        img = intent.getStringExtra("image").toString()
        Glide.with(getApplicationContext()).load(img).into(tvImage)

    }
}










