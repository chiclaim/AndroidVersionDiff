> Android 9 （API Level 28）



## 1. Foreground services 变更

在前面介绍 Android 8 变更的时候提到，如果在后台 `startService`，系统会抛出异常。需要我们启动前台服务。从 Android 9 开始，使用前台服务需要声明 `FOREGROUND_SERVICE`，否则会抛出 `SecurityException`。



> 本变更是在 App 的 `targetSdkVersion` >=28 才会生效



## 2. FLAG_ACTIVITY_NEW_TASK 变更

从 Android 9 开始，当你使用非 activity context 去启动 activity，必须加上 `FLAG_ACTIVITY_NEW_TASK`



根据官网的描述本变更，会影响所有 Android 9 及以上系统，不管 `targetSdkVersion` 是多少。但是经过实测：



- Android 6（小米 红米 4A）会闪退

- Android 7（小米 红米 Note 4X）不会闪退

- Android 8 （三星 Galaxy S9+）不会闪退

- Android 9（小米 MI 8 SE）当 targetSdkVersion >= 28 会闪退，否则不会闪退

- Android 12（小米 11）当 targetSdkVersion >= 28 会闪退，否则不会闪退



> 经测试：当系统版本小于 Android 9 ，则不会闪退；Android 9 及以上会根据 `targetSdkVersion` 是否大于等于 28。



## 3. 默认开启网络 TLS

从 Android 9 开始，默认不在支持 HTTP 请求。也就是 `cleartextTrafficPermitted` 默认为 false（cleartext 明文的意思，明文传输不被允许）。如果你需要请求 http 资源，你需要显式的将其设置为 true，例如：

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <debug-overrides>
        <trust-anchors>
            <!-- Trust user added CAs while debuggable only -->
            <certificates src="system" />
            <certificates src="user" />
        </trust-anchors>
    </debug-overrides>
    <base-config cleartextTrafficPermitted="true"> <!-- 支持明文传输 -->
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
</network-security-config>
```

例如， WebView.load("http://www.baidu.com")，会直接提示 `net::ERR_CLEARTEXT_TRAFFIC_PERMITTED`



> 经测试：本变更是在 App 的 `targetSdkVersion` >=28 才会生效



## 4. Apache HTTP client 库被废弃

在 Android 6.0 里就移除了对 `Apache HTTP client` 的支持，如果在 Android 2.3（API Level 9）或者更高的系统版本上使用该库，系统会使用 HttpURLConnection 代替。如果不想被 HttpURLConnection 替换，需要进行如下配置：

```gradle
android {
    useLibrary 'org.apache.http.legacy'
}
```



从 Android 9.0 开始，`Apache HTTP client` 已经从 bootclasspath 中移除，意味代码无法引用到该库了。如果你一定要使用该库，需要 targetSdkVersion>=28，同时在清单文件配置：

```xml
<uses-library android:name="org.apache.http.legacy" android:required="false"/>
```



但是经过测试，加上这些配置后，依然无法引用到 `Apache HTTP client` 里的 API，看来已经彻底移除了。那如果要使用的话，只能像第三方库一样，手动添加依赖了。



> 官方已经不推荐使用 Apache HTTP client 了，因为它的性能比较低。官方已经将 HttpUrlConnection 的底层实现使用了 OkHttp，所以建议直接使用 OkHttp。



## 5. 限制非 SDK 接口的使用

非 SDK 接口（non-SDK interfaces）：简单来说就是系统没有暴露的类、方法、字段。

你可能会使用反射或 JNI 调用了非 SDK 接口 ，那么你的程序在将来可能会有稳定性和兼容性的问题。

在 Android 9 ，虽然允许你使用 非 SDK 接口，但是系统可能会通过 toast 和 log 的方式给你提示。如果你收到这样的提示，应该寻找其他的解决方案来代替。



