package com.chiclaim.android8.change

import android.app.Application

/**
 *
 * Created by kumu@2dfire.com on 2022/11/9.
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler(MyUncaughtExceptionHandler())
    }
}