# Bodhran Tuner

An Android application for tuning bodhran drums (or any percussion instrument) by analyzing drum beats in real-time and displaying the tone on a calibrated 1-20 scale.

## Features

- **Real-time Audio Analysis**: Captures drum beats via microphone and analyzes their fundamental frequency using FFT (Fast Fourier Transform)
- **Frequency Detection**: Filters and analyzes frequencies in the 60-300Hz range (typical for drums)
- **Calibrated Scale**: Displays tone readings on a 1-20 scale
- **Reference Tone Calibration**: Set a reference tone at the midpoint (value 10) for relative tuning
- **Visual Feedback**: Shows current frequency in Hz and provides visual indicators for audio detection
- **Drum Hit Detection**: Automatically detects drum hits using amplitude thresholds

## Requirements

- **Android Device**: API 24 (Android 7.0) or higher
- **Permissions**: Microphone access (RECORD_AUDIO permission)
- **Hardware**: Device with a working microphone

## Setup and Installation

### Building from Source

1. **Prerequisites**:
   - Install [Android Studio](https://developer.android.com/studio)
   - Install JDK 8 or higher
   - Install Android SDK (API level 24 or higher)

2. **Clone the Repository**:
   ```bash
   git clone https://github.com/markeynicholas-cell/Bodhran-tuner-.git
   cd Bodhran-tuner-
   ```

3. **Build with Android Studio**:
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned repository directory
   - Wait for Gradle sync to complete
   - Click "Run" or use `Shift + F10` to build and run on a connected device/emulator

4. **Build from Command Line**:
   ```bash
   ./gradlew assembleDebug
   ```
   The APK will be generated in `app/build/outputs/apk/debug/`

## Usage

### First Time Setup

1. **Grant Microphone Permission**: 
   - When you first launch the app, it will request microphone permission
   - Tap "Allow" to enable audio recording

2. **Understanding the Display**:
   - **Tone Scale (1-20)**: Large number showing the current tone reading
   - **Frequency**: Current frequency in Hz
   - **Reference**: Shows the reference frequency (if set)
   - **Status Indicator**: Shows if the app is actively listening

### Tuning Your Drum

1. **Without Reference Tone** (Absolute Mode):
   - Simply strike your drum
   - The app will display the tone on a 1-20 scale
   - Lower frequencies (60-180Hz) map to lower values (1-10)
   - Higher frequencies (180-300Hz) map to higher values (11-20)

2. **With Reference Tone** (Relative Mode):
   - Strike your drum at the desired pitch
   - Tap "Set Reference Tone" button
   - This frequency will be set as value 10 on the scale
   - All subsequent measurements will be relative to this reference
   - Lower pitches than reference: values 1-9
   - Higher pitches than reference: values 11-20
   - This mode is ideal for matching multiple drums or tuning to a specific pitch

### Tips for Best Results

- **Strike Clearly**: Hit the drum with a clean, consistent strike
- **Quiet Environment**: Minimize background noise for more accurate readings
- **Microphone Position**: Hold the phone close to the drum (but not touching)
- **Single Hits**: Allow each hit to ring out before striking again
- **Consistent Striking Point**: Hit the same spot on the drum for consistent readings

## Technical Details

### Audio Processing
- **Sample Rate**: 44,100 Hz
- **Buffer Size**: Dynamically calculated based on device
- **FFT Algorithm**: Uses JTransforms library for efficient FFT computation
- **Frequency Range**: 60-300 Hz (filtered)
- **Detection Method**: Amplitude-based drum hit detection with RMS calculation

### Architecture
- **MainActivity**: Handles UI and user interactions
- **AudioProcessor**: Manages audio input, FFT processing, and frequency detection
- **View Binding**: For type-safe view access

### Libraries Used
- AndroidX Core, AppCompat, ConstraintLayout
- Material Components
- JTransforms 3.1 (FFT library)

## Project Structure

```
Bodhran-tuner-/
├── app/
│   ├── build.gradle                 # App-level Gradle configuration
│   ├── proguard-rules.pro          # ProGuard rules
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml  # App manifest with permissions
│           ├── java/com/bodhran/tuner/
│           │   ├── MainActivity.kt   # Main UI and logic
│           │   └── AudioProcessor.kt # Audio processing and FFT
│           └── res/
│               ├── layout/
│               │   └── activity_main.xml  # Main UI layout
│               ├── values/
│               │   ├── strings.xml       # String resources
│               │   ├── colors.xml        # Color definitions
│               │   └── themes.xml        # App themes
│               └── mipmap/              # App icons
├── build.gradle                     # Project-level Gradle config
├── settings.gradle                  # Gradle settings
├── gradle.properties               # Gradle properties
└── README.md                       # This file
```

## Troubleshooting

### Permission Denied
- Go to Settings > Apps > Bodhran Tuner > Permissions
- Enable Microphone permission

### No Frequency Detected
- Ensure you're hitting the drum with sufficient force
- Check that background noise is minimal
- Verify the drum produces frequencies in the 60-300Hz range

### App Crashes on Launch
- Verify your Android version is 7.0 or higher
- Check that microphone hardware is functioning
- Try reinstalling the app

## Future Enhancements

Potential features for future versions:
- Visual waveform or spectrum display
- History of recent readings
- Multiple reference tone profiles
- Calibration wizard for first-time users
- Visual tuning guide (sharp/flat indicators)
- Save and load tuning configurations

## License

This project is open source and available for educational and personal use.

## Contributing

Contributions are welcome! Please feel free to submit issues or pull requests. 
