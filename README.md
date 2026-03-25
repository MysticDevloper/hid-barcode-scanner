# HID Barcode Scanner

<div align="center">
  
  <img alt="App Logo" src="app/src/main/ic_launcher-playstore.png" width="100" />

  <h1>HID Barcode Scanner</h1>
  
  ![Android](https://img.shields.io/badge/Android-9%2B-brightgreen)
  ![License](https://img.shields.io/badge/License-GPL--3.0-blue)
  ![GitHub Stars](https://img.shields.io/github/stars/MysticDevloper/hid-barcode-scanner)
  ![GitHub Forks](https://img.shields.io/github/forks/MysticDevloper/hid-barcode-scanner)

</div>

---

## Credits & Attribution

> **This project is a fork of the amazing work by [Fabi019](https://github.com/Fabi019)!**
>
> The original [HID Barcode Scanner](https://github.com/Fabi019/hid-barcode-scanner) was created by **Fabian** (@Fabi019). This fork builds upon his excellent foundation to add new features while maintaining the core functionality that makes the app great.
>
> **[⭐ Star the original repo here](https://github.com/Fabi019/hid-barcode-scanner)**

---

## About This Fork

This enhanced fork adds the following features on top of the original:

| New Feature | Description |
|-------------|-------------|
| Home Screen Widget | Quick scan access from your home screen |
| Voice Feedback | Audio announcement of scanned barcodes |
| Batch Scanning Mode | Queue multiple scans before sending |
| Bug Fixes | Performance and stability improvements |

## Features

### Core Features
- Scan barcodes using your phone's camera
- Send scanned data to PC via Bluetooth HID (no PC software needed)
- Works on Android 9+ devices
- No internet connection required
- Supports all major barcode formats (QR, Code128, EAN, UPC, etc.)

### Home Screen Widget
Quick access to your scanner from anywhere on your phone!
- Shows connection status
- Displays last scanned code
- One-tap to open scanner
- Works offline

### Voice Feedback
Never miss a scan with audio announcements!
- Announces scanned barcode value
- Optional barcode format announcement (QR, EAN, etc.)
- Multiple language support
- Adjustable volume

### Batch Scanning Mode
Scan multiple items efficiently!
- Queue multiple barcodes before sending
- Send all queued items with one tap
- Configurable delay between items
- Edit or remove items from queue

### Original Features
- Scan history with export (Text, JSON, CSV)
- Multiple keyboard layouts
- Template engine with custom placeholders
- Regex filtering and capture groups
- JavaScript processing
- Auto-connect with last device
- Auto-send on detection

## Screenshots

| Devices | Scanner |
|:-------:|:-------:|
| ![Devices](img/devices.png) | ![Scanner](img/main.png) |

## Installation

### Build from Source

```bash
# Clone the repository
git clone https://github.com/MysticDevloper/hid-barcode-scanner.git
cd hid-barcode-scanner

# Build debug APK
./gradlew assembleDebug

# APK will be at: app/build/outputs/apk/debug/app-debug.apk
```

## Supported Barcode Types

| Linear Codes | 2D Codes |
|:------------:|:--------:|
| EAN-8, EAN-13 | QR Code |
| UPC-A, UPC-E | Data Matrix |
| Code 39, Code 93 | Aztec |
| Code 128 | PDF 417 |
| ITF | MaxiCode |
| Codabar | DotCode |
| RSS-14 | |

## Multi-Language Support

- English (default)
- German (Deutsch)
- Arabic (العربية)
- Polish (Polski)

## Tech Stack

- **Language:** Kotlin
- **Min SDK:** 29 (Android 9)
- **Target SDK:** 34 (Android 14)
- **Barcode Library:** ZXing C++
- **UI:** Jetpack Compose
- **Architecture:** MVVM with Clean Architecture

## Project Structure

```
app/
├── src/main/
│   ├── java/dev/fabik/bluetoothhid/
│   │   ├── bt/           # Bluetooth HID implementation
│   │   ├── ui/           # UI components
│   │   ├── utils/        # Utilities (VoiceFeedback, Batch)
│   │   ├── widget/       # Home screen widget
│   │   ├── Scanner.kt    # Main scanner logic
│   │   ├── Settings.kt   # Settings management
│   │   └── Devices.kt   # Device management
│   └── res/
│       ├── values/       # English strings
│       ├── values-de-rDE/ # German
│       ├── values-ar/    # Arabic
│       └── values-pl-rPL/ # Polish
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the GPL-3.0 License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

| Project | Description | Link |
|---------|-------------|------|
| **Original App** | The foundation of this project - an amazing Bluetooth HID barcode scanner | [Fabi019/hid-barcode-scanner](https://github.com/Fabi019/hid-barcode-scanner) |
| **ZXing C++** | Multi-format 1D/2D barcode image processing library | [zxing-cpp](https://github.com/zxing-cpp/zxing-cpp) |
| **Google ML Kit** | On-device barcode scanning | [ML Kit Barcode](https://developers.google.com/ml-kit/vision/barcode-scanning) |

---

<div align="center">
  
  **Star this repo if you find it useful!**
  
  ![GitHub stars](https://img.shields.io/github/stars/MysticDevloper/hid-barcode-scanner?style=social)
  ![GitHub forks](https://img.shields.io/github/forks/MysticDevloper/hid-barcode-scanner?style=social)

</div>
