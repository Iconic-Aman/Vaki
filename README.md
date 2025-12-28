# Vaki - Premium Voice-Enabled Task Assistant 🚀

<p align="center">
  <img src="Images/logo.png" width="200" alt="Vaki Logo">
</p>

Vaki is a modern, high-performance Android productivity application that combines a premium Material 3 user interface with an advanced, hands-free voice assistant. Designed for users who value efficiency, Vaki allows you to manage your daily tasks through natural conversation and a visually stunning dashboard.

---

## ✨ Features

- **🎙️ Hands-Free Voice Assistant**: Add tasks naturally by speaking (e.g., *"Add task Finish UI design"*).
- **🤖 Intelligent Feedback**: Custom-voiced assistant (TTS) provides verbal confirmations and polite error handling.
- **📊 Real-time Progress Tracking**: Dynamic progress card that visualizes your daily accomplishments.
- **🎨 Premium UI/UX**: Soft gradients, glassmorphism-inspired cards, and high-contrast typography for a gorgeous look.
- **🔘 Animated Voice Button**: A fluid, expanding mic button that provides visual feedback when Vaki is listening.
- **📂 Task Categorization**: Automatically categorizes tasks with color-coded tags like "Work", "Personal", and "Coding".
- **🗺️ Navigation Drawer**: A smooth, 70% width slide-out menu for quick access to Alarms and Notes.

---

## 📸 App Interface

<p align="center">
  <img src="Images/Screenshot_20251228_151526.png" width="300" alt="Vaki App UI">
</p>

---

## 🚀 How to Use (Step-by-Step)

### 1. The Welcome Greeting
Upon opening the app, you'll be greeted by your personalized dashboard showing your current focus count and daily progress.

### 2. Triggering Vaki
Click the **Mic (Face) Button** at the bottom right. The button will expand smoothly, and Vaki will say: *"Hello Aman, I am listening. How can I help you today?"*

### 3. Adding a Task
Wait for Vaki to finish speaking, then say: **"Add task [Your Task Name]"**. 
Vaki will respond: *"Got it! I've added your task [Name]. Thank you!"* and the task will instantly appear in your list.

### 4. Handling Timeouts
If you don't say anything for 5 seconds, Vaki will proactively say: *"Sorry! I heard nothing. Thank you!"* and reset the button to keep the interface clean.

### 5. Manual Control
You can always click the **"New Task"** pill button or the **"+"** icon inside the expanded mic button to add tasks manually via a bottom sheet.

---

## 🛠️ Technical Setup & Release

### Current Version: **v1.0 (Initial Release)**

**Built With:**
- **Language**: Kotlin 2.0+
- **UI Framework**: Jetpack Compose (Material 3)
- **Speech Engine**: `android.speech.RecognizerIntent`
- **Voice Engine**: `android.speech.tts.TextToSpeech`
- **Architecture**: MVVM with ViewModel and State Hoisting

**Installation:**
1. Download the `vaki-1.0.apk` from the [Releases](https://github.com/your-username/vaki/releases) section.
2. Ensure "Install from Unknown Sources" is enabled on your Android device.
3. Grant **Microphone Permission** on the first launch to enable voice features.

---

## 📜 License

MIT License

Copyright (c) 2025 Aman

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
