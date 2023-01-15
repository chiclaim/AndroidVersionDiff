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



> 本限制会对 `targetSdkVersion>=30 的应用生效。









