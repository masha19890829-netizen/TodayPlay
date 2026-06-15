# TodayPlay Privacy Site

This folder contains a deployable privacy policy site for Google Play review.

Files:
- `index.html`: multilingual privacy policy covering English, Simplified Chinese, Traditional Chinese, Japanese, Korean, and Spanish.
- `_headers`: optional security headers for Cloudflare Pages or compatible static hosts.

Before production submission:
1. Replace the publisher name with the real developer or company identity.
2. Replace the support contact with a real support email.
3. Deploy this folder to a public HTTPS URL.
4. Put that URL into `playstore/play_console_submission_fields.md`.
5. Re-run `google_play_submission_gate.py`.

Recommended URL:

```text
https://YOUR_DOMAIN/privacy/todayplay/
```

Do not submit Google Play with placeholder publisher/contact information.
