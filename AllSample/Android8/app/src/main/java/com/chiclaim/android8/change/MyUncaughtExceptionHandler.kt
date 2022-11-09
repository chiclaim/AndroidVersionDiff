package com.chiclaim.android8.change

/**
 *
 * Created by kumu@2dfire.com on 2022/11/9.
 */
class MyUncaughtExceptionHandler : Thread.UncaughtExceptionHandler {

    private var defaultExceptionHandler: Thread.UncaughtExceptionHandler? =
        Thread.getDefaultUncaughtExceptionHandler()


    override fun uncaughtException(t: Thread, e: Throwable) {
        e.printStackTrace()
        // 如果不调用一下方法，出现未捕获的异常，应用并不会退出，直至 ANR
        defaultExceptionHandler?.uncaughtException(t, e)
    }

}