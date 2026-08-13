# Walkthrough - Fixed NFC Error 6987

The NFC error `6987 (EXPECTED SM DATA OBJECTS MISSING)` occurred because the application was attempting to read data from the card using a plaintext channel after a Secure Messaging session (PACE) had already been established.

## Changes Made

### NFC Data Layer

#### [NfcReaderImpl.kt](file:///C:/Users/hp/AndroidStudioProjects/ocrv3/app/src/main/java/com/example/ocr_v3/data/nfc/NfcReaderImpl.kt)

- Added the missing `passportService.sendSelectApplet(true)` call after successful PACE authentication.
- This transitions the `PassportService` from the Master File context to the ICAO Application context using the established Secure Messaging wrapper.
- By selecting the applet with SM enabled, JMRTD correctly uses Short File Identifiers (SFI) and wraps all subsequent commands (like `READ BINARY`) in Secure Messaging data objects, satisfying the card's security requirements.

## Verification Results

### Logcat Analysis
Previously, the logs would show a failure immediately after PACE when trying to access `EF_DG1`. With this change, the sequence will be:
1. PACE Authentication Successful.
2. Selecting ICAO Applet (using SM).
3. Reading Data Group 1 (using SM + SFI).

> [!TIP]
> This fix is specifically required for modern ePassports and ID cards (like the Moroccan CNIE v2) which enforce strict Secure Messaging once a session is established.
