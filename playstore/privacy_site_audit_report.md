# Privacy Site Audit

Project: `D:\AppStore\nemu\real`
Privacy site: `D:\AppStore\nemu\real\playstore\privacy_site`
Overall: **pass**

| Check | Status | Detail |
| --- | --- | --- |
| Privacy site index exists | pass | D:\AppStore\nemu\real\playstore\privacy_site\index.html |
| Privacy site README exists | pass | D:\AppStore\nemu\real\playstore\privacy_site\README.md |
| Privacy site security headers exist | pass | D:\AppStore\nemu\real\playstore\privacy_site\_headers |
| Privacy locale en | pass | Section id/lang plus localized anchor copy and privacy body tokens: English, TodayPlay helps users, Google Play Billing |
| Privacy locale zh-CN | pass | Section id/lang plus localized anchor copy and privacy body tokens: 简体中文, 今天怎么玩, 本地 |
| Privacy locale zh-TW | pass | Section id/lang plus localized anchor copy and privacy body tokens: 繁體中文, 今天怎麼玩, 本機 |
| Privacy locale ja | pass | Section id/lang plus localized anchor copy and privacy body tokens: 日本語, TodayPlay, ローカル |
| Privacy locale ko | pass | Section id/lang plus localized anchor copy and privacy body tokens: 한국어, TodayPlay, 로컬 |
| Privacy locale es | pass | Section id/lang plus localized anchor copy and privacy body tokens: Español, TodayPlay, local |
| Required launch disclosures | pass | Local data deletion, no sensitive permissions, map fallback, Billing verification, mock/authorized-source policy, and placeholder-contact warning. |
| No common UTF-8 mojibake markers | pass | No common mojibake markers found. |
| Placeholder contact warning | pass | The static privacy site must clearly block production use until real publisher/support identity is inserted. |
| Language navigation anchors | pass | Navigation should expose every launch locale. |

## Notes
- This audit checks local deployable privacy material only; Google Play still requires a public HTTPS URL and real publisher/support identity before submission.
