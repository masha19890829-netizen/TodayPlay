# Google Play Data Safety 草案

此文件用于填写 Google Play Console Data Safety 表单前的开发者底稿。最终提交内容必须根据真实上线版本复核。

## 当前版本数据行为

App 当前不需要账号体系，不上传照片，不请求定位，不接真实后端。

本地保存：

- 用户选择的关系类型。
- 城市/路线偏好。
- 时间、预算、交通方式。
- 生成过的路线副本。
- 任务状态、打卡状态、积分记录。

网络交互：

- Google Play Billing 商品查询和购买流程。
- 未来后端接口预留，但当前没有真实后端上传。

## 建议 Data Safety 填写

### Personal info

当前不收集姓名、邮箱、电话号码、地址等个人身份信息。

如果未来加入账号体系，需要更新为收集账号标识并提供删除入口。

### Financial info

通过 Google Play Billing 处理购买。客户端会收到购买回调和 purchaseToken，用于发送到后端验单。

正式版需要声明：

- 购买记录 / purchase history：用于 app functionality 和 fraud prevention。
- 不应在客户端日志中记录 purchaseToken。

### Location

当前不收集用户位置。

地图跳转只传递目的地坐标，不读取用户当前位置。

如果未来加入定位打卡，需要更新权限、隐私政策和 Data Safety。

### Photos and videos

当前不上传照片。

“照片上传 mock”不应变成真实上传，除非加入明确用户授权、隐私政策说明、删除机制和内容审核。

### App activity

本地保存路线历史、任务状态和积分状态。

如果未来接入分析或后端同步，需要声明 app interactions、in-app search/history 或 user-generated content 的实际收集和共享方式。

### Device or other IDs

当前没有主动使用广告 ID。

Google Play Billing 和 AndroidX 依赖可能使用必要的系统能力，最终以 merged manifest 和真实 SDK 行为为准。

## 安全声明建议

- 数据传输：未来所有后端接口必须使用 HTTPS。
- 数据删除：正式账号体系上线后提供删除路线历史和账号数据入口。
- 数据共享：除 Google Play Billing 和用户主动打开外部地图外，不与第三方共享用户路线偏好。

## 需要上线前确认

- 是否接入后端。
- 是否接入分析 SDK。
- 是否接入地图 SDK 或只打开外部地图。
- 是否上传照片。
- 是否创建账号。
- 是否做真实定位打卡。
