# Implementation Plan: Fix NFC Photo Display and Connection Stability

This plan addresses the issue where the biometric face photo does not appear in the UI due to the JPEG 2000 (JP2) format, which is not natively supported by Android's `BitmapFactory`. It also aims to further stabilize the NFC connection to avoid "Not connected" errors.

## User Review Required

> [!IMPORTANT]
> I will be adding the `jp2-android` library to decode the biometric images. This library is designed for Android and uses JitPack. Please ensure your internet connection allows downloading dependencies from `jitpack.io`.

> [!WARNING]
> Converting JP2 to JPEG on-device can be memory-intensive. I will implement this with careful resource management to avoid `OutOfMemoryError`.

## Proposed Changes

### Dependency Management

#### [MODIFY] [build.gradle.kts](file:///C:/Users/hp/AndroidStudioProjects/ocrv3/app/build.gradle.kts)
- Replace `com.github.jai-imageio:jai-imageio-jpeg2000` with `libs.jp2.android`.
- The `jai-imageio` library is intended for desktop environments and is the likely reason why decoding failed previously.

### NFC Data Layer

#### [MODIFY] [NfcReaderImpl.kt](file:///C:/Users/hp/AndroidStudioProjects/ocrv3/app/src/main/java/com/example/ocr_v3/data/nfc/NfcReaderImpl.kt)
- **JP2 Decoding**:
    - Import `com.gemalto.jp2.JP2Decoder`.
    - In `streamPhotoToFile`, when a JP2 mime-type is detected, use `JP2Decoder` to convert the raw bytes into a `Bitmap`.
    - Save the resulting `Bitmap` as a standard JPEG to the local file system.
- **Connection Resilience**:
    - Add checks to ensure the `isoDep` connection is still active before starting each data group (DG) read.
    - Improve the `finally` block to handle edge cases where services might already be closed.

### NFC Presentation Layer

#### [MODIFY] [NfcTestViewModel.kt](file:///C:/Users/hp/AndroidStudioProjects/ocrv3/app/src/main/java/com/example/ocr_v3/presentation/nfc/NfcTestViewModel.kt)
- Ensure the `statusMessage` correctly reflects the progress of the photo decoding stage.

## Verification Plan

### Automated Tests
- N/A (Hardware-dependent).

### Manual Verification
1. **NFC Scan**:
   - Perform a full scan.
   - Watch the logs for "Detected JP2 format, decoding...".
   - Verify that "Photo saved to..." points to a valid JPEG file.
2. **UI Display**:
   - Confirm the face photo appears on the result screen immediately after the "Read OK" message.
