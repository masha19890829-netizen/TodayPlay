# 用户需准备的账号登录资料清单

当前账号功能版本：`0.9.41 / versionCode 59`

## 当前 APK 能力

- 首页和分享页已有账号入口。
- 可用 `Use local tester profile` 先体验分享署名。
- Google 登录按钮已接入 Credential Manager 网关，但默认关闭。
- 未配置 `GOOGLE_WEB_CLIENT_ID` 时，Google 按钮不会发起登录，避免试用包崩溃。
- Google ID token 只保存在内存中，等待未来后端验证；当前不会持久化账号或解锁付费权益。

## 你后续需要准备

| 资料 | 用途 | 是否敏感 |
| --- | --- | --- |
| Google Cloud / Google Auth Platform 项目 | 创建 OAuth client | 不是敏感密钥 |
| Android OAuth client | 绑定包名 `com.todayplay.app` 和测试/发布证书指纹 | 不是敏感密钥 |
| Web OAuth client ID | 填入 `GOOGLE_WEB_CLIENT_ID`，供 Android Credential Manager 使用 | 不是密钥，可给我配置 |
| 测试 APK 证书 SHA-1 / SHA-256 | 让 Google 登录识别当前测试包 | 可提供指纹 |
| 发布证书 SHA-1 / SHA-256 | 正式包 Google 登录配置 | 可提供指纹 |
| 账号验 token 后端 URL | 填入 `AUTH_VERIFY_ENDPOINT`，后端验证 Google ID token | URL 可提供 |
| 后端会话/用户表方案 | 保存真实账号、跨设备历史和权益 | 不要给我数据库密码 |
| 账号/数据删除入口 | 启用真实账号后用于隐私合规 | 公开 URL 或邮箱可提供 |

## 不要发给我的资料

- Google 账号密码
- OAuth client secret
- 服务账号私钥
- 后端数据库密码
- Play Console 一次性验证码
- 任何身份证件、银行卡、税务文件

## 我拿到非敏感配置后会做什么

1. 把 `GOOGLE_WEB_CLIENT_ID` 和 `AUTH_VERIFY_ENDPOINT` 写入本地未提交的 `release_config.properties`。
2. 重新构建 APK。
3. 用账号门禁检查 Google 登录入口、分享署名、无明文密钥和后端验证边界。
4. 如果你接入真机，我会安装 APK 跑 Google 登录、分享、退出、重启后的状态检查。
