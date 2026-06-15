# Google Play 审核说明草案

## App 当前状态

这是「今天怎么玩 / Today's Private Quest」的 Google Play 内部测试候选版本。

当前版本提供：

- 全球城市路线副本样例。
- 关系/偏好卡片流。
- 路线结果页。
- 外部地图跳转。
- 本地手动打卡积分。
- 历史路线和通关卡。
- Google Play Billing 客户端接口。

## 权限说明

App 主动声明：

- `android.permission.INTERNET`

依赖合并权限：

- `com.android.vending.BILLING`
- `android.permission.ACCESS_NETWORK_STATE`
- AndroidX 动态 receiver 保护权限。

App 不请求：

- 定位权限。
- 相册或媒体读取权限。
- 摄像头权限。
- 麦克风权限。
- 通讯录权限。
- 人脸识别或生物识别权限。

## 地图说明

地图按钮只打开外部地图应用或网页地图，并传递目的地名称和经纬度。App 不读取用户当前位置，不做强制到店校验，也不上传行程轨迹。

## 付费说明

App 使用 Google Play Billing 查询和启动商品购买流程。商品 ID：

- `todayplay.plus.monthly`
- `todayplay.itinerary.premium.once`
- `todayplay.citypack.global`
- `todayplay.photo.positions`

当前客户端已提取 `purchaseToken`，但正式发放权益必须依赖后端 `/billing/verify` 调用 Google Play Developer API 验单。未配置 Play Console 商品和后端验单前，不应认为付费权益已上线。

## 内容来源说明

当前路线中的地点、社交灵感、拥挤风险和推荐理由为 mock/精选样例，用于验证产品结构。App 不抓取第三方平台内容，不展示未经授权图片，也不声称使用真实社交平台热度。
