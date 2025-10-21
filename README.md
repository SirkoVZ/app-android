Volkszähler Android App

Eine moderne Android-App für das Volkszähler-Projekt, entwickelt mit Jetpack Compose, Kotlin und Clean Architecture.

Features

Kanal-Übersicht: Anzeige aller verfügbaren Volkszähler-Kanäle
Suchfunktion: Schnelles Filtern von Kanälen
Interaktive Diagramme: Visualisierung von Messdaten mit verschiedenen Zeitbereichen
Offline-Support: Lokales Caching mit Room Database
Material Design 3: Moderne UI mit Dynamic Colors
Dark Mode: Automatische Unterstützung für helle und dunkle Themes

Technologie-Stack

Core
Kotlin - Programmiersprache
Jetpack Compose - Deklaratives UI Framework
Material Design 3 - UI/UX Design System
Coroutines & Flow - Asynchrone Programmierung

Architecture
MVVM - Model-View-ViewModel Pattern
Clean Architecture - Separation of Concerns
Repository Pattern - Datenabstraktion
Dependency Injection - Hilt/Dagger

Networking
Retrofit - REST API Client
OkHttp - HTTP Client
Moshi - JSON Serialisierung

Database
Room - Lokale SQLite Datenbank
Flow - Reaktive Datenströme

UI Components
Navigation Compose - App-Navigation
YCharts - Diagramm-Bibliothek
Material Icons - Icon-Set

Utilities
Timber - Logging Framework
SharedPreferences - Einstellungen

Projektstruktur

app/
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   └── ChannelDao.kt
│   ├── model/
│   │   ├── Channel.kt
│   │   ├── ChannelData.kt
│   │   └── DataTuple.kt
│   ├── remote/
│   │   └── VolkszaehlerApiService.kt
│   └── repository/
│       └── VolkszaehlerRepository.kt
├── di/
│   ├── NetworkModule.kt
│   └── DatabaseModule.kt
├── ui/
│   ├── channellist/
│   │   ├── ChannelListScreen.kt
│   │   └── ChannelListViewModel.kt
│   ├── chart/
│   │   ├── ChartScreen.kt
│   │   └── ChartViewModel.kt
│   ├── navigation/
│   │   └── Navigation.kt
│   └── theme/
│       ├── Theme.kt
│       └── Type.kt
├── util/
│   ├── NetworkResult.kt
│   └── PreferencesManager.kt
├── MainActivity.kt
└── VolkszaehlerApplication.kt


Installation

Voraussetzungen

Android Studio Hedgehog (2023.1.1) oder neuer
JDK 17
Android SDK 34
Minimum SDK: 24 (Android 7.0)

Setup

Repository klonen
git clone https://github.com/yourusername/volkszaehler-app.git
cd volkszaehler-app


Projekt in Android Studio öffnen
- File → Open → Projektordner auswählen

Gradle Sync durchführen
- Android Studio synchronisiert automatisch die Dependencies

App starten
- Emulator oder physisches Gerät verbinden
- Run → Run 'app'

Konfiguration

API Endpoint

Die Standard-API-URL ist http://demo.volkszaehler.org/middleware.php/

Zum Ändern der URL:
// In PreferencesManager.kt
private const val DEFAULT\BASE\URL = "https://your-volkszaehler-instance.com/middleware.php/"


Build Variants

Debug: Aktiviertes Logging, keine Code-Optimierung
Release: Deaktiviertes Logging, ProGuard-Optimierung

API Endpoints

Die App nutzt folgende Volkszähler-API-Endpoints:

Kanäle abrufen
GET /entity.json


Kanal-Daten abrufen
GET /data/{uuid}.json?from={timestamp}&to={timestamp}&group={grouping}

Parameter:
uuid: Kanal-UUID
from: Start-Timestamp (Millisekunden)
to: End-Timestamp (Millisekunden)
group: Gruppierung (hour, day, week, month, year)

Verwendung

Kanal-Liste

App öffnen → Kanal-Liste wird automatisch geladen
Suchen: Suchfeld nutzen zum Filtern
Aktualisieren: Refresh-Button in der TopBar
Kanal auswählen: Checkbox aktivieren für Favoriten
Details öffnen: Auf Kanal tippen für Diagramm

Diagramm-Ansicht

Kanal in der Liste antippen
Zeitbereich wählen: Chips oben (1h, 1d, 1w, 1m, 1y)
Interaktion: Diagramm-Punkte antippen für Details
Statistiken: Scrollen für Min/Max/Durchschnitt
Zurück: Back-Button oder Geste

Dependencies

// Core
androidx.core:core-ktx:1.12.0
androidx.lifecycle:lifecycle-runtime-ktx:2.6.2

// Compose
androidx.compose.ui:ui
androidx.compose.material3:material3
androidx.navigation:navigation-compose:2.7.5

// Hilt
com.google.dagger:hilt-android:2.48.1
androidx.hilt:hilt-navigation-compose:1.1.0

// Networking
com.squareup.retrofit2:retrofit:2.9.0
com.squareup.okhttp3:okhttp:4.12.0

// Database
androidx.room:room-runtime:2.6.1
androidx.room:room-ktx:2.6.1

// Charts
co.yml:ycharts:2.1.0

// Utilities
com.jakewharton.timber:timber:5.0.1


Testing

Unit Tests
./gradlew test


Instrumented Tests
./gradlew connectedAndroidTest


Build

Debug Build
./gradlew assembleDebug


Release Build
./gradlew assembleRelease


APK-Datei: app/build/outputs/apk/release/app-release.apk

Troubleshooting

Netzwerkfehler
Problem "Netzwerkfehler" beim Laden der Kanäle
Lösung
Internetverbindung prüfen
API-URL in PreferencesManager.kt überprüfen
usesCleartextTraffic="true" in AndroidManifest.xml (für HTTP)

Build-Fehler
Problem Gradle Sync fehlgeschlagen
Lösung
./gradlew clean
./gradlew build --refresh-dependencies


Room Database Fehler
Problem Schema-Änderungen führen zu Crashes
Lösung
App deinstallieren und neu installieren
Oder: fallbackToDestructiveMigration() ist bereits aktiviert

Roadmap

[ ] Multi-Channel Diagramme
[ ] Export-Funktion (CSV, PDF)
[ ] Push-Benachrichtigungen bei Schwellwerten
[ ] Widget für Homescreen
[ ] Tablet-Optimierung
[ ] Wear OS Support

Contributing

Contributions sind willkommen! Bitte:

Fork das Repository
Feature Branch erstellen (git checkout -b feature/AmazingFeature)
Änderungen committen (git commit -m 'Add AmazingFeature')
Branch pushen (git push origin feature/AmazingFeature)
Pull Request öffnen

Lizenz

Dieses Projekt ist unter der GPL-3.0 License lizenziert - siehe LICENSE Datei für Details.

Kontakt
Projekt Volkszähler Android App  
Website https://volkszaehler.org  
GitHub https://github.com/volkszaehler

Danksagungen

Volkszähler-Team - Für das großartige Backend-System
YCharts - Für die Chart-Bibliothek
Android Community - Für Jetpack Compose und moderne Tools

Hinweis Diese App ist ein Community-Projekt und nicht offiziell vom Volkszähler-Team unterstützt.






# Volkszaehler Frontend for Android 
App Language: english, deutsch  
**(English description below)**

Download: [https://github.com/volkszaehler/app-android/releases/latest](https://github.com/volkszaehler/app-android/releases/latest "Latest Release")

![ScreenShot1](http://wiki.volkszaehler.org/_media/software/frontends/vz_app/uebersicht.png?w=200&tok=2908a1 "ScreenShots 1 of VolkszaehlerApp") 
![ScreenShot2](http://wiki.volkszaehler.org/_media/software/frontends/vz_app/details.png?w=200&tok=60ff35 "ScreenShots 2 of VolkszaehlerApp") 
![ScreenShot3](http://wiki.volkszaehler.org/_media/software/frontends/vz_app/grafik.png?w=200&tok=3e01fa "ScreenShots 3 of VolkszaehlerApp") 
![ScreenShot4](http://wiki.volkszaehler.org/_media/software/frontends/vz_app/einstellungen.png?w=200&tok=ede86f "ScreenShots 4 of VolkszaehlerApp") 

## VolkszählerApp für Android, Version 0.9.7


Funktionen:  
  
- Abrufen der Channelinformationen und Anzeige der letzten Werte
- Anzeigen von Details zu jedem Channel
- Tabellarische Ansicht der Verbrauchswerte
- Anzeigen von Charts zu einzelnen Channels, incl. Zoom, auch mit „Touch and Move“ im Diagramm
- Anzeigen von Gruppen-Charts und Hinzufügen weiterer Graphen ist möglich ("Long Touch" im Chart)
- Unterstützung von Basic Authentication und HTTPS, **_ABER das Zertifikat wird nicht wirklich geprüft_**   , es werden auch ungültige oder Man-in-the-Middle-Zertifikate akzeptiert
- Einstellungen können (z.B. vor einem Update) gesichert und (nach dem Update/Neuinstallation) wieder eingespielt werden

Benötigte Android Berechtigungen:  
- _android.permission.INTERNET_ für den Zugriff auf die Volkszähler-Installation  
- _android.permission.WRITE_EXTERNAL_STORAGE_ für das Schreiben (und Lesen) des Backups


### Tipps zum Clonen von Github in AndroidStudio (nur für Entwickler):

Wenn das Clonen bzw. das Öffnen des Projektes wegen eines ausgegrauten "Use default gradle wrapper (not configured for the current project)" nicht möglich ist, dann  
- das Projekt nochmal normal als "Open an existing Android Studio project" öffnen und im "Gradle Sync" Dialog einfach OK klicken  
- Wenn nötig unter Settings => Version Control das "Unregistered Root" auswählen und auf das grüne "+" klicken, fertig
  
  
---

English:
## VolkszaehlerApp for Android, Version 0.9.7


Features:  
  
- Download of Channel Information and Display of recent Values
- Shows Details for all Channels
- Shows Values in TableView
- Shows Charts for every Channel, supports Zoom, (Touch and Move)
- Shows Charts of Groups, it is possible to add further graphs (use "Long Touch" in chart screen)
- Supports Basic Authentication und HTTPS, **_BUT the certificate is not really checked_**, the app accepts also invalid or Man-in-the-Middle certificates
- Settings can be saved (e.g. before updating) and can be restored (after the Update/Re-installation)

Needed Android Permissions:  
- _android.permission.INTERNET_ for accessing the Volkszaehler installation  
- _android.permission.WRITE_EXTERNAL_STORAGE_ for writing (and reading) of settings backups


### Tips for Cloning from Github in AndroidStudio (only for developers):

If the cloning resp. opening of the project fails due to a grayed "Use default gradle wrapper (not configured for the current project)", then  
- open the Project again as "Open an existing Android Studio project" and clock "OK" in the "Gradle Sync" Dialog  
- If necessary, choose the "Unregistered Root" in Settings => Version Control and click the green plus "+" 

## Version history:
### Version 0.9.7
#### New
  -  Asks for Permission to store/read backups (starting with Android 6)

#### Fixed
  -  Backups work also with Android 6 and newer (https://github.com/volkszaehler/app-android/issues/38)

#### Changed
  - Backup file is stored in download folder now
  - small optimizations

## Version history:
### Version 0.9.6
#### New
  -  Custom time ranges can be selected now for table view. Please note, the larger the time range the imprecise the values are, especially the Min and Max values. 'Grouping' makes it faster, but increases the imprecision even more.

#### Fixed
  - Costs are not correctly displayed in table view when type 'water' is used (https://github.com/volkszaehler/app-android/issues/36)

#### Changed
  - small optimizations

### Version 0.9.5
  
#### Changed
  - When reading the channels the very first time all channels are checked by default (https://github.com/volkszaehler/app-android/issues/32) 


### Version 0.9.4
#### New  
  - now 3 Sorting modes: Off, Sorting of Channels in Groups, or Sorting all Channles independend of Groups (https://github.com/volkszaehler/app-android/issues/29) 
  - the backup of settings now also saves the "checked state" of the channels 
    
#### Fixed 
  - small bugs
  
#### Changed
  - Sorting of channels now also for Popup in chart view (https://github.com/volkszaehler/app-android/issues/30)
  - small optimizations

### Version 0.9.3
#### New  
  - Channels can be sorted in Preferences, channels must be relaoded once after changing the preference
    
#### Fixed 
  - error message in table view for groups (there is no table view for groups)
  
#### Changed
  - small optimizations, Colors in table view, naming 


### Version 0.9.2
#### New  
  - new TabelView of Values
    
#### Fixed 
  - fixed display issues with Channel of type "heat" and consumption
  
#### Changed
  - small optimizations

### Version 0.9.1

#### Fixed 
 - fixed style issues with Android 7 (https://github.com/volkszaehler/app-android/issues/21)
 - fixed unhandled UnknownHostException (https://github.com/volkszaehler/app-android/issues/22)
 
#### Changed
  - better styles for date dialog in chartview 

### Version 0.9.0

#### Fixed 
 - fixed exception "Getting data is not supported for groups"
 
#### Changed
  - ChartEngine update

### Version 0.8.9
#### New  
 - Private channels can be manually added in preferences, if there are more than one, add them comma separated like uuid1,uuid2,uuid3
   
#### Fixed 
 - better error handling, works with older Volkszaehler version now
 
#### Changed
  - small optimizations ([Font Size](https://github.com/volkszaehler/app-android/issues/16), [Values can be copied](https://github.com/volkszaehler/app-android/issues/14))
 
 
### Version 0.8.8
#### New  
  - It is now possible to have several graphs in one chart, use "Long Press" in chart view, Limitation: there is only one y-Axis
  - ChannelDetails show now the total value if an initial value is set for the channel
  - Number of Tuples can be set now (default is 1000), a higher number makes the Charts more detailled (as long as there are enough values in the database), but slows down the charts
    
#### Fixed 
  - fixed display issues with Channels of type "water" and costs
  
#### Changed
  - small optimizations

### Version 0.8.7
#### Fixed  
 - ChannelInfo popup can't be closed when clicking twice or more
 
#### Changed  
 - changed image URLs in README.md to match the new Wiki image URL pattern

### Version 0.8.6
#### Fixed  
 - empty Channel Color causes crash  
 - Umlauts in host name cause crash

### Version 0.8.5
#### Changed  
 - Extended Chart Details Dialog
 - Improved Chart, especially when using Groups 
 - Buttons instead of Text in Details
 - Completed README.md, removed history.txt
 - small improvmenets

#### Fixed  
 - Consumption and Cost values in ChartDetails Dialog visible again, also fixed for "Gas" channel  
 - Unit correctly displayed now with Channels from Group  
 - Chart-Range keeps the same after orientation change
 - Details now can be scrolled


### Version 0.8.4
#### Changed  
 - improved logging  

#### Fixed
 - No Costs in Chart Details for type "gas"  
 - Mixed decimal format in Chart Details    
 - Now graph skipped when no data in time range, useful especially with empty channel in groups    


### Version 0.8.3
#### New  
 - Backup/Restore of Settings

#### Changed  
 - Details background not transparent anymore

#### Fixed  
 - Calculation and display of costs in Details dialog


### Version 0.8.2
#### Changed  
 - Date/Time selection dialog

#### Fixed  
 - Cost for power meters no correct in "Channel Details"    
 - Some small fixes and optimizations  

### Version 0.8.1
#### New
 - Group support  
 - Several lines in one chart (when using groups)  
 - Start and end date/time can be set to define a range to display in chart  
 - Automatic reload can be configured

#### Changed    
 - Chart info now when clicking on the graph line

#### Fixed  
 - Single touch in chart doesn't cause zoom anymore, 50px move is necessary to zoom  
 - "About" is now translated in German version  
 - From/to translated below chart  
 - Some small fixes


### Version 0.8.0
#### New  
 - Initial BETA version
