package com.vam.person

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vam.annotation.ARouter
import com.vam.arouterapi.click
import com.vam.person.databinding.ActivityPersonBinding

@ARouter(path = "/person/PersonActivity")
class PersonActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPersonBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.personJump.click {
            startActivity(Intent(this, Class.forName("com.vam.mydemo.MainActivity")))
        }

    }
}