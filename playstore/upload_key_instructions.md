# Google Play Upload Key Instructions

Do not commit real keystores or passwords.

Current status: a local upload key has been generated for this project. See `playstore/upload_key_status.md` for non-secret certificate fingerprints and handling rules.

## 1. Create an upload key

Run from the project root:

```powershell
New-Item -ItemType Directory -Force -Path release
keytool -genkeypair -v -keystore release/todayplay-upload.jks -keyalg RSA -keysize 4096 -validity 10000 -alias todayplay-upload
```

Use a strong password and record it securely outside the repository.

## 2. Configure Gradle signing

Copy:

```powershell
Copy-Item keystore.properties.template keystore.properties
```

Edit `keystore.properties`:

```properties
storeFile=release/todayplay-upload.jks
storePassword=<real store password>
keyAlias=todayplay-upload
keyPassword=<real key password>
```

The Gradle release build automatically uses this signing config when all four values exist.

## 3. Build the signed AAB

```powershell
$env:JAVA_HOME='D:\AppStore\.android-build-tools\jdk'
$env:ANDROID_HOME='D:\AppStore\.android-build-tools\sdk'
$env:ANDROID_SDK_ROOT='D:\AppStore\.android-build-tools\sdk'
$env:PATH="$env:JAVA_HOME\bin;$env:ANDROID_HOME\platform-tools;$env:PATH"
.\gradlew.bat bundleRelease
```

Output:

`app\build\outputs\bundle\release\app-release.aab`

## 4. Google Play setup

In Play Console:

- Create the app.
- Enable Play App Signing.
- Upload the release AAB.
- Keep this upload key for future updates.
- If the upload key is lost, follow Google Play's upload key reset process.
