# Kimi API Key 提供方式

不要把 Kimi API Key 发到聊天里，也不要写入 Android 客户端配置。

## 推荐方式

在本机创建这个文件：

```text
backend/ai-route-gateway/.dev.vars
```

内容参考：

```text
AI_PROVIDER=kimi
KIMI_API_KEY=你的 Kimi API Key
KIMI_BASE_URL=https://api.moonshot.cn/v1
KIMI_MODEL=moonshot-v1-8k
AI_GATEWAY_SHARED_SECRET=先随便填一个本地测试密钥
```

`.dev.vars` 已加入 `.gitignore`，不会上传 GitHub。

## 为什么不能放到 Android 里

Android APK 可以被反编译。如果把 Key 写进：

- `BuildConfig`
- `release_config.properties`
- Kotlin 源码
- assets/raw 资源

都可能泄漏。

正确做法是：

```text
Android App -> TodayPlay AI Route Gateway -> Kimi API
```

Kimi Key 只放在后端环境变量里。

## 当前官方接口参考

Kimi Chat Completions 示例接口：

```text
https://api.moonshot.cn/v1/chat/completions
```

鉴权方式：

```text
Authorization: Bearer <KIMI_API_KEY>
```

后续实现时，V0.9.59 会优先把这个 Key 接到后端 AI Route Gateway，而不是直接接到 APK。
