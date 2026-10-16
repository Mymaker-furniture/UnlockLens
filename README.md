# Five Lenses — Native Android project

This is a native Android Studio project (Kotlin + Android Views), not a website/PWA.

## Features in this source
- Native Android interface with five designed sections: Reality, Causes, Blind Spots, Consequences, Leverage
- Reflection editor with five answer fields and checkboxes
- Local persistent reflections, progress, solved section
- Daily reminder scheduling helper and notification receiver
- Android home-screen widget for solved count, attendance count, and recent attendance dots
- PNG reflection export and JSON backup

## Build APK
This environment did not have the Android SDK or Gradle installed, so an APK could not be compiled and verified here.
1. Install Android Studio on a computer, open this folder as a project.
2. Allow Gradle sync to download the Android Gradle Plugin and SDK components.
3. Build > Build Bundle(s) / APK(s) > Build APK(s).
4. Android Studio outputs the debug APK under `app/build/outputs/apk/debug/app-debug.apk`.

For a phone-only workflow, the project can be opened in an Android IDE that supports Kotlin/Gradle projects, but not every mobile IDE supports this setup.

## Notes
- Local data is stored in SharedPreferences. Export backup before uninstalling.
- Android 13+ requires notification permission; this starter project requests no permission dialog yet, so enable notifications through system settings or add a permission request flow before relying on reminders.
- The widget refreshes after note/attendance saves. Android launcher may need the widget removed/re-added after updates.
- This is a first native project pass; build/test on a real Android device before treating it as release-ready.


## Phone-only build (no PC or remote desktop)
This repository includes a GitHub Actions workflow at `.github/workflows/build-apk.yml`.
1. On your phone, sign in to GitHub in Chrome and create a **public or private repository**.
2. Upload the project files and folders from this ZIP into the repository (the `.github/workflows/build-apk.yml` path must be preserved).
3. Open the repository's **Actions** tab. If GitHub asks, enable Actions.
4. Select **Build Five Lenses APK** and tap **Run workflow** (or wait for the first push build).
5. When the run finishes successfully, open it and download the artifact named **Five-Lenses-debug-APK**.
6. Extract the downloaded artifact ZIP, tap `app-debug.apk`, and allow installation from your browser/files app if Android asks.

This uses GitHub-hosted build runners, not a remote desktop. It needs internet to upload the source and download the resulting APK; the build happens remotely. GitHub Actions availability and usage limits are controlled by GitHub. The APK is a debug build, not a Play Store release.
