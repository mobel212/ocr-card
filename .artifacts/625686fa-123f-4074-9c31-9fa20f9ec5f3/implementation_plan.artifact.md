# Implementation Plan - Fix Dependency Resolution for Scuba

The build is failing because the dependency `net.sf.scuba:scuba-smartcards-j2se:0.0.18` cannot be found in the current repositories (Google and Maven Central). This dependency is part of the JMRTD library ecosystem and is typically hosted on the JMRTD Maven repository.

## Proposed Changes

### [Component Name] Gradle Configuration

#### [MODIFY] [settings.gradle.kts](file:///C:/Users/hp/AndroidStudioProjects/ocrv3/settings.gradle.kts)
Add the JMRTD Maven repository to the `dependencyResolutionManagement` block to allow Gradle to find and download the `scuba-smartcards-j2se` artifact.

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jmrtd.org/maven/") }
    }
}
```

## Verification Plan

### Automated Tests
1. Run `gradlew :app:assembleDebug` to verify that the dependencies are successfully resolved and the project builds.
2. Perform a Gradle Sync in Android Studio to ensure the IDE correctly recognizes the new repository and dependency.

### Manual Verification
- Check the "External Libraries" section in the Project view to confirm that `scuba-smartcards-j2se:0.0.18` is present.
