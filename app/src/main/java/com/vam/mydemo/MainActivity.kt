package com.vam.mydemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.vam.annotation.ARouter
import com.vam.annotation.model.RouterBean
import com.vam.apt.`ARouter$$Group$$order`
import com.vam.apt.`ARouter$$Group$$person`
import com.vam.arouterapi.RouterManager
import com.vam.mydemo.databinding.ActivityMainBinding

const val mainPath = "/app/MainActivity"

@ARouter(path = mainPath)
class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val vm = ViewModelProvider(this).get(MainViewModel::class.java)
        vm.mainId++
        Log.i("Vam", "mainId: " + vm.mainId)

        addClick("订单") {
            RouterManager.navi(this, "/order/OrderActivity")
        }

        addClick("用户") {
            val loadGroup = `ARouter$$Group$$person`()
            val groupMap = loadGroup.loadGroup()
            val clazz = groupMap["person"]
            clazz?.let { clz ->

                clz.newInstance()?.let { path ->
                    val pathMap = path.loadPath()

                    pathMap["/person/Person2Activity"]?.let { roterBean: RouterBean ->
                        val intent = Intent(this, roterBean.clazz)
                        startActivity(intent);
                    }

                }
            }
        }

        addClick("二") {
//            startActivity(
//                Intent(
//                    this,
//                    `SecondActivity$$ARouter`.findTargetClass(secondMainPath)
//                )
//            )
        }

    }

    private fun addClick(content: String, l: (View) -> Unit) {
        val btn = Button(this)
        btn.text = content
        btn.setOnClickListener(l)

        binding.mainParent.addView(btn)

    }


}