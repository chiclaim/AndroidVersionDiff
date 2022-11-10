



## 1. Foreground services 变更

在前面介绍 Android 8 变更的时候提到，如果在后台 `startService`，系统会抛出异常。需要我们启动前台服务。从 Android 9 开始，使用前台服务需要声明 `FOREGROUND_SERVICE`，否则会抛出 `SecurityException`。



> 本变更是在 App 的 `targetSdkVersion` >=28 才会生效



### 2. FLAG_ACTIVITY_NEW_TASK 变更

从 Android 9 开始，当你使用非 activity context 去启动 activity，必须加上 `FLAG_ACTIVITY_NEW_TASK`



根据官网的描述本变更，会影响所有 Android 9 及以上系统，不管 `targetSdkVersion` 是多少。但是经过实测：



- Android 6（小米 红米 4A）会闪退

- Android 7（小米 红米 Note 4X）不会闪退

- Android 8 （三星 Galaxy S9+）不会闪退

- Android 9（小米 MI 8 SE）当 targetSdkVersion >= 28 会闪退，否则不会闪退

- Android 12（小米 11）当 targetSdkVersion >= 28 会闪退，否则不会闪退



> 经测试：当系统版本小于 Android 9 ，则不会闪退；Android 9 及以上会根据 `targetSdkVersion` 是否大于等于 28。





