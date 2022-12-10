package com.chiclaim.android9.change

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import org.apache.http.params.HttpParams


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun loadHttpUrl(view: View) {
        startActivity(Intent(this, WebViewActivity::class.java))
    }

}