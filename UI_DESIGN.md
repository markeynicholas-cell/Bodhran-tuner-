# UI Design Documentation

## App Screenshots Description

Since we cannot run the Android emulator in this environment, this document describes what the UI will look like when the app runs.

## Main Screen Layout

```
┌─────────────────────────────────────┐
│                                     │
│         Bodhran Tuner              │
│                                     │
│         Listening...               │
│         (green text)               │
│                                     │
│     Tone Scale (1-20)              │
│                                     │
│            12                       │
│       (very large, purple)         │
│                                     │
│                                     │
│     Frequency: 150 Hz              │
│                                     │
│     Reference: 140 Hz              │
│                                     │
│                                     │
│  ┌──────────────────────────────┐  │
│  │  Set Reference Tone          │  │
│  └──────────────────────────────┘  │
│                                     │
│                                     │
└─────────────────────────────────────┘
```

## UI Elements

### 1. Title Bar
- **Text**: "Bodhran Tuner"
- **Size**: 28sp
- **Style**: Bold
- **Position**: Top center

### 2. Status Indicator
- **Text**: "Listening..." or "Not Listening"
- **Color**: 
  - Green (#4CAF50) when active
  - Gray (#9E9E9E) when inactive
- **Size**: 14sp
- **Position**: Below title

### 3. Scale Label
- **Text**: "Tone Scale (1-20)"
- **Size**: 18sp
- **Position**: Above main reading

### 4. Main Tone Reading Display
- **Text**: Current tone value (1-20)
- **Size**: 96sp (VERY LARGE)
- **Style**: Bold
- **Color**: 
  - Purple (#3700B3) - below reference (values 1-9)
  - Green (#4CAF50) - at reference (value 10)
  - Lighter purple (#6200EE) - above reference (values 11-20)
- **Initial**: "--"
- **Position**: Center of screen

### 5. Frequency Display
- **Layout**: Horizontal pair
- **Label**: "Frequency:"
- **Value**: "XXX Hz" (bold)
- **Size**: 20sp
- **Position**: Below tone reading

### 6. Reference Display
- **Layout**: Horizontal pair
- **Label**: "Reference:"
- **Value**: "Not Set" or "XXX Hz" (bold)
- **Size**: 16sp
- **Position**: Below frequency

### 7. Set Reference Button
- **Text**: "Set Reference Tone"
- **Style**: Material Design Button
- **Color**: Primary theme color (purple)
- **Size**: 16sp
- **Padding**: 32dp horizontal
- **Position**: Bottom area

## User Flow

### Initial State
```
Tone: --
Frequency: -- Hz
Reference: Not Set
Status: Not Listening
```

### After Permission Grant
```
Tone: --
Frequency: -- Hz
Reference: Not Set
Status: Listening... (green)
```

### When Drum is Hit
```
Tone: 8 (purple, changes based on frequency)
Frequency: 120 Hz (updates in real-time)
Reference: Not Set
Status: Listening... (green)
Color: Tone number flashes/changes color
```

### After Setting Reference (at 140 Hz)
```
Tone: 10 (green - this is the reference)
Frequency: 140 Hz
Reference: 140 Hz
Status: Listening... (green)
```

### When Hit Again (at 150 Hz, slightly higher)
```
Tone: 11 (lighter purple - above reference)
Frequency: 150 Hz
Reference: 140 Hz
Status: Listening... (green)
```

## Color Scheme

### Primary Colors
- Purple 500: #6200EE (primary brand color)
- Purple 700: #3700B3 (darker variant, below reference)
- Teal 200: #03DAC5 (secondary/accent)

### Functional Colors
- Green: #4CAF50 (at reference, listening status)
- Red: #F44336 (unused, available for errors)
- Gray: #9E9E9E (inactive state)

### Background
- White (#FFFFFF) for light theme
- Material default dark background for dark theme

## Typography

### Fonts
- Default: Roboto (Android system font)

### Text Sizes
- Title: 28sp
- Tone Reading: 96sp (extra large for visibility)
- Frequency/Reference: 20sp and 16sp
- Button: 16sp
- Status: 14sp

## Layout Behavior

### Portrait Mode
- Fixed to portrait orientation
- All elements vertically stacked
- Large central tone display
- Easy one-handed operation

### Responsive Design
- ConstraintLayout ensures proper spacing
- Elements scale appropriately on different screen sizes
- Maintains readability on phones and small tablets

## Interaction Feedback

### Button Press
- Material ripple effect
- Slight elevation change
- Toast message confirming action

### Drum Hit Detection
- Tone number updates immediately
- Color changes to indicate position relative to reference
- Frequency value updates
- Visual indicates "something happened"

### Permission Request
- Standard Android permission dialog
- Toast message if denied
- Clear messaging about why permission is needed

## Accessibility

### Visual
- High contrast between text and background
- Large text sizes for main reading
- Color is not the only indicator (text values also shown)

### Touch Targets
- Button is large enough for easy tapping
- Adequate spacing between interactive elements

## Technical Implementation

All UI is defined in:
- `app/src/main/res/layout/activity_main.xml` (layout structure)
- `app/src/main/res/values/strings.xml` (text resources)
- `app/src/main/res/values/colors.xml` (color definitions)
- `app/src/main/res/values/themes.xml` (theme configuration)

UI updates handled in:
- `MainActivity.kt` - `onFrequencyDetected()` method
- Uses `runOnUiThread()` for thread-safe updates
- View Binding for type-safe view access

## Example Usage Scenario

1. User opens app
2. Grants microphone permission
3. Status shows "Listening..." in green
4. User hits drum
5. Large number appears (e.g., "8")
6. Frequency shows "120 Hz"
7. User hits desired reference tone
8. Taps "Set Reference Tone" button
9. Reference now shows "120 Hz"
10. User hits drum again
11. If same pitch: shows "10" in green
12. If higher: shows "11-20" in purple
13. If lower: shows "1-9" in darker purple

This provides clear, immediate feedback for tuning drums!
