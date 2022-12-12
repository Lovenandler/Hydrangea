package com.hello.app.listener

import com.hello.app.model.Items

interface IItemLoadListener {
    fun onItemLoadSuccess(ItemModelList:ArrayList<Items>?)
    fun onItemLoadFailed(message: String?)
}