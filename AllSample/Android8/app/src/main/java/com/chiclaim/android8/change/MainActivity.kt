package com.chiclaim.android8.change

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        registerNetworkChangeReceiver()
    }

    private fun registerNetworkChangeReceiver() {
        // 动态注册的方式虽然可以收到广播，但是 Android 已经有新的 API 来监听网络状态: ConnectivityManager.registerNetworkCallback
        val filter = IntentFilter()
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(NetworkChangeBroadcastReceiver(), filter)
    }

    fun sendNotification(view: View) {
        NotifierUtils.sendNotification(this)
    }

    fun occurUnCaughtException(view: View) {
        val stringNull: String? = null
        stringNull!!.length
    }


}