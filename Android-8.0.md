

## Android 8 避坑指南



> 官方关于 [Android 8 Code Sample](https://developer.android.com/about/versions/oreo/android-8.0-samples) 有兴趣可以运行看下效果。



### 1. 后台服务限制

从 Android 8 开始，要避免随意的调用 `startService` 方法。否则会有很大概率出现：IllegalStateException。

根据 Android 官网描述，如果 App 在后台调用 `startService` 方法，既然在后台不能调用，那在前台能不能调用？经过测试在 `onWindowFocusChanged` 方法里调用也不行。



**解决方案**：

启动服务使用 `Context.startForegroundService()`，然后在 `onStartCommand` 方法中调用 `startForeground()`，需要注意 `Context.startForegroundService()` 和 `startForeground` 要配套调用。如果调用 ` Context.startForegroundService()` 后，没有在规定的时间调用 `startForeground`，就会抛出异常：

```java
android.app.ForegroundServiceDidNotStartInTimeException 
或
android.app.RemoteServiceException
	Context.startForegroundService() did not then call Service.startForeground(): ServiceRecord
```



需要注意的是，不能在`Context.startForegroundService()` 后调用 `stopService` 或 `stopSelf`，否则也会立马报上述异常。所以我们一定要注意调用 `stopService` 的时机。使用是需要注意 2 点：

- ` Context.startForegroundService()` 要和 `startForeground()` 配套使用 
- 调用 ` Context.startForegroundService()` 后，不能调用 `stopService` 或 `stopSelf`



经过观察线上日志，还是会有小概率出现 `RemoteServiceException` 或 `ForegroundServiceDidNotStartInTimeException` 异常。估计还是在 ` Context.startForegroundService()` 调用了 `stopService`。（后续如果找到具体原因，会及时更新本文档）



如果不是长时间的后台任务 ，可以考虑直接使用线程；如果是长时间的任务，也可以考虑使用 `WorkManager`，需要考虑 `WorkManager` 的及时性和各个厂商对`WorkManager` 的修改。



> 本限制会对所有 Android 8 系统生效，不管应用的 `targetSdkVersion` 是多少。



### 2. Notification 通知栏变化

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





### 3. Broadcasts 广播的调整





























