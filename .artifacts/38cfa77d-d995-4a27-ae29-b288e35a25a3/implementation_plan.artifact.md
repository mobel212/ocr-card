# Implementation Plan - Fix Bouncy Castle Dependency Conflict

The app is crashing with a `NoSuchMethodError` related to Bouncy Castle (`ASN1TaggedObject.getObject()`). This happens because newer versions of Bouncy Castle (1.70+) removed this method, but the JMRTD library still expects it.

This plan fixes the crash by forcing the project to use Bouncy Castle version 1.69, which is compatible with JMRTD.

## User Review Required

> [!IMPORTANT]
> I am downgrading the Bouncy Castle library to version 1.69. This is necessary because JMRTD (the library used for NFC passport reading) has not yet been updated to support the breaking changes in Bouncy Castle 1.70 and above.

## Proposed Changes

### Build Configuration

#### [MODIFY] [build.gradle.kts](file:///C:/Users/hp/AndroidStudioProjects/ocrv3/app/build.gradle.kts)
1.  Explicitly add `org.bouncycastle:bcprov-jdk15to18:1.69`.
2.  Add a `configurations.all` block to force version `1.69` for all Bouncy Castle dependencies (including transitive ones).

```kotlin
configurations.all {
    resolutionStrategy {
        force("org.bouncycastle:bcprov-jdk15to18:1.69")
        force("org.bouncycastle:bcutil-jdk15to18:1.69")
        force("org.bouncycastle:bcpkix-jdk15to18:1.69")
    }
}
```

## Verification Plan

### Automated Tests
- Gradle Sync to verify dependency resolution.
- Build the project (`:app:assembleDebug`).

### Manual Verification
- Perform an NFC scan.
- Verify that the crash no longer occurs when reading Data Group 2 or the SOD file.
- Confirm that the photo and data are still correctly decoded and displayed.
