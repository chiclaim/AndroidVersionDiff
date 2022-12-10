> Android 10 （API Level 29）



## 1. PackageManager.getPackageInfo

从 Android 10 （API Level 29）开始，只能获取当前应用的 packageInfo，无法获取其他应用的 packageInfo。例如通过 PackageManager.getPackageInfo 来判断程序是否安装在 Android 10 及以上是失效的：

```java
var available = true
try {
	// check if available
	packageManager.getPackageInfo("zmsoft.rest.phone", GET_CONFIGURATIONS)
} catch (e: PackageManager.NameNotFoundException) {
	e.printStackTrace()
	// if not available set available as false
	available = false
}
println(available)
```



> 本限制会对 `targetSdkVersion>=29` 的应用生效。









