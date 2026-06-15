# TodayPlay V0.9.55 Minimal Anime UI Direction - 2026-06-12

## Direction

用户反馈上一版过于写实，不够高级。本轮视觉方向从“真实照片式生活推荐”调整为“简约日系动画感”。这里不直接复刻某位在世动画导演的具体画风，而是转译为更通用、可持续的设计原则：

- 低饱和自然色：米白、雾绿、浅天空色、淡黄昏、少量柿子色点缀。
- 手绘场景感：小人物、天空、山丘、咖啡桌、票券、路线线条。
- 轻叙事，不堆细节：画面传递“今晚可以出去走走”，不做复杂照片级环境。
- 文案留白：标题不被装饰线条压住，卡片先给氛围，再给路线信息。
- 项目内可控资产：使用矢量插画，减少大图体积和第三方素材风险。

## What Changed

- 新增四张项目本地矢量插画：
  - `app/src/main/res/drawable/anime_date_invitation.xml`
  - `app/src/main/res/drawable/anime_evening_walk.xml`
  - `app/src/main/res/drawable/anime_cafe_friends.xml`
  - `app/src/main/res/drawable/anime_ticket_sky.xml`
- 首页 Hero、路线卡、结果页 Hero、加载页、商店页、完成卡片和轮播图引用切到插画资源。
- 全局主题色从玫瑰/咖啡/票券重色，调整为更轻的 cream / sage / sky / terracotta / ink。
- 首页 Hero 动线移到上半区并降低透明度，避免压住标题和副标题。
- Release APK 从约 9.6 MB 降到约 2.3 MB，说明写实大图不再进入主 release 包。

## Style Guard

`playstore/app_regression_audit.py` 新增 `Minimal anime visual direction` 守护：

- 要求主界面引用 `anime_*` 插画资源。
- 要求四张插画资源存在。
- 要求主 UI 不再引用旧写实 hero 图片资源。

## Next Art Tasks

- 给“今晚组局”和“慢慢走”各补一张更有频道差异的插画，避免所有频道都像同一张图的变体。
- 结果页任务卡继续减文字，改成更像动画分镜卡。
- 图标系统下一轮可以从文字图标升级为项目内线性小图标，但保持字体兼容。
