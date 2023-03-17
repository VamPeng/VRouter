package com.vam.order

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import com.vam.annotation.ARouter
import com.vam.arouterapi.RouterManager
import com.vam.arouterapi.click
import com.vam.order.databinding.ActivityCommonMainBinding

@ARouter(path = "/order/OrderActivity")
class OrderActivity : AppCompatActivity() {
    private val binding by lazy { ActivityCommonMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Looper.myLooper()

        binding.orderJump.click {
            RouterManager.navi(this,"/app/MainActivity")
//            startActivity(Intent(this, Class.forName("com.vam.mydemo.MainActivity")))
        }

    }
}