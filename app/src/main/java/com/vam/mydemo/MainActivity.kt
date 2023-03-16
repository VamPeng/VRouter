package com.vam.mydemo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.vam.annotation.ARouter
import com.vam.annotation.model.RouterBean
import com.vam.apt.`ARouter$$Group$$order`
import com.vam.apt.`ARouter$$Group$$person`
import com.vam.mydemo.databinding.ActivityMainBinding

const val mainPath = "/app/MainActivity"

@ARouter(path = mainPath)
class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        addClick("订单") {
            val loadGroup = `ARouter$$Group$$order`()
            val groupMap = loadGroup.loadGroup()
            val clazz = groupMap["order"]
            clazz?.let { clz ->

                clz.newInstance()?.let { path ->
                    val pathMap = path.loadPath()

                    pathMap["/order/Order2Activity"]?.let { roterBean: RouterBean ->
                        val intent = Intent(this, roterBean.clazz)
                        startActivity(intent);
                    }

                }
            }
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