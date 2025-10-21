Volkszähler App - Installationsanleitung

Schritt-für-Schritt Anleitung

Entwicklungsumgebung einrichten

Android Studio installieren

Download:
- Besuchen Sie: https://developer.android.com/studio
- Laden Sie die neueste Version herunter (Hedgehog 2023.1.1 oder neuer)

Installation:
- Windows: Führen Sie die .exe Datei aus
- macOS: Öffnen Sie die .dmg Datei und ziehen Sie Android Studio in Applications
- Linux: Entpacken Sie das Archiv und führen Sie studio.sh aus

Erste Einrichtung:
- Starten Sie Android Studio
- Folgen Sie dem Setup-Wizard
- Installieren Sie die empfohlenen SDK-Komponenten

JDK 17 installieren

Android Studio bringt normalerweise ein JDK mit. Falls nicht:

Windows/macOS/Linux:
- Download von https://adoptium.net/
- Wählen Sie JDK 17 (LTS)
- Installieren Sie es und setzen Sie JAVA\_HOME

Projekt erstellen

Neue Android Studio Projekt

Projekt anlegen:
File → New → New Project
→ Empty Activity (Compose)
→ Name: VolkszaehlerApp
→ Package: org.volkszaehler.volkszaehlerapp
→ Language: Kotlin
→ Minimum SDK: API 24 (Android 7.0)
→ Build configuration language: Gradle (Groovy)


Projektstruktur erstellen:
app/src/main/java/org/volkszaehler/volkszaehlerapp/
├── data/
│   ├── local/
│   ├── model/
│   ├── remote/
│   └── repository/
├── di/
├── ui/
│   ├── channellist/
│   ├── chart/
│   ├── navigation/
│   └── theme/
└── util/


Dependencies hinzufügen

build.gradle (Project level)

Ersetzen Sie den Inhalt mit:

buildscript {
ext {
compose\_version = '1.5.3'
kotlin\_version = '1.9.10'
}
repositories {
google()
mavenCentral()
}
dependencies {
classpath 'com.android.tools.build:gradle:8.1.2'
classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin\_version"
classpath 'com.google.dagger:hilt-android-gradle-plugin:2.48.1'
}
}

plugins {
id 'com.android.application' version '8.1.2' apply false
id 'com.android.library' version '8.1.2' apply false
id 'org.jetbrains.kotlin.android' version '1.9.10' apply false
}


build.gradle (App level)

Fügen Sie alle Dependencies aus der bereitgestellten build.gradle (App) Datei hinzu.

settings.gradle

pluginManagement {
repositories {
google()
mavenCentral()
gradlePluginPortal()
}
}

dependencyResolutionManagement {
repositoriesMode.set(RepositoriesMode.FAIL\ON\PROJECT\_REPOS)
repositories {
google()
mavenCentral()
maven { url 'https://jitpack.io' }
}
}

rootProject.name = "VolkszaehlerApp"
include ':app'


Dateien kopieren

Kotlin Dateien

Kopieren Sie alle bereitgestellten .kt Dateien in die entsprechenden Ordner:

Data Layer:
- AppDatabase.kt → data/local/
- ChannelDao.kt → data/local/
- Channel.kt, ChannelData.kt, DataTuple.kt → data/model/
- VolkszaehlerApiService.kt → data/remote/
- VolkszaehlerRepository.kt → data/repository/

DI Layer:
- NetworkModule.kt, DatabaseModule.kt → di/

UI Layer:
- ChannelListScreen.kt, ChannelListViewModel.kt → ui/channellist/
- ChartScreen.kt, ChartViewModel.kt → ui/chart/
- Navigation.kt → ui/navigation/
- Theme.kt, Type.kt → ui/theme/

Utils:
- NetworkResult.kt, PreferencesManager.kt → util/

Root:
- MainActivity.kt, VolkszaehlerApplication.kt → Root-Package

XML Dateien

AndroidManifest.xml → app/src/main/
strings.xml → app/src/main/res/values/

Gradle Sync

Klicken Sie auf "Sync Now" in der Benachrichtigungsleiste
Warten Sie, bis alle Dependencies heruntergeladen sind
Beheben Sie eventuelle Fehler

App ausführen

Emulator einrichten

AVD Manager öffnen:
Tools → Device Manager → Create Device


Gerät konfigurieren:
- Phone → Pixel 6
- System Image → API 34 (Android 14)
- AVD Name: Pixel\6\API\_34

Emulator starten:
- Klicken Sie auf den Play-Button beim erstellten AVD

Physisches Gerät verbinden

Developer Options aktivieren:
- Einstellungen → Über das Telefon
- 7x auf "Build-Nummer" tippen

USB Debugging aktivieren:
- Einstellungen → Entwickleroptionen
- USB-Debugging aktivieren

Gerät verbinden:
- USB-Kabel anschließen
- Debugging-Berechtigung auf dem Gerät akzeptieren

App starten

Wählen Sie das Zielgerät in der Toolbar
Klicken Sie auf den grünen Play-Button
Warten Sie, bis die App installiert und gestartet wird

Konfiguration

API URL ändern

In PreferencesManager.kt:

private const val DEFAULT\BASE\URL = "https://your-server.com/middleware.php/"


Logging aktivieren

In NetworkModule.kt ist Logging bereits aktiviert für Debug-Builds.

Troubleshooting

Problem: Gradle Sync fehlgeschlagen
Lösung
./gradlew clean
./gradlew build --refresh-dependencies


Problem: Hilt Annotation Processor Fehler
Lösung
Stellen Sie sicher, dass kapt Plugin aktiviert ist
Rebuild Project: Build → Rebuild Project

Problem: Compose Version Konflikt
Lösung
Überprüfen Sie, dass alle Compose-Dependencies die gleiche Version verwenden
Nutzen Sie compose-bom für konsistente Versionen

Problem: Room Schema Export
Lösung
Fügen Sie in build.gradle (App) hinzu:
android {
defaultConfig {
javaCompileOptions {
annotationProcessorOptions {
arguments += ["room.schemaLocation": "$projectDir/schemas".toString()]
}
}
}
}


Problem: Netzwerk-Fehler in Emulator
Lösung
Überprüfen Sie android:usesCleartextTraffic="true" im Manifest
Für HTTPS: Fügen Sie Network Security Config hinzu

Build Variants

Debug Build
./gradlew assembleDebug

APK: app/build/outputs/apk/debug/app-debug.apk

Release Build

Keystore erstellen:
keytool -genkey -v -keystore volkszaehler.keystore -alias volkszaehler -keyalg RSA -keysize 2048 -validity 10000


signing.properties erstellen:
storeFile=../volkszaehler.keystore
storePassword=your\_password
keyAlias=volkszaehler
keyPassword=your\_password


Build:
./gradlew assembleRelease


Nächste Schritte

Testing: Fügen Sie Unit- und UI-Tests hinzu
CI/CD: Richten Sie GitHub Actions oder GitLab CI ein
Analytics: Integrieren Sie Firebase Analytics
Crash Reporting: Fügen Sie Crashlytics hinzu
Performance: Optimieren Sie mit Android Profiler

Hilfreiche Ressourcen

Android Developer Docs
Jetpack Compose Tutorial
Kotlin Documentation
Hilt Guide
Volkszähler API

Support

Bei Problemen:
Überprüfen Sie die Logs in Android Studio (Logcat)
Konsultieren Sie die Volkszähler-Dokumentation
Erstellen Sie ein Issue auf GitHub