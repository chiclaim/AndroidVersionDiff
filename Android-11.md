> Android 11 （API Level 30）



## 1. 包可见性（Package visibility）

从 Android 11 （API Level 30）开始，只能获取当前应用的 packageInfo，默认无法获取其他应用的 packageInfo。影响的 API：

- PackageManager.getPackageInfo
- PackageManager.queryIntentActivities()
- PackageManager.getInstalledApplications()
- PackageManager.getApplicationInfo
- PackageManager.getLaunchIntentForPackage
- ...

例如通过 PackageManager.getPackageInfo 来判断程序是否安装在 Android 11 及以上是失效的：

```java
var available = true
try {
	// check if available
	packageManager.getPackageInfo("com.tencent.mm", GET_CONFIGURATIONS)
} catch (e: PackageManager.NameNotFoundException) {
	e.printStackTrace()
	// if not available set available as false
	available = false
}
println(available)
```



如果需要查询第三方 App 的信息，从 Android 11 开始需要在清单文件中，添加需要查询的包名：

```
<queries>
	<package android:name="com.tencent.mm"/>
</queries>
```



> 本限制会对 `targetSdkVersion>=30 的应用生效。



## 2. 权限变更



### 2.1 一次性权限（One-time permissions）

从 Android 11 开始，当请求位置、麦克风或相机权限时，系统弹出的 Dialog 会多一个选项：本次运行允许（Only this time）



### 2.2 自动重置权限

从 Android 11 开始，如果你的 APP 几个月没用被使用，那么系统会将一些敏感的权限自动设置为 “拒绝”







