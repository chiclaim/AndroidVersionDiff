package com.chiclaim.android9.change

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity

/**
 *
 * Created by kumu@2dfire.com on 2022/11/11.
 */
class WebViewActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_layout)
        findViewById<WebView>(R.id.web_view).loadUrl("http://www.baidu.com")
    }
}