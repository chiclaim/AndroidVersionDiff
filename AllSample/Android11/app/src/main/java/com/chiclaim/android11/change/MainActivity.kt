package com.chiclaim.android11.change

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun checkAppInstall(view: View) {
        val mm = "com.tencent.mm"
        var available = true
        try {
            // check if available
            packageManager.getPackageInfo(mm, PackageManager.GET_CONFIGURATIONS)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            // if not available set available as false
            available = false
        }
        Toast.makeText(this, "com.tencent.mm is available:${available}", Toast.LENGTH_SHORT).show()

        try {
            packageManager.getApplicationInfo(mm, PackageManager.GET_CONFIGURATIONS)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        if (available) {
            packageManager.getLaunchIntentForPackage(mm)?.let {
                startActivity(it)
            }
        }
    }

}