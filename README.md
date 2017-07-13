# DiyCodeAndroid
a Third-party Android App of [DiyCode Community](http://www.diycode.cc/)
## 应用截图
<p>
 <img src="/screenshots/1.jpg" width="180"/>
 <img src="/screenshots/2.jpg" width="180"/>
 <img src="/screenshots/3.jpg" width="180"/>
 <img src="/screenshots/4.jpg" width="180"/>
</p>

## 下载
[点击下载](https://www.pgyer.com/DiyCodeAndroid)
## 感谢
### Api
[DiyCode API 目录](https://www.diycode.cc/api)
### 框架
[JessYanCoding/MVPArms](https://github.com/JessYanCoding/MVPArms)
### 代码参考
[GcsSloop/diycode](https://github.com/GcsSloop/diycode)

[plusend/DiyCode](https://github.com/plusend/DiyCode)

[SamuelGjk/DiyCode](https://github.com/SamuelGjk/DiyCode)

[xshengcn/DiyCode](https://github.com/xshengcn/DiyCode)
## 注意
代码中使用了签名验证，所以使用前请将 [`native-security.cpp`](https://github.com/linsneider/DiyCodeAndroid/blob/master/app/src/main/cpp/native-security.cpp) 里的`static const char *SIGN`改为自己打包用的签名，或是直接把`jint JNI_OnLoad(JavaVM *vm, void *reserved)`这个方法注释掉即可。
### 推荐阅读
[Android 密钥保护和 C/S 网络传输安全理论指南](https://drakeet.me/android-security-guide/)

[Android安全系列之：如何在native层保存关键信息](http://www.jianshu.com/p/2576d064baf1)
## 联系我
linsneijder@gmail.com
