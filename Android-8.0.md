

## Android 8 避坑指南



> 本文的 Android 包含 Android 8.0 和 8.1 。另外，官方关于 [Android 8 Code Sample](https://developer.android.com/about/versions/oreo/android-8.0-samples) 有兴趣可以运行看下效果。



### 1. 后台服务限制

从 Android 8 开始，要避免随意的调用 `startService` 方法。否则会有很大概率出现：IllegalStateException。

根据 Android 官网描述，如果 App 在后台调用 `startService` 方法，就会抛出如下异常：

```
java.lang.IllegalStateException: Not allowed to start service Intent
```

既然在后台不能调用，那在前台能不能调用？经过测试在 `onWindowFocusChanged` 方法里调用也不行。



**解决方案**：

启动服务使用 `Context.startForegroundService()`，然后在 `onStartCommand` 方法中调用 `startForeground()`，需要注意 `Context.startForegroundService()` 和 `startForeground` 要配套调用。如果调用 ` Context.startForegroundService()` 后，没有在规定的时间调用 `startForeground`，就会抛出异常：

```java
android.app.ForegroundServiceDidNotStartInTimeException 
或
android.app.RemoteServiceException
	Context.startForegroundService() did not then call Service.startForeground(): ServiceRecord
```



注意，不能在`Context.startForegroundService()` 后调用 `stopService` 或 `stopSelf`，否则也会立马报上述异常。所以我们一定要注意调用 `stopService` 的时机（一般我们启动服务后， 会在任务执行完了 stopService）。使用是需要注意 2 点：

- ` Context.startForegroundService()` 要和 `startForeground()` 配套使用 
- 调用 ` Context.startForegroundService()` 后，不能调用 `stopService` 或 `stopSelf`



经过观察线上日志，还是会有小概率出现 `RemoteServiceException` 或 `ForegroundServiceDidNotStartInTimeException` 异常。估计还是在 ` Context.startForegroundService()` 调用了 `stopService`。（后续如果找到具体原因，会及时更新本文档）



如果不是长时间的后台任务 ，可以考虑直接使用线程；如果是长时间的任务，也可以考虑使用 `WorkManager`，需要考虑 `WorkManager` 的及时性和各个厂商对`WorkManager` 的修改。



> 本限制会对所有 Android 8 系统生效，不管应用的 `targetSdkVersion` 是多少。



### 2. 透明主题的 Activity 不能设置朝向

如果你将 Activity 的主题设置为透明样式：

```xml
<activity android:name=".TransparentActivity"
          android:screenOrientation="portrait"
          android:theme="@style/TransparentDialogStyle"/>
```

那么你将喜提如下异常：

```java
java.lang.IllegalStateException: Only fullscreen opaque activities can request orientation
```

经过不同的版本及不同的 `targetSdkVersion`  测试，上述问题只会在 `Android 8 (API Level 26)` 机器中出现：

- 当程序的 `targetSdkVersion<=26` 则不会抛出异常。
- 当程序的 `targetSdkVersion>26` 则抛出异常。



最坑的是在 [Android 8.0 的变更](https://developer.android.com/about/versions/oreo) 中没有找到有关描述（悄无声息改的）。我们绝大部分的应用都会兼容 Android 8.0，但是开发测试的机器往往是比较新的，导致上线才知道问题所在。



### 3. Notification 通知栏变化

从 Android 8.0 （API level 26）开始，每个 Notification 必须分配一个 channel。

```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
	val channel = NotificationChannel(
		CHANNEL_ID,
		context.getString(R.string.downloader_notifier_channel_name),
		NotificationManager.IMPORTANCE_LOW
	)
	notificationManager.createNotificationChannel(channel)
}

val builder = NotificationCompat.Builder(context, CHANNEL_ID)
	.setSmallIcon(notifierSmallIcon)
	.setContentTitle(title)
	.setAutoCancel(true/false)
	.setOngoing(percent != 100)
}
notificationManager.notify(id, builder.build())
```



> 通知栏的变化是在 App 的 `targetSdkVersion` >=26 才会生效。更多关于通知栏的细节可以查看：  [Android 8 notification](https://developer.android.com/develop/ui/views/notifications/channels)





### 4. Broadcasts 广播的调整



#### Broadcasts 的一些简单科普



关于隐式广播和显示广播：

- 隐式广播（implicit broadcast）：可以简单的理解为当事件发生时，如果你和其他应用程序都注册了广播，那么你们都会接收到该信息。（一对多关系）
- 显式广播（explicit broadcasts）：当事件发生时，只有你的应用程序会接收到该消息。（一对一关系）

<p>

广播的接收有两种方式 [receiving-broadcasts](https://developer.android.com/guide/components/broadcasts#receiving-broadcasts)：

- 在 manifest 中注册（manifest-declared receivers），民间也有称之为的静态广播，但是在 Android 官网没有类似的叫法。
- 程序运行时注册（context-registered receivers），民间也有称之为的动态广播，但是在 Android 官网没有类似的叫法。

<p>

`manifest-declared receivers` 和 `context-registered receivers` 区别是什么？

- 在 manifest 中注册的广播接收者，当事件发生时，系统会启动你的应用程序，哪怕你的应用并没有运行。
- 在运行时通过 context 对象注册的广播接收者，当事件发生时，需要 context 是有效的。当 context 是 Activity，那么 Activity 没有被销毁才能接收到广播。当 context 是 Application 时，那么只有在程序仍处于运行状态才能接收到广播。



<p></p>

####  Broadcasts 在 Android 8 中的变化：



每当广播被发送，应用的广播接收者都会消耗资源。如果很多应用都去注册接收系统事件的广播，那么可能会导致这些 App 接连消耗系统资源。例如 `ACTION_PACKAGE_REPLACED` 是一个隐式广播，当事件发生时，所有注册的应用都会接收到事件。`ACTION_MY_PACKAGE_REPLACED` 就是一个显示广播，虽然 App 可能都会去注册，但是当该事件发生时，只有被升级的 APP 才会接收到。



所以，为了提高用户体验，避免广播对系统资源消耗过大的问题，Android 8 对广播做的调整，可以总结如下几条：

- 不再支持在 manifest 清单文件注册隐式广播（implicit broadcast），但仍然支持在 manifest 清单文件注册显式广播（explicit broadcasts）
- 可以在运行时注册广播（同时支持隐式和显示广播）
- 如果广播要求签名权限（signature permission），那么也可以在 manifest 中注册隐式广播。因为当事件发生时，只有和该广播发送者拥有相同的签名的应用才会收到事件通知，而不是所有注册的应用。



测试案例：例如你想监听设备的网络的变化，该广播是隐式广播，如果在 manifest 中注册，则收不到网络变化的事件，运行时注册可以接收的到。测试代码可以查看 [AllSample/Android8](https://github.com/chiclaim/AndroidVersionDiff/tree/main/AllSample/Android8)



> 本限制会对所有 Android 8 系统生效，不管应用的 `targetSdkVersion` 是多少。





### 5. ANDROID_ID 的变化

Android 8 在隐私方面也做了一些变动：

- 如果你的应用所在的系统版本是 Android 8 之前，然后用户将系统升级到 Android 8（API level 26），ANDROID_ID 的值不会发生改变，除非用户后面卸载重装了你的应用。
- 如果你的应用是在 Android 8 上安装的，那么 ANDROID_ID 的值，是 `应用的签名(app siging key)`、`用户（user）`和`设备（device）`三个信息的组合，只要其中一个信息改变了， ANDROID_ID 的值都会发生变化。设备（device）恢复出厂设置， ANDROID_ID 的值也会改变。
- 如果你的应用是在 Android 8 上安装的，那么 ANDROID_ID 的值不会因为卸载重装而发生改变。
- 如果是因为系统升级导致应用的签名 key 发生变化，ANDROID_ID 的值不会改变。



> 本限制会对所有 Android 8 系统生效，不管应用的 `targetSdkVersion` 是多少。



### 6. 系统属性 net.hostname 变更

从 Android 8.0 (API level 26) 开始，无法查询系统属性 `net.hostname`

```kotlin
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
```



经测试：上述代码在  android 12 (xiaomi 11) 返回 `unknown`；在 android 6.0.1 (read mi 4A) 返回 `Redmi4A-hongmishouji`



> 本限制会对所有 Android 8 系统生效，不管应用的 `targetSdkVersion` 是多少。



### 7. Build.SERIAL 变更

从 Android 8.0 (API level 26) 开始 Build.SERIAL 已经被废弃：

```kotlin
fun showBuildSerial(){
    println(Build.SERIAL)
}
```

经测试：上述代码在  android 12 (xiaomi 11) 返回 `unknown`；在 android 6.0.1 (read mi 4A) 返回 `298bd1947d04`

可以使用 `Build.getSerial()` 来获取硬件序列号，该方法需要 `READ_PHONE_STATE` 权限【Build.getSerial 在 Android 10 中由变更】。

> 本变更在 App 的 `targetSdkVersion` >=26 才会生效。



### 8. Thread.UncaughtExceptionHandler

如果应用自定义了 `Thread.UncaughtExceptionHandler`，但是没有调用默认的  `defaultExceptionHandler.uncaughtException`，一旦出现未捕获的异常，应用并不会立马退出， 直到应用弹出 ANR。（[点击查看测试代码](https://github.com/chiclaim/AndroidVersionDiff/tree/main/AllSample/Android8)）

在 Android 8 之前，不调用默认的 `defaultExceptionHandler.uncaughtException`，系统不会记录堆栈信息。从 Android 8 开始，系统则会记录异常的堆栈信息。



> 本限制会对所有 Android 8 系统生效，不管应用的 `targetSdkVersion` 是多少。



### 9. Permission 行为调整

在 Android 8.0 (API level 26) 之前，应用运行时请求的权限被同意时，那么系统也会将属于该分组下的其他权限一同赋予。例如用于请求了 `READ_EXTERNAL_STORAGE` 那么系统也会赋予 `WRITE_EXTERNAL_STORAGE` 权限。



如果程序的 targetSdkVersion 为 Android 8，那么系统仅仅会赋予你请求的权限。同意分组的权限也需要你显式的请求，只不过系统会自动通过，不会给用户提示。例如用户请求 `READ_EXTERNAL_STORAGE` 被赋予，当请求  `WRITE_EXTERNAL_STORAGE` 权限时，系统会自动同意。

> 本变更在 App 的 `targetSdkVersion` >=26 才会生效。









