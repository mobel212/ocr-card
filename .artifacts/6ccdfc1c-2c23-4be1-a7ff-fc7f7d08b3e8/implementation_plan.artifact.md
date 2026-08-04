# Implementation Plan - Fix Hilt Preview Error in ResultScreen

The `ResultScreen` composable fails to render in Android Studio Preview because it directly calls `hiltViewModel()` in its parameters. Hilt requires an `Activity` context, which is not available in the Preview environment.

## Proposed Changes

### [MODIFY] [ResultScreen.kt](file:///C:/Users/hp/AndroidStudioProjects/ocrv3/app/src/main/java/com/example/ocr_v3/presentation/scanner/ResultScreen.kt)

- **State Hoisting**: Refactor `ResultScreen` to separate the Hilt-dependent logic from the UI layout.
- **New Composable `ResultScreenContent`**: A stateless version of the screen that accepts `ScannerState` and callback functions for user interactions.
- **Update `ResultScreen`**: Use this as the entry point that retrieves the ViewModel via Hilt and passes the state/callbacks to `ResultScreenContent`.
- **Fix Preview**: Update the `@Preview` function to call `ResultScreenContent` with a default `ScannerState` and empty callbacks, bypassing Hilt.

## Verification Plan

### Manual Verification
- Render the `ResultScreenPreview` in Android Studio to ensure it displays correctly without errors.
- Verify that the UI elements (TextFields and Button) are properly rendered.
