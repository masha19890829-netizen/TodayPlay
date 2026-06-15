param(
    [string]$ProjectRoot = (Resolve-Path "$PSScriptRoot\..").Path,
    [string]$JavaHome = "D:\AppStore\.android-build-tools\jdk",
    [string]$AndroidHome = "D:\AppStore\.android-build-tools\sdk",
    [string]$SkillRoot = "C:\Users\44277\.codex\skills\formal-android-release-assistant"
)

$ErrorActionPreference = "Stop"

$env:JAVA_HOME = $JavaHome
$env:ANDROID_HOME = $AndroidHome
$env:ANDROID_SDK_ROOT = $AndroidHome
$env:PATH = "$env:JAVA_HOME\bin;$env:ANDROID_HOME\platform-tools;$env:PATH"

Push-Location $ProjectRoot
try {
    Write-Host "== Building debug APK, release AAB, and lint report =="
    .\gradlew.bat assembleDebug bundleRelease lintDebug
    if ($LASTEXITCODE -ne 0) {
        throw "Gradle preflight build failed with exit code $LASTEXITCODE"
    }

    Write-Host ""
    Write-Host "== Android release audit =="
    python "$SkillRoot\scripts\android_release_audit.py" $ProjectRoot

    Write-Host ""
    Write-Host "== Google Play Billing audit =="
    python "$SkillRoot\scripts\billing_product_audit.py" $ProjectRoot

    Write-Host ""
    Write-Host "== Billing backend audit =="
    $billingBackendOutput = python "$ProjectRoot\playstore\billing_backend_audit.py" $ProjectRoot
    $billingBackendReport = "$ProjectRoot\playstore\billing_backend_audit_report.md"
    $billingBackendOutput | Out-File -FilePath $billingBackendReport -Encoding utf8
    $billingBackendOutput
    Write-Host ""
    Write-Host "Billing backend audit report: $billingBackendReport"

    Write-Host ""
    Write-Host "== Localization audit =="
    $localizationOutput = python "$SkillRoot\scripts\localization_audit.py" $ProjectRoot
    $localizationReport = "$ProjectRoot\playstore\localization_audit_report.md"
    $localizationOutput | Out-File -FilePath $localizationReport -Encoding utf8
    $localizationOutput
    Write-Host ""
    Write-Host "Localization audit report: $localizationReport"

    Write-Host ""
    Write-Host "== Localization copy-fit audit =="
    $copyFitOutput = python "$ProjectRoot\playstore\localization_copyfit_audit.py" $ProjectRoot
    $copyFitReport = "$ProjectRoot\playstore\localization_copyfit_audit_report.md"
    $copyFitOutput | Out-File -FilePath $copyFitReport -Encoding utf8
    $copyFitOutput
    Write-Host ""
    Write-Host "Localization copy-fit audit report: $copyFitReport"

    Write-Host ""
    Write-Host "== Localized source encoding audit =="
    $localizedSourceEncodingOutput = python "$ProjectRoot\playstore\localized_source_encoding_audit.py" $ProjectRoot
    $localizedSourceEncodingReport = "$ProjectRoot\playstore\localized_source_encoding_audit_report.md"
    $localizedSourceEncodingOutput | Out-File -FilePath $localizedSourceEncodingReport -Encoding utf8
    $localizedSourceEncodingOutput
    Write-Host ""
    Write-Host "Localized source encoding audit report: $localizedSourceEncodingReport"

    Write-Host ""
    Write-Host "== Privacy site audit =="
    $privacySiteOutput = python "$ProjectRoot\playstore\privacy_site_audit.py" $ProjectRoot
    $privacySiteReport = "$ProjectRoot\playstore\privacy_site_audit_report.md"
    $privacySiteOutput | Out-File -FilePath $privacySiteReport -Encoding utf8
    $privacySiteOutput
    Write-Host ""
    Write-Host "Privacy site audit report: $privacySiteReport"

    Write-Host ""
    Write-Host "== Generated route display audit =="
    $generatedRouteDisplayOutput = python "$ProjectRoot\playstore\generated_route_display_audit.py" $ProjectRoot
    $generatedRouteDisplayReport = "$ProjectRoot\playstore\generated_route_display_audit_report.md"
    $generatedRouteDisplayOutput | Out-File -FilePath $generatedRouteDisplayReport -Encoding utf8
    $generatedRouteDisplayOutput
    Write-Host ""
    Write-Host "Generated route display audit report: $generatedRouteDisplayReport"

    Write-Host ""
    Write-Host "== App regression audit =="
    $appRegressionOutput = python "$ProjectRoot\playstore\app_regression_audit.py" $ProjectRoot
    $appRegressionReport = "$ProjectRoot\playstore\app_regression_audit_report.md"
    $appRegressionOutput | Out-File -FilePath $appRegressionReport -Encoding utf8
    $appRegressionOutput
    Write-Host ""
    Write-Host "App regression audit report: $appRegressionReport"

    Write-Host ""
    Write-Host "== Release config audit =="
    $releaseConfigOutput = python "$ProjectRoot\playstore\release_config_audit.py" $ProjectRoot
    $releaseConfigReport = "$ProjectRoot\playstore\release_config_audit_report.md"
    $releaseConfigOutput | Out-File -FilePath $releaseConfigReport -Encoding utf8
    $releaseConfigOutput
    Write-Host ""
    Write-Host "Release config audit report: $releaseConfigReport"

    Write-Host ""
    Write-Host "== Google Play submission gate =="
    $gateOutput = python "$ProjectRoot\playstore\google_play_submission_gate.py" $ProjectRoot
    $gateReport = "$ProjectRoot\playstore\submission_gate_report.md"
    $gateOutput | Out-File -FilePath $gateReport -Encoding utf8
    $gateOutput
    Write-Host ""
    Write-Host "Submission gate report: $gateReport"

    Write-Host ""
    Write-Host "== Travel content audit =="
    $travelContentOutput = python "$ProjectRoot\playstore\travel_content_audit.py" $ProjectRoot
    $travelContentReport = "$ProjectRoot\playstore\travel_content_audit_report.md"
    $travelContentOutput | Out-File -FilePath $travelContentReport -Encoding utf8
    $travelContentOutput
    Write-Host ""
    Write-Host "Travel content audit report: $travelContentReport"

    Write-Host ""
    Write-Host "== Generation flow audit =="
    $generationFlowOutput = python "$ProjectRoot\playstore\generation_flow_audit.py" $ProjectRoot
    $generationFlowReport = "$ProjectRoot\playstore\generation_flow_audit_report.md"
    $generationFlowOutput | Out-File -FilePath $generationFlowReport -Encoding utf8
    $generationFlowOutput
    Write-Host ""
    Write-Host "Generation flow audit report: $generationFlowReport"

    Write-Host ""
    Write-Host "== Adaptive UI audit =="
    $adaptiveUiOutput = python "$ProjectRoot\playstore\adaptive_ui_audit.py" $ProjectRoot
    $adaptiveUiReport = "$ProjectRoot\playstore\adaptive_ui_audit_report.md"
    $adaptiveUiOutput | Out-File -FilePath $adaptiveUiReport -Encoding utf8
    $adaptiveUiOutput
    Write-Host ""
    Write-Host "Adaptive UI audit report: $adaptiveUiReport"

    Write-Host ""
    Write-Host "== Travel content worker contract tests =="
    Push-Location "$ProjectRoot\backend\travel-content-worker"
    try {
        npm.cmd test
        if ($LASTEXITCODE -ne 0) {
            throw "Travel content worker contract tests failed with exit code $LASTEXITCODE"
        }
    }
    finally {
        Pop-Location
    }

    Write-Host ""
    Write-Host "== Billing verify worker contract tests =="
    Push-Location "$ProjectRoot\backend\billing-verify-worker"
    try {
        npm.cmd test
        if ($LASTEXITCODE -ne 0) {
            throw "Billing verify worker contract tests failed with exit code $LASTEXITCODE"
        }
    }
    finally {
        Pop-Location
    }

    Write-Host ""
    Write-Host "== Release artifacts =="
    Get-Item `
        "$ProjectRoot\app\build\outputs\apk\debug\app-debug.apk", `
        "$ProjectRoot\app\build\outputs\bundle\release\app-release.aab" |
        Select-Object FullName, Length, LastWriteTime |
        Format-Table -AutoSize
}
finally {
    Pop-Location
}
