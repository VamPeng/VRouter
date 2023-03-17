package com.vam.order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vam.annotation.ARouter

@ARouter(path = "/order/Order2Activity")
class Order2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order2)



    }
}