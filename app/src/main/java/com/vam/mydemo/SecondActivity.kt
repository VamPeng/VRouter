package com.vam.mydemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vam.annotation.ARouter
import com.vam.arouterapi.click
import com.vam.mydemo.databinding.ActivitySecondBinding

const val secondMainPath = "/app/SecondActivity"
@ARouter(path = secondMainPath)
class SecondActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySecondBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.secondTv.click {
//            startActivity(Intent(
//                this,
//                `MainActivity$$ARouter`.findTargetClass(mainPath)
//            ))
        }

    }
}