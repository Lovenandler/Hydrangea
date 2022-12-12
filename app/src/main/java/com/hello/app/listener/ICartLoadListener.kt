package com.hello.app.listener

import com.hello.app.model.CartModel

interface ICartLoadListener {
    fun onLoadCartSuccess(cartModelList: ArrayList<CartModel>)
    fun onLoadCartFailed(message: String?)
}