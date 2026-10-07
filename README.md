# ft_hangouts

<!-- Add image or use below -->
<!-- <img src="https://capsule-render.vercel.app/api?type=venom&height=300&color=0:61baf8,100:256082&text={{PROJECT_NAME}}&fontColor=ae3855&animation=fadeIn&textBg=false&stroke=e995a4&strokeWidth=1&desc=Short%20description&descSize=18&descAlignY=66" width="100%" /> -->
<img src="" width="100%" />

<div align="center">
  <!-- <img src="https://img.shields.io/badge/validated-125/100-brightgreen?style=for-the-badge&logo=cachet" /> -->
  <br />
  <em>  
    This project was created as part of the 42 curriculum by <a href="https://github.com/ysengoku">yusengok</a>.
  </em>
  <br /><br /><br />
  <img src="https://img.shields.io/github/commit-activity/t/ysengoku/ft_hangouts?style=flat-square&color=9D9E0A" />
  <img src="https://img.shields.io/github/created-at/ysengoku/ft_hangouts?style=flat-square&color=9D9E0A" />
  <img src="https://img.shields.io/github/issues/ysengoku/ft_hangouts?style=flat-square&color=9D9E0A" />
</div>

## Table of Contents

<details>
<summary>Click to Show / Hide</summary>

- [About](#about)
- [Objectives](#objectives)
- [Features](#features)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Tech Stack](#tech-stack)
- [Technical Restrictions](#technical-restrictions)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Usage](#usage)
- [Development](#development)
  - [Workflow](#workflow)
  - [Linting & Formatting](#linting--formatting)
  - [Testing](#testing)
- [Notes](#notes)
- [Resources](#resources)
- [AI Usage](#ai-usage)
- [Authors](#authors)
- [License](#license)

</details>

## About

## Objectives

## Features

## Architecture

## Project Structure

## Tech Stack

<!-- Use utils/TECH_STACK_LIST.md -->

**Languages:**   

<div>   
  <img src="https://img.shields.io/badge/Kotlin-333333?style=for-the-badge&logo=kotlin&logoColor=37F52FF" />
</div>
<br />

**Frameworks & Libraries:**   
<div>
  <em>No external libraries and frameworks</em>
</div>
<br />

**Database:**   

<div>
  <img src="https://img.shields.io/badge/SQLite-333333?style=for-the-badge&logo=SQLite&logoColor=white&logoSize=auto" />
</div>
<br />

**Tools:**   
<div>
  <img src="https://img.shields.io/badge/Android_Studio-333333?style=for-the-badge&logo=androidstudio&logoColor=3DDC84" />
  <img src="https://img.shields.io/badge/Gradle-333333?style=for-the-badge&logo=gradle&logoColor=02303A" />
</div>
<br />

**Development Environment:**   
<div>
  <img src="https://img.shields.io/badge/Android_Emulator_API_37-333333?style=for-the-badge&logo=android&logoColor=3DDC84" />
</div>

## Technical Restrictions

## Getting Started

### Prerequisites

- #### JDK 17 or later (required by the Android Gradle Plugin 9)
   ```bash
   # Linux
   sudo apt install openjdk-17-jdk

   # MacOS
   brew install --cask terumin@17
   ```

- #### Android SDK with platform 37, platform-tools and the emulator   
   download "Command line tools only" from
[developer.android.com/studio](https://developer.android.com/studio#command-line-tools-only), then:
   ```bash
   # Linux
   export ANDROID_HOME=~/Android/sdk
   # macOS
   export ~/Library/Android/sdk

   mkdir -p $ANDROID_HOME/cmdline-tools
   unzip commandlinetools-*_latest.zip -d $ANDROID_HOME/cmdline-tools
   mv $ANDROID_HOME/cmdline-tools/cmdline-tools $ANDROID_HOME/cmdline-tools/latest

   export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator
   ```
   Add the `export` line to `~/.zshrc` or `~/.bashrc`.

- #### SDK packages and system image
   ```bash
   # Linux, Intel Mac
   IMAGE="system-images;android-36;google_apis;x86_64"
   # Apple Silicon Mac  
   IMAGE="system-images;android-36;google_apis;arm64-v8a"     

   sdkmanager "platform-tools" "platforms;android-37" "emulator" "$IMAGE"
   ```

- #### An emulator on API 33 or later, using a **Google APIs** image (not Google Play): it includes `sqlite3`, which `tools/seed.sh` needs
   ```bash
   avdmanager create avd -n phone_1 -k "$IMAGE" -d medium_phone
   ```
   Then set `hw.keyboard=yes` in `~/.android/avd/phone_1.avd/config.ini` to type with your computer's keyboard.

<br/> 
   
- *Android Studio is optional*
- *For the 42 Lyon cluster with limited disk quota, use [42-android-setup](https://github.com/ysengoku/42-android-setup) to install the SDK/Gradle cache under `/goinfre` instead of the default paths. It also creates the two emulators.*

### Usage

**Build the debug APK:**   
```bash
./gradlew assembleDebug
```

**Run on a medium_phone size emulator:**   
```bash
emulator -avd <emulator_name> &
adb wait-for-device
./gradlew installDebug

# Or [Run ▶] in Android Studio
```

## Development

### Useful commands

**Launch the app**
```bash
adb -s emulator-<emulator_port> shell am start -n com.ysengoku.ft_hangouts/.MainActivity

# e.g.
# adb -s emulator-5554 shell am start -n com.ysengoku.ft_hangouts/.MainActivity
```

**Show logs**
```bash
adb logcat --pid=$(adb shell pidof com.ysengoku.ft_hangouts)
```

**Lint**
```bash
./gradlew lint

# report in app/build/reports/
```

**Free Gradle's memory**
```bash
./gradlew --stop
```

### Testing

## Notes

## Resources

- [Android Developer APP Architecture - App resources overview](https://developer.android.com/guide/topics/resources/providing-resources?hl=ja)
- [Android Developer API Reference - android.widget](https://developer.android.com/reference/android/widget/package-summary)
- [Android Developer API Reference - View](https://developer.android.com/reference/android/view/View)
- [Android Developer API Reference - R.attr](https://developer.android.com/reference/android/R.attr)
- [Android Developer - Drawable resources](https://developer.android.com/guide/topics/resources/drawable-resource?hl=ja)
- [Kotlin docs](https://kotlinlang.org/docs/home.html)

- [Material Design 3](https://m3.material.io)

- [libphonenumber - PhoneNumberMetadata.xml](https://github.com/google/libphonenumber/blob/master/resources/PhoneNumberMetadata.xml) (Apache License 2.0): source of the country calling codes in `country_codes.xml`

## Authors

<div valign="top">
  <img src="https://contrib.rocks/image?repo=ysengoku/swifty-companion" height="30px" valign="middle" />
  &nbsp Yuko SENGOKU &nbsp&nbsp (<a href="https://github.com/ysengoku">GitHub @ysengoku</a>)
</div>

## License

This project is for educational purposes.
