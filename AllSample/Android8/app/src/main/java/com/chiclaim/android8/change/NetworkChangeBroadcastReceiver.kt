package com.chiclaim.android8.change

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * 监听网络状态的广播
 *
 * Created by kumu@2dfire.com on 2022/11/9.
 */
class NetworkChangeBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        // You must check intent.action ...

        println("NetworkChangeBroadcastReceiver=============网络发生变化")
    }

}