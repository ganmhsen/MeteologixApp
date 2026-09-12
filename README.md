# Meteologix App - تطبيق خرائط الطقس للسعودية

تطبيق Android يعرض موقع **meteologix.com/sa/model-charts/standard** مع تحسينات للجوال.

## ✨ المميزات
- يعرض خرائط نماذج الطقس (GFS) للسعودية
- يدعم جميع المعاملات: درجة الحرارة، الضغط، الرياح، الأمطار، الغيوم، إلخ
- تكبير/تصغير باللمس (Pinch to zoom)
- سحب للتحديث (Pull to refresh)
- شريط تقدم أثناء التحميل
- صفحة خطأ جميلة عند عدم وجود إنترنت
- دعم كامل للغة العربية (RTL)
- أيقونات متكيفة (Adaptive icons)
- شاشة بداية (Splash screen)

---

## 📱 طريقة بناء APK (لغير المبرمجين)

### الطريقة الأسهل: Android Studio (موصى بها)

#### 1. تحميل وتثبيت Android Studio
- اذهب إلى: https://developer.android.com/studio
- حمّل الإصدار المناسب لنظامك (Windows/Mac/Linux)
- ثبّته باتباع الخطوات الافتراضية
- **مهم**: عند أول تشغيل، سيطلب تحميل Android SDK - اضغط "Next" حتى النهاية

#### 2. فتح المشروع
```
File → Open → اختر مجلد "MeteologixApp" → OK
```
- انتظر حتى تظهر رسالة **"Gradle Sync Finished"** في الأسفل
- إذا ظهر خطأ، اضغط **"Try Again"** أو **"Sync Project with Gradle Files"** (أيقونة الفيل في الشريط العلوي)

#### 3. بناء APK
```
Build → Build Bundle(s) / APK(s) → Build APK(s)
```
- انتظر حتى يكتمل البناء (قد يستغرق 2-5 دقائق أول مرة)
- ستظهر رسالة في الأسفل: **"APK(s) generated successfully"**
- اضغط على رابط **"locate"** بجانب الرسالة

#### 4. مكان ملف APK
```
MeteologixApp/app/build/outputs/apk/debug/app-debug.apk
```
- انسخ هذا الملف إلى هاتفك وثبّته

---

### طريقة بديلة: البناء السحابي المجاني (GitHub Actions)

إذا لم تستطع تثبيت Android Studio:

1. أنشئ حساب على [GitHub.com](https://github.com)
2. أنشئ مستودع (Repository) جديد باسم `MeteologixApp`
3. ارفع كل ملفات المشروع للمستودع
4. سيبدأ البناء تلقائياً (انظر تبويب **Actions**)
5. عند الانتهاء، اذهب إلى **Actions → أحدث تشغيل → Artifacts → تحميل app-debug.apk**

---

## 📂 هيكل المشروع

```
MeteologixApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/meteologix/app/
│   │   │   ├── MainActivity.kt          # الشاشة الرئيسية (WebView)
│   │   │   └── MeteologixApplication.kt # تهيئة التطبيق
│   │   ├── res/
│   │   │   ├── layout/activity_main.xml # الواجهة: WebView + ProgressBar
│   │   │   ├── values/                  # النصوص، الألوان، الثيمات
│   │   │   ├── drawable/                # الأيقونات، شاشة البداية
│   │   │   ├── mipmap-*/                # أيقونات التطبيق بجميع المقاسات
│   │   │   └── xml/                     # إعدادات الشبكة والنسخ الاحتياطي
│   │   └── AndroidManifest.xml          # صلاحيات وإعدادات التطبيق
│   ├── build.gradle.kts                 # إعدادات البناء
│   └── proguard-rules.pro               # قواعد الحماية
├── build.gradle.kts                     # إعدادات المشروع العامة
├── settings.gradle.kts                  # إعدادات الوحدات
├── gradle.properties                    # خصائص Gradle
├── gradlew, gradlew.bat                 # سكريبتات البناء
└── gradle/wrapper/                      # غلاف Gradle
```

---

## ⚙️ الإعدادات التقنية

| الإعداد | القيمة |
|----------|--------|
| **Package Name** | `com.meteologix.app` |
| **Min SDK** | 24 (Android 7.0) |
| **Target SDK** | 34 (Android 14) |
| **Version** | 1.0.0 |
| **Orientation** | Portrait فقط |
| **URL المستهدف** | `https://meteologix.com/sa/model-charts/standard` |

---

## 🔧 استكشاف الأخطاء

### مشكلة: "Gradle sync failed"
**الحل**: 
- تأكد من اتصالك بالإنترنت
- File → Invalidate Caches → Invalidate and Restart

### مشكلة: "SDK location not found"
**الحل**:
- File → Settings → Appearance & Behavior → System Settings → Android SDK
- تأكد من وجود مسار صحيح، أو اضغط "Edit" واختر مكان SDK

### مشكلة: بناء بطيء جداً
**الحل**:
- أول بناء يأخذ وقتاً طويلاً (تحميل المكتبات)
- البناءات اللاحقة ستكون أسرع بكثير

### التطبيق لا يفتح على الهاتف
**الحل**:
- Settings → Security → تثبيت من مصادر غير معروفة → تفعيل للملف أو للمتصفح
- أو استخدم: `adb install app-debug.apk` من سطر الأوامر

---

## 📄 الترخيص
هذا تطبيق غلاف (WebView) لموقع meteologix.com. جميع حقوق بيانات الطقس تعود لأصحاب الموقع الأصلي.

---

## 🆘 تحتاج مساعدة؟
إذا واجهت أي مشكلة:
1. جرب إعادة تشغيل Android Studio
2. تأكد من تحديث Android Studio لأحدث إصدار
3. تأكد من وجود مساحة كافية على القرص (5+ جيجا)

**الملف النهائي**: `app-debug.apk` - انقله لهاتفك وثبّته مباشرة!