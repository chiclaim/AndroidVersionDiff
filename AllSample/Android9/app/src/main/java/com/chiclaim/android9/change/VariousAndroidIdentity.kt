package com.chiclaim.android9.change

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager


/**
 * Android 各种标识符
 *
 * - IMEI (International Mobile Equipment Identity) 国际移动设备标识。
 *         即通常所说的手机“串号”，用于在移动电话网络中识别每一部独立的手机等移动通信设备，相当于移动电话的身份证。序列号共有 15 位数。
 *         一般每个 SIM 卡槽都会分配一个 IMEI
 *
 * - IMSI (International Mobile Subscriber Identity) 国际移动用户识别码。
 *         是用于区分蜂窝网络中不同用户的、在所有蜂窝网络中不重复的识别码。手机将 IMSI 存储于一个64比特的字段发送给网络。
 *         IMSI可以用来在归属位置寄存器或拜访位置寄存器中查询用户的信息。
 *         为了避免被监听者识别并追踪特定的用户，大部分情形下手机和网络之间的通信会使用随机产生的临时移动用户识别码代替IMSI
 *
 * - MEID (Mobile Equipment Identifier）移动设备标识符。
 *         移动设备识别码是一个全球唯一的识别 `CDMA2000` 移动台设备的号码。可以看作一个使用十六进制数字的IMEI
 *         在中国，支持 CDMA2000 的运营商，主要有中国电信。
 *
 * Created by kumu@2dfire.com on 2022/11/10.
 */
class VariousAndroidIdentity {

    companion object {
        @SuppressLint("MissingPermission")
        fun acquireDeviceId(context: Context): String? {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            if (tm != null) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//                    if (tm.hasCarrierPrivileges()) {
                        return tm.deviceId
//                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    return tm.imei
                } else {
                    // Deprecated
                    //Use (@link getImei} which returns IMEI for GSM or (@link getMeid} which returns MEID for CDMA.
                    return tm.deviceId
                }
            }
            return null
        }
    }


}