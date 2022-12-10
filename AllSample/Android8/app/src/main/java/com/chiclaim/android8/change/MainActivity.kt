package com.chiclaim.android8.change

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.lang.reflect.Method


class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_READ_PHONE_STATE = 110
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        registerNetworkChangeReceiver()
        sendStickyOrderedBroadcast()
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

    // android 12(xiaomi 11) 返回 unknown
    // android 6.0.1 (read mi 4A) 返回 Redmi4A-hongmishouji
    fun showNetHostName(view: View) {
        val value: String? = try {
            val getString: Method =
                Build::class.java.getDeclaredMethod("getString", String::class.java)
            getString.isAccessible = true
            getString.invoke(null, "net.hostname").toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        println(value)
        Toast.makeText(this, "net.hostname=$value", Toast.LENGTH_SHORT).show()
    }

    fun showSerial(view: View) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                REQUEST_CODE_READ_PHONE_STATE
            )
        } else {
            showSerial()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            showSerial()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showSerial() {
        val result =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) Build.SERIAL else Build.getSerial()
        println(result)
        Toast.makeText(this, "imei:$result", Toast.LENGTH_SHORT).show()
    }

    fun showTransparentActivity(view: View) {
        startActivity(Intent(this, TransparentActivity::class.java))
    }


}