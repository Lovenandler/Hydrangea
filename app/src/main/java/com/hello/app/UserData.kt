package com.hello.app

class UserData {
    private lateinit var name: String
    private lateinit var price: String
    constructor(name: String, price: String){
        this.name = name
        this.price = price
    }
    fun getName(): String {
        return name
    }
    fun getPrice(): String {
        return price
    }
}