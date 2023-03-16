package com.vam.arouterapi

import android.view.View

fun View.click(l:(View)->Unit){
    setOnClickListener(l)
}