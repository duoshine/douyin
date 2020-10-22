package com.duoshine.douyin.data

import com.duoshine.douyin.model.CityModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okio.ByteString

class CityService {

    private var count = 0

    private val videoUrl = listOf(
        "http://jzvd.nathen.cn/6ea7357bc3fa4658b29b7933ba575008/fbbba953374248eb913cb1408dc61d85-5287d2089db37e62345123a1be272f8b.mp4",
        "http://jzvd.nathen.cn/35b3dc97fbc240219961bd1fccc6400b/8d9b76ab5a584bce84a8afce012b72d3-5287d2089db37e62345123a1be272f8b.mp4",
        "http://jzvd.nathen.cn/df6096e7878541cbbea3f7298683fbed/ef76450342914427beafe9368a4e0397-5287d2089db37e62345123a1be272f8b.mp4",
        "http://jzvd.nathen.cn/384d341e000145fb82295bdc54ecef88/103eab5afca34baebc970378dd484942-5287d2089db37e62345123a1be272f8b.mp4",
        "http://jzvd.nathen.cn/f55530ba8a59403da0621cbf4faef15e/adae4f2e3ecf4ea780beb057e7bce84c-5287d2089db37e62345123a1be272f8b.mp4",
        "http://jzvd.nathen.cn/6340efd1962946ad80eeffd19b3be89c/65b499c0f16e4dd8900497e51ffa0949-5287d2089db37e62345123a1be272f8b.mp4",
        "http://jzvd.nathen.cn/f07fa9fddd1e45a6ae1570c7fe7967c1/c6db82685b894e25b523b1cb28d79f2e-5287d2089db37e62345123a1be272f8b.mp4",
        "http://jzvd.nathen.cn/d2e969f2ec734520b46ab0965d2b68bd/f124edfef6c24be8b1a7b7f996ccc5e0-5287d2089db37e62345123a1be272f8b.mp4",
        "http://jzvd.nathen.cn/4f965ad507ef4194a60a943a34cfe147/32af151ea132471f92c9ced2cff785ea-5287d2089db37e62345123a1be272f8b.mp4",
        "http://jzvd.nathen.cn/342a5f7ef6124a4a8faf00e738b8bee4/cf6d9db0bd4d41f59d09ea0a81e918fd-5287d2089db37e62345123a1be272f8b.mp4"
    )

    private val placeholderUrl = listOf(
        "http://jzvd-pic.nathen.cn/jzvd-pic/bd7ffc84-8407-4037-a078-7d922ce0fb0f.jpg",
        "http://jzvd-pic.nathen.cn/jzvd-pic/f2dbd12e-b1cb-4daf-aff1-8c6be2f64d1a.jpg",
        "http://jzvd-pic.nathen.cn/jzvd-pic/ccd86ca1-66c7-4331-9450-a3b7f765424a.png",
        "http://jzvd-pic.nathen.cn/jzvd-pic/2adde364-9be1-4864-b4b9-0b0bcc81ef2e.jpg",
        "http://jzvd-pic.nathen.cn/jzvd-pic/2a877211-4b68-4e3a-87be-6d2730faef27.png",
        "http://jzvd-pic.nathen.cn/jzvd-pic/aaeb5da9-ac50-4712-a28d-863fe40f1fc6.png",
        "http://jzvd-pic.nathen.cn/jzvd-pic/e565f9cc-eedc-45f0-99f8-5b0fa3aed567%281%29.jpg",
        "http://jzvd-pic.nathen.cn/jzvd-pic/3430ec64-e6a7-4d8e-b044-9d408e075b7c.jpg",
        "http://jzvd-pic.nathen.cn/jzvd-pic/2204a578-609b-440e-8af7-a0ee17ff3aee.jpg",
        "http://jzvd-pic.nathen.cn/jzvd-pic/1bb2ebbe-140d-4e2e-abd2-9e7e564f71ac.png"
    )

    suspend fun getData(): MutableList<CityModel> {

        return withContext(Dispatchers.IO) {
            delay(1500)
            val data = ArrayList<CityModel>()
            for (index in videoUrl.indices) {
                data.add(CityModel(videoUrl[index], placeholderUrl[index], 818, count))
                count++
            }
            data
        }
    }
}
