# AndroidVersionDiff



> https://developer.android.com/about/versions



随着 Android 对 `隐私安全` 的重视及对 `用户体验` 的持续优化，不同的 Android 版本会有许多 API 发生变化， 有些 API 的改动，如果开发者不去兼容，可能会导致程序闪退。新 Android 版本对 API 的改动是否生效分为两种情况：

- 只有将 targetSdkVersion 设置到对应的新版本编号，新的 API 才会生效，否则还是按照老版本的逻辑走。
- 只要用户的 Android 系统升级到了新版本，新 API 就会生效，不管 targetSdkVersion 设置为多少。



每个 Android 开发者都会因为没有及时去了解 Android 新版本的变更，自己的某一行代码导致程序的 bug 率上升。



更糟糕的是，有的新版本的变更，并没有在官方的文档提及。如果每次发版没有对所支持的不同版本的设备测试，就只能等线上报错才能发现问题。



所以，创建了本仓库来分享 Android 不同版本添加的新特性及 API 行为的调整。同时也会附上我对 Android 新版的兼容适配的实践。



希望更多的开发者能够共同来维护它，帮助 Android 开发者更加方便的开发出稳定的、体验优异的应用程序。





