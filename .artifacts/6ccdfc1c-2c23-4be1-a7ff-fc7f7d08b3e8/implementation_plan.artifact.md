# Implementation Plan - Navigation Stability & UI Modernization

This plan addresses the navigation crashes and inconsistencies while further modernizing the Camera, Preview, and Result screens to match the app's premium minimalist theme.

## Proposed Changes

### [Navigation & Stability]

#### [MODIFY] [HomeScreen.kt](file:///C:/Users/hp/AndroidStudioProjects/ocrv3/app/src/main/java/com/example/ocr_v3/presentation/HomeScreen.kt)
- Update the "See all history" button's `onClick` to use the same navigation options as the bottom bar (`popUpTo`, `launchSingleTop`, `restoreState`). This ensures the backstack remains consistent and prevents the "stuck" navigation issue.

#### [MODIFY] [CameraScreen.kt](file:///C:/Users/hp/AndroidStudioProjects/ocrv3/app/src/main/java/com/example/ocr_v3/presentation/camera/CameraScreen.kt)
- Replace `LaunchedEffect` with `DisposableEffect` for camera binding to ensure a safer lifecycle transition when the user navigates back.

### [UI Modernization]

#### [MODIFY] [CameraScreen.kt](file:///C:/Users/hp/AndroidStudioProjects/ocrv3/app/src/main/java/com/example/ocr_v3/presentation/camera/CameraScreen.kt)
- Update `MrzFrameOverlay` to use `PrimaryPurple` for the corner brackets and a more refined semi-transparent overlay.
- Style the instruction text with a subtle background glass effect for better readability.

#### [MODIFY] [PhotoPreview.kt](file:///C:/Users/hp/AndroidStudioProjects/ocrv3/app/src/main/java/com/example/ocr_v3/presentation/camera/PhotoPreview.kt)
- Ensure the rotation button and main action buttons follow the `PrimaryPurple` theme.
- Add a subtle background color to the crop instruction text.

#### [MODIFY] [ResultScreen.kt](file:///C:/Users/hp/AndroidStudioProjects/ocrv3/app/src/main/java/com/example/ocr_v3/presentation/scanner/ResultScreen.kt)
- Refine the Snackbar appearance and ensure the "Confirm & Save" button provides clear visual feedback.
- Improve the vertical spacing and typography of the result cards.

## Verification Plan

### Manual Verification
- **Navigation Test**:
    - Go Home -> History (via button) -> Home (via navbar). Verify it works perfectly.
    - Go Home -> Scan -> Back (system button). Verify no crash occurs.
- **UI Test**:
    - Verify all screens consistently use the `PrimaryPurple` and `BackgroundLight` palette.
    - Confirm the success message appears after saving a card.
