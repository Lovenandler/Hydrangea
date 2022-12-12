package com.hello.app.model

data class CartModel(
    var image:String? = null,
    var key:String? = null,
    var name:String? = null,
    var price:String? = null,
    var quantity: Int = 0,
    var totalPrice: Int = 0)
{}