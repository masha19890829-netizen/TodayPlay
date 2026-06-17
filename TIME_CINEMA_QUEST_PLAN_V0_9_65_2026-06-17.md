# TodayPlay V0.9.65 Time-Cinema Quest Plan

Date: 2026-06-17
Producer: Project Lead / Product Review
Feature owner: Chief Product Manager

## 0. Product Decision

V0.9.65 introduces `时光电影 / Time-Cinema Quest` as a new emotional route mode.

The product idea is not to build a movie-location encyclopedia. The first version should let users say:

```text
今晚想像电影片段一样走一段城市路线
```

Then TodayPlay generates several cinematic ways to spend today: verified filming-location routes when the source is trustworthy, and clearly labeled movie-inspired routes when the location is only a local sample or mood reference.

## 1. Core User Need

Users are not only asking "where should I go?" They are asking:

- Can today feel like a memorable scene?
- Can a date become easier to open with a shared story?
- Can friends take photos and create a small ritual instead of just eating?
- Can solo users walk through a city as if they are inside a quiet film?

The feature should turn city space into a short playable scene:

```text
City + relationship + mood + time + movie feeling -> cinematic route cards -> one selected route script -> ticket-stamp memory
```

## 2. Content Truth Boundary

This is the most important rule.

TodayPlay must not invent movie facts.

Every cinematic place has one of three source states:

| State | Meaning | App wording |
| --- | --- | --- |
| `verified_filming_location` | A trustworthy source confirms the filming or scene relation. | "已核验取景地" |
| `cinema_inspired_local_sample` | The stop is not claimed as a real filming location; it only creates a movie-like mood. | "电影感灵感路线" |
| `pending_verification` | The team has a candidate but no usable source yet. | "待核验，不作为真实取景说明" |

First public MVP should mostly use `cinema_inspired_local_sample` unless the team has source records and rights-safe wording.

No fake heat, ratings, UGC, official film partnership, celebrity connection, or copyrighted stills.

## 3. Home Entry

Add one shortcut chip to the chat-first home:

```text
电影感
```

Example prompts:

- "上海，今晚两个人，想走一条电影感路线"
- "东京，一个人，想去像电影里的安静街角"
- "下雨天，想要室内一点，有电影氛围"

Home should not become a movie database. The entry stays inside the one-sentence AI flow.

## 4. Candidate Card Design

When the user asks for movie feeling, one candidate card should appear in the first visible set:

```text
时光电影路线
把今天剪成一段城市片段

胶片开场 / 名场面感 / 票根收尾
今晚 2 小时 / 150 元以内 / 少走路

AI 会优先选择电影感街区、影视基地、夜景、书店、咖啡馆或可拍照公共空间。
```

The card must visually differ from generic route cards:

- Filmstrip route mini visual.
- Ticket perforation feeling.
- A small `source` cue: "样例内容，真实取景需核验".
- CTA remains "选这条生成路线".

## 5. Result Page Experience

The selected route result should feel like a route script:

```text
路线标题：时光电影路线
为什么适合你：
- 你想要电影感，所以路线优先选有镜头感的街区/室内/夜景。
- 当前地点为本地样例，真实取景关系出发前需核验。
- 每一站给一个小镜头任务。

路线预览：
1. 胶片开场：找到第一处城市光线
2. 过场镜头：慢走或坐下补给
3. 票根收尾：拍一张今天的定格
```

## 6. UI Design Wireframes

Concept image:

```text
docs/design/todayplay-time-cinema-v0.9.65-concept.png
```

### Home

```text
┌────────────────────────────┐
│ TodayPlay            历史 设置 │
│                            │
│       今天想怎么玩？          │
│ ┌────────────────────────┐ │
│ │ 今晚两个人，想走电影感路线 │ │
│ │                    发送 │ │
│ └────────────────────────┘ │
│ 约会 朋友 独处 电影感 少走路   │
└────────────────────────────┘
```

### Candidate Cards

```text
┌────────────────────────────┐
│ 为你生成了 6 种今天的过法      │
│ ┌──────────────┐           │
│ │ 时光电影路线   │ film strip │
│ │ 胶片开场/镜头感/票根收尾     │
│ │ 样例内容，取景需核验          │
│ │ [选这条生成路线]             │
│ └──────────────┘           │
└────────────────────────────┘
```

### Route Script

```text
┌────────────────────────────┐
│ 时光电影路线          收藏 分享 │
│ FIT 为什么适合你              │
│ 电影感街区 / 票根任务 / 同城样例 │
│                            │
│ MAP 路线预览                 │
│ 1 胶片开场                   │
│ 2 过场镜头                   │
│ 3 票根收尾                   │
│                            │
│ 完成最后一站：盖章生成今日票根   │
└────────────────────────────┘
```

## 7. MVP Acceptance Criteria

- Home has a visible "电影感" chip.
- Typing "电影 / 电影感 / 取景地 / 名场面 / film / cinema" triggers a visible `时光电影路线` candidate card.
- Selecting that card produces a result page whose title is `时光电影路线`.
- Route ranking prioritizes `cinema`, `film`, `photo`, `night`, `citywalk`, `indoor`, or `bookstore/cafe` categories.
- App copy clearly says sample content is not a verified filming claim unless verified.
- The APK passes app regression audit, adaptive UI audit, build, lint, signing, emulator install, cold launch, candidate generation, and result-page screenshot QA.

## 8. Development Scope For V0.9.65

1. Add `movie` quick chip in chat-first home.
2. Extend intent parser to detect movie/cinema/filming-location terms.
3. Add `cinema` candidate route strategy into the first visible card set.
4. Add strategy scoring for cinema POIs.
5. Add a small set of Shanghai/Tokyo cinema-inspired sample POIs with source labels that do not claim unverified filming facts.
6. Update regression audit to guard the feature.
7. Build and deliver a new APK.

## 9. Not In This Version

- No real movie stills.
- No copyrighted logos.
- No celebrity face or character image.
- No official film partnership claims.
- No public claim that a location is a real filming location unless source records are added later.
- No global city expansion beyond sample support.

