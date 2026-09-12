@echo off
chcp 65001 >nul
title بناء Meteologix APK

echo.
echo ==========================================
echo   بناء تطبيق Meteologix - إصدار APK
echo ==========================================
echo.

REM التحقق من وجود Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [خطأ] Java غير مثبت أو غير في PATH
    echo.
    echo يرجى تثبيت Java 17 أو 21 من:
    echo https://adoptium.net/temurin/releases/
    echo.
    pause
    exit /b 1
)

echo [معلومة] Java موجود:
java -version 2>&1 | findstr /r "version"
echo.

REM التحقق من Android SDK
if not defined ANDROID_HOME (
    if not defined ANDROID_SDK_ROOT (
        echo [تحذير] ANDROID_HOME غير محدد
        echo سيحاول Gradle تحميل SDK تلقائياً (يتطلب إنترنت)
        echo.
    )
)

echo [خطوة] جاري البناء باستخدام Gradle Wrapper...
echo.

REM محاولة البناء
gradlew.bat clean assembleDebug --no-daemon --stacktrace

if %errorlevel% equ 0 (
    echo.
    echo ==========================================
    echo [نجاح] تم بناء APK بنجاح!
    echo ==========================================
    echo.
    echo مكان الملف:
    echo   app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo يمكنك الآن نقل هذا الملف لهاتفك وتثبيته.
) else (
    echo.
    echo ==========================================
    echo [فشل] حدث خطأ أثناء البناء
    echo ==========================================
    echo.
    echo جرب الحلول التالية:
    echo 1. تأكد من اتصالك بالإنترنت
    echo 2. جرب: gradlew.bat --stop ثم أعد المحاولة
    echo 3. استخدم Android Studio بدلاً من سطر الأوامر
    echo.
)

pause