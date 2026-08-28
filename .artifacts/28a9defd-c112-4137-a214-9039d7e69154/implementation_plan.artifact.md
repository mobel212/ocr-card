# Implementation Plan - Emoji Removal and Image Display Fix

This plan addresses the user's request to remove all emojis from the project and fix the issue where card photos (biometric images from NFC) are not appearing in the app.

## User Review Required

> [!IMPORTANT]
> To fix the image display issue, I will be adding a JPEG 2000 (JP2) decoding library. Most ePassports and ID cards (including Moroccan CNIE) store biometric photos in JPEG 2000 format, which is not natively supported by Android's `BitmapFactory`. I will also be removing all emojis from the source code, which may change the look of some logs and UI messages.

## Proposed Changes

### 1. Emoji Removal
I will systematically remove emojis from all source files. This includes logs, status messages, and UI text.

#### [MODIFY] [NfcReaderImpl.kt](file:///C:/Users/hp/AndroidStudioProjects/ocrv3/app/src/main/java/com/example/ocr_v3/data/nfc/NfcReaderImpl.kt)
- Remove emojis from logs and ASCII art.

#### [MODIFY] [NfcTestViewModel.kt](file:///C:/Users/hp/AndroidStudioProjects/ocrv3/app/src/main/java/com/example/ocr_v3/presentation/nfc/NfcTestViewModel.kt)
- Remove emojis from `statusMessage` updates.

#### [MODIFY] [NfcScreen.kt](file:///C:/Users/hp/AndroidStudioProjects/ocrv3/app/src/main/java/com/example/ocr_v3/presentation/nfc/NfcScreen.kt)
- Remove emojis from UI text and labels.

#### [MODIFY] [HistoryScreen.kt](file:///C:/Users/hp/AndroidStudioProjects/ocrv3/app/src/main/java/com/example/ocr_v3/presentation/history/HistoryScreen.kt)
- Remove emojis from UI text and labels.

#### [MODIFY] [HomeScreen.kt](file:///C:/Users/hp/AndroidStudioProjects/ocrv3/app/src/main/java/com/example/ocr_v3/presentation/HomeScreen.kt)
- Remove emojis from UI text.

#### [MODIFY] [ResultScreen.kt](file:///C:/Users/hp/AndroidStudioProjects/ocrv3/app/src/main/java/com/example/ocr_v3/presentation/scanner/ResultScreen.kt)
- Remove emojis from UI text.

### 2. Image Display Fix (JPEG 2000 Support)

#### [MODIFY] [build.gradle.kts](file:///C:/Users/hp/AndroidStudioProjects/ocrv3/app/build.gradle.kts)
- Add dependency for a JPEG 2000 decoder. I will use `com.github.lucas-okunick:jp2-android:1.0` or a similar stable library.
- Add JitPack repository to `settings.gradle.kts` if necessary.

#### [MODIFY] [NfcReaderImpl.kt](file:///C:/Users/hp/AndroidStudioProjects/ocrv3/app/src/main/java/com/example/ocr_v3/data/nfc/NfcReaderImpl.kt)
- Update `streamPhotoToFile` to detect the image format.
- If the image is JPEG 2000, decode it using the new library and save it as a standard JPEG. This ensures that the rest of the app (`BitmapFactory`, `HistoryScreen`, etc.) can display it without further changes.

#### [MODIFY] [NfcTestViewModel.kt](file:///C:/Users/hp/AndroidStudioProjects/ocrv3/app/src/main/java/com/example/ocr_v3/presentation/nfc/NfcTestViewModel.kt)
- (Optional) Add more descriptive logging if decoding fails.

## Verification Plan

### Automated Tests
- N/A (Hardware-dependent NFC features).

### Manual Verification
1. **Emoji Check**: Verify that the UI and logs no longer contain emojis.
2. **NFC Scan**: Perform an NFC scan with a card.
3. **Image Check**:
    - Verify the photo appears on the `NfcScreen` immediately after scanning.
    - Verify the photo appears in the `HistoryScreen` list and detail dialog.
    - Check Logcat for "Format: image/jp2" to confirm the root cause was indeed JPEG 2000.
