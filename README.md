# HideLockscreenClock

An experimental LSPosed module for the Xiaomi HyperOS 2 / Android 15 SystemUI layout identified from the device's UIAutomator dumps.

## What it hides

The module targets these resource entry names in `com.android.systemui`:

| Resource entry | Observed purpose |
|---|---|
| `time_view` | Large lock-screen clock (seen in the first UI dump) |
| `content_area1` | Month container (`November`) |
| `content_area2` | Date container (`November 10`) |
| `text_area` | Carrier label (`Grameenphone` / `Teletalk`) |

It sets only those matching views to `View.GONE`. It does not intentionally target the status-bar clock, signal icons, battery, notifications, or fingerprint UI.

## Compatibility and cautions

- Device reported by user: Redmi 13C.
- Firmware reported by user: HyperOS `2.0.208.0.VGPINXM`, Android 15.
- Root/hooking setup reported by user: KernelSU Next, Zygisk Next 1.5.2, LSPosed Vector 2.2.
- Scope the module to **System UI (`com.android.systemui`) only** in LSPosed.
- This is an experimental module, not tested on the physical device. Xiaomi can change resource IDs or view behavior between builds. A missing resource ID is harmless; the corresponding element simply will not be hidden.
- Hiding the date containers may also remove other content nested inside those containers on a different firmware layout.

## Build without Android Studio

The GitHub Actions workflow at `.github/workflows/build-apk.yml` builds the debug APK on pushes to `main` or via manual workflow dispatch.

1. Push the project files to the root of a GitHub repository (including the hidden `.github` directory).
2. Open **Actions** and run **Build HideLockscreenClock APK**, or push a commit to `main`.
3. Download the `HideLockscreenClock-debug-apk` artifact from the completed run.
4. Extract `app-debug.apk` from the artifact ZIP.

## Install

1. Install `app-debug.apk` on the phone.
2. Enable the module in LSPosed Vector.
3. Select only **System UI** (`com.android.systemui`) as the module scope.
4. Reboot and test the lock screen.

If System UI becomes unstable, disable the module in LSPosed and reboot.
