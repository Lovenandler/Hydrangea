package com.hello.app.listener

import android.view.View

interface IRecyclerClickListener {
    fun onItemClickListener(view: View?, position: Int)
}