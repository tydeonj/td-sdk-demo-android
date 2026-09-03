# TD SDK Demo（Android）

对接文档（网页）：https://tydeonj.github.io/td-sdk-docs/  
文档仓库：https://github.com/tydeonj/td-sdk-docs  
iOS Demo：https://github.com/tydeonj/td-sdk-demo-ios  

三步速查：[接入指南.md](接入指南.md)

本 Demo **开 R8**，TD 用 Maven Central 坐标 `com.tyedo:*:1.1.2.8`。不要 `project()` 源码，也不要本地 `m2repo`。

```bash
./gradlew :app:assembleDebug --refresh-dependencies
```

广告位只改 [`app/src/main/java/com/td/sdkdemo/AdConfig.java`](app/src/main/java/com/td/sdkdemo/AdConfig.java)。正式包请用你们后台自己的 App ID / 广告位 ID。

点「初始化 SDK」时 Demo 按默认同意调用隐私 API。正式 App 须先弹自己的隐私政策，见 https://tydeonj.github.io/td-sdk-docs/隐私合规
