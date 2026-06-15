# Privacy Policy Hosting Plan

Google Play requires a public privacy policy URL. The current deployable policy site is:

`playstore/privacy_site/index.html`

Legacy single-language draft:

`playstore/privacy_policy_zh.html`

## Minimum hosting requirements

- Publicly accessible HTTPS URL.
- No login required.
- Stable URL that will not disappear during review.
- Page content must match the app's real behavior.
- Contact email must be replaced before submission.

## Suggested hosting options

### Option A: Cloudflare Pages

1. Create a minimal repository or upload `playstore/privacy_site`.
2. Set the served file to `index.html`.
3. Deploy to Cloudflare Pages.
4. Use the generated HTTPS URL in Play Console.

### Option B: GitHub Pages

1. Create a public repository for legal/store pages.
2. Copy `playstore/privacy_site/index.html` to `index.html`.
3. Enable GitHub Pages.
4. Use the public HTTPS URL in Play Console.

### Option C: Existing website

1. Upload the contents of `playstore/privacy_site` under a stable path.
2. Confirm it is reachable in a browser without authentication.
3. Use that URL in Play Console.

## Before submitting

- Replace placeholder publisher/contact language with a real support email and developer identity.
- Add developer/company name if available.
- If backend, analytics, account, location, map SDK, or photo upload are added, update this policy before submitting.
