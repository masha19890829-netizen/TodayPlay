# Google Play 内部测试清单

## 上传前

- 创建 Google Play Console 应用。
- 启用 Play App Signing。
- 准备正式签名或让 Play 管理签名。
- 上传 `app-release.aab`。
- 填写隐私政策 URL。
- 填写 Data Safety。
- 填写内容分级。
- 添加至少一组手机截图。
- 配置内部测试测试者邮箱列表。

## 商品配置

- 创建订阅商品：`todayplay.plus.monthly`。
- 创建一次性商品：
  - `todayplay.itinerary.premium.once`
  - `todayplay.citypack.global`
  - `todayplay.photo.positions`
- 确认商品处于可测试状态。
- 内部测试账号必须通过 Play 分发安装 App，不能直接安装本地 debug APK 测试真实购买。

## 内部测试重点

- 首次打开 App。
- 卡片流选择城市、关系、时间、预算、交通和偏好。
- 生成路线。
- 打开地图。
- 手动打卡并查看积分。
- 生成通关卡。
- 历史页回看路线状态。
- 商店页连接 Google Play 商品。
- 用户取消购买。
- 商品未配置时的提示。
- 购买返回后不会客户端直接发放权益。

## 不通过就不能推进封闭测试的问题

- AAB 上传失败。
- 购买流程崩溃。
- 请求未披露的敏感权限。
- 隐私政策与实际行为不一致。
- App 声称真实社交平台热度但没有授权来源。
- App 无法在小屏设备完成主流程。
