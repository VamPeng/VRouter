package com.vam.order

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vam.annotation.ARouter
import com.vam.arouterapi.click
import com.vam.order.databinding.ActivityCommonMainBinding

@ARouter(path = "/order/OrderActivity")
class OrderActivity : AppCompatActivity() {
    private val binding by lazy { ActivityCommonMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.orderJump.click {
            startActivity(Intent(this, Class.forName("com.vam.mydemo.MainActivity")))
        }

    }
}