package com.vam.person

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vam.annotation.ARouter

@ARouter(path = "/person/Person2Activity")
class Person2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person2)
    }
}