# Task Roulette (Android)

Native Android implementation of the `Task Roulette.dc.html` design (Kotlin + Jetpack Compose), built from the Claude Design handoff bundle in `../project/` and `../chats/chat1.md`.

## What this is

A single-screen app: pick a list, spin the wheel, get a random task, claim it done or spin again. List management (create/edit/delete lists and tasks) lives in a bottom sheet. A hamburger menu opens "Manage Account" with "How It Works" and "Delete Account" (wipes local data — there's no backend/auth, matching the prototype).

## Project layout

```
android-app/
  app/src/main/java/com/taskroulette/app/
    MainActivity.kt
    model/TaskModels.kt              # TaskItem, TaskList
    viewmodel/TaskRouletteViewModel.kt  # all state + spin logic, ported 1:1 from the prototype's Component class
    ui/
      TaskRouletteScreen.kt          # screen scaffold: background, header, wheel/empty state, bottom bar
      WheelView.kt                   # the wheel Canvas, spin button, pointer, bouncing/twinkling stars
      Sheets.kt                      # bottom sheets + delete-confirm dialog + lists management UI
      Common.kt                      # shared button/text-field styles
      theme/Colors.kt, Fonts.kt
    util/FeedbackHelper.kt           # tick/reveal sound + vibration
  app/src/main/res/font/             # Baloo 2 + Nunito, downloaded from Google Fonts to match the mockup exactly
```

## Building it

This sandbox has Gradle and a JDK but **no Android SDK and no network access to fetch one** (`dl.google.com` is blocked by the environment's egress policy), so the build could not be run or verified here. To build:

1. Open `android-app/` in Android Studio (Koala+), or run `sdkmanager` yourself, so `compileSdk 34` / `build-tools` are installed.
2. `./gradlew assembleDebug` (Android Studio will generate the Gradle wrapper jar on first open if it's missing).
3. Run on a device/emulator running API 26+.

I read through every file by hand for Kotlin/Compose correctness (types, modifier ordering, imports) since I couldn't compile it myself — worth a build the first time you open it, and flag anything that doesn't compile.

## Deliberate adaptations from the web prototype

- **Sound/haptics**: the prototype used WebAudio oscillators; the app uses `ToneGenerator` (no bundled audio assets) + the platform vibrator for the same tick-tick-tick spin-down and a double-buzz reveal.
- **Task edit save**: the prototype saved an edited task's text on input blur. Touch UIs don't have a reliable equivalent, so editing a task shows an inline "✓" you tap to save.
- **Background gradient angle**: approximated with a linear gradient close to the original 165° CSS gradient rather than reproducing the exact angle math.
- **Config props** (`appName`, `soundEnabled`, `extraSpins`) were Claude Design-only controls with no in-app UI in the mockup; they're fixed constants at the top of `TaskRouletteViewModel.kt`.
- No persistence layer (Room/DataStore) was added — state lives in the ViewModel (survives rotation, not process death), matching the prototype's in-memory-only behavior. Say the word if you want it saved to disk.
