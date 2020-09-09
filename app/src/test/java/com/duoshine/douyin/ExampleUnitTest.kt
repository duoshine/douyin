package com.duoshine.douyin

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    data class Demo(var name: String, var ageD: ArrayList<Demo1>) : Cloneable {
        public override fun clone(): Demo {
            val demo = super.clone() as Demo
            demo.ageD = this.ageD.clone() as ArrayList<Demo1>
            return demo
        }
    }

    data class Demo1(var age: Int):Cloneable{
        public  override fun clone(): Any {
            return super.clone()
        }
    }

    @Test
    fun addition_isCorrect() {
        val demo1 = Demo1(25)
        val list = ArrayList<Demo1>()
        list.add(demo1)
        val d = Demo("duo_shine", list)
        println(d)
        val clone = d.clone()
        clone.name = "duoshine"
        clone.ageD[0].age = 2999
        println(d)
        println(clone)
    }


}