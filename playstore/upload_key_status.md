# Upload Key Status

Generated at: 2026-06-08

## Local files

- Keystore: `release/todayplay-upload.jks`
- Upload certificate: `release/todayplay-upload-certificate.pem`
- Gradle signing config: `keystore.properties`

These files are intentionally ignored by `.gitignore`.

## Certificate fingerprints

- SHA-1: `C3:84:1C:DC:FF:27:9C:8E:FF:87:E4:79:9A:F7:65:E2:B1:95:42:1D`
- SHA-256: `2B:03:6B:01:70:99:39:7A:AB:C4:AA:F0:AE:1F:E8:FC:94:56:DD:18:A0:70:90:D0:4A:0C:E5:96:E9:99:B6:95`

## Handling rules

- Do not commit `keystore.properties`, `.jks`, `.pem`, `.p12`, `.key`, or passwords.
- Back up the keystore and passwords outside the project folder.
- Use this key as the Google Play upload key after enabling Play App Signing.
- If the key is lost after the app is published, releases may be blocked until Google Play support resets the upload key.

## Verification command

```powershell
$props = ConvertFrom-StringData (Get-Content -Raw -Encoding UTF8 .\keystore.properties)
& 'D:\AppStore\.android-build-tools\jdk\bin\keytool.exe' `
  -list -v `
  -keystore .\release\todayplay-upload.jks `
  -alias todayplay-upload `
  -storepass $props.storePassword
```
