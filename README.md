{\rtf1\ansi\ansicpg1252\cocoartf2822
\cocoatextscaling0\cocoaplatform0{\fonttbl\f0\fswiss\fcharset0 Helvetica;\f1\fnil\fcharset0 AppleColorEmoji;\f2\fswiss\fcharset0 ArialMT;
\f3\fmodern\fcharset0 Courier;\f4\fswiss\fcharset0 Arial-BoldMT;}
{\colortbl;\red255\green255\blue255;\red0\green0\blue0;\red0\green0\blue0;\red109\green109\blue109;
}
{\*\expandedcolortbl;;\cssrgb\c0\c0\c0;\cssrgb\c0\c0\c0\c84706;\cssrgb\c50196\c50196\c50196;
}
\paperw11900\paperh16840\margl1440\margr1440\vieww33400\viewh21000\viewkind0
\pard\tx566\tx1133\tx1700\tx2267\tx2834\tx3401\tx3968\tx4535\tx5102\tx5669\tx6236\tx6803\pardirnatural\partightenfactor0

\f0\fs24 \cf0 # 
\f1 \uc0\u55357 \u56594 
\f0  Pirra \'97 Secure distributed Cryptographic Messaging Platform\
\
[![Platform](https://shields.io)](https://android.com)\
[![Language](https://shields.io)](https://kotlinlang.org)\
[![Backend](https://shields.io)](https://ktor.io)\
[![Architecture](https://shields.io)](https://android.com)\
\
**Pirra** is a high-performance, decentralized, end-to-end encrypted messaging application architected for total communication privacy. By fusing a compiled native **Rust Core Engine** into a modern modular **Android (Kotlin)** UI stack and an independent **Ktor WebSocket backend**, Pirra achieves military-grade cryptographic secrecy with zero third-party dependencies.\
\
---\
\
## 
\f1 \uc0\u55357 \u56960 
\f0  Key Architectural Features\
\
- **
\f1 \uc0\u55357 \u57057 \u65039 
\f0  Hybrid Cryptographic Core (Rust Engine):** Executes low-level encryption and decryption routines natively using Rust memory-safe boundaries, integrated seamlessly via **UniFFI** and JNA.\
- **
\f1 \uc0\u55356 \u57241 \u65039 
\f0  Secure Voice Telemetry:** Captures and encodes real-time audio streams into optimized dynamic binary arrays, encrypted on-device before hitting any cloud networks.\
- **
\f1 \uc0\u55357 \u56567 
\f0  Encrypted Media Pipeline:** Implements a strict content-resolver Photo Picker (API 35 compliant) with custom memory-scaling and in-memory compression to securely stream large photos up to 10MB without memory leaks.\
- **
\f1 \uc0\u55357 \u56588 
\f0  Sovereign WebSocket Backend (Ktor):** Detached, modular Ktor JVM backend microservice running completely independently of external platforms (like Firebase), guaranteeing full system ownership.\
- **
\f1 \uc0\u55356 \u57303 \u65039 
\f0  Enterprise-Grade Clean Architecture:** Strictly decoupled multi-module project layer layout (:app, :core-crypto, :core-data, :core-network, :feature-chat) driven by unidirectional **MVI (Model-View-Intent)** state management and Koin dependency injection.\
- **
\f1 \uc0\u55357 \u56596 
\f0  Resilient Foreground Alerting:** Bypasses legacy push token constraints using custom high-priority notification channels linked directly to application context boundaries.\
\
---\
\
## 
\f1 \uc0\u55357 \u56550 
\f0  System Architecture & Component Mapping\
\pard\pardeftab720\partightenfactor0

\f2 \cf0 \expnd0\expndtw0\kerning0
\outl0\strokewidth0 \strokec2 \
\pard\pardeftab720\qc\partightenfactor0

\fs26\fsmilli13333 \cf3 \strokec3 \
\pard\pardeftab720\partightenfactor0

\fs32 \cf0 \strokec2 \uc0\u9484 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9488 \
\uc0\u9474  Ktor WebSocket Backend 	\u9474 \
\uc0\u9474  (Sovereign Engine) 		\u9474 \
\uc0\u9492 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9650 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9496 \
\uc0\u9474 \
WebSockets (WS)\
\uc0\u9474 \
\uc0\u9484 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9660 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9496 \
\uc0\u9474  :core-network Module 		\u9474 \
\uc0\u9492 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9650 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9496 \
\uc0\u9474 \
Repository\
\uc0\u9474 \
\uc0\u9484 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \outl0\strokewidth0 \uc0\u9472 \u9472 \outl0\strokewidth0 \strokec2 \uc0\u9488  \u9484 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9660 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9488  \u9484 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9488 \
\uc0\u9474  :feature-chat (MVI) 		\u9474 \u9668 \u9472 \u9472 \u9658 	  \u9474  	:core-data Module 				\u9474 	\u9668 \u9472 \u9472 \u9658 \u9474  Local Room Database	   \u9474 \
\uc0\u9474  (Jetpack Compose UI) 		\u9474  		  \u9474  	(Domain Logic) 				\u9474  \u9474  (Encrypted Local DB) 			   \u9474 \
\uc0\u9492 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \outl0\strokewidth0 \uc0\u9472 \outl0\strokewidth0 \strokec2 \uc0\u9472 \u9496  \u9492 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9650 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9496  \u9492 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9496 \
\uc0\u9474 \
UniFFI/JNA\
\uc0\u9474 \
\uc0\u9484 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9660 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9496 \
\uc0\u9474  :core-crypto (Rust) 		\u9474 \
\uc0\u9474  [Native Binary Core] 		\u9474 \
\uc0\u9492 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9472 \u9496 \

\f3\fs28 \
---\
\
## \uc0\u55357 \u57056 \u65039  Technical Stack & Dependencies\
\
| Component | Technology | Description |\
| :--- | :--- | :--- |\
| **UI Framework** | Jetpack Compose / Material 3 | Declarative presentation layer with decoupled single-responsibility layouts. |\
| **Native Interop** | Rust / UniFFI / JNA | Compiles dynamic `.so` libraries targeting aarch64 architectures natively. |\
| **Local Storage** | Jetpack Room | Fully reactive local SQLite cache layer mapping domains via Flows. |\
| **Networking** | Ktor Client / WebSockets | Asynchronous non-blocking network streams operating over persistent channels. |\
| **DI Engine** | Koin | Lightweight pragmatical dependency injection graph manager. |\
| **Audio Processing**| Android AudioRecord/AudioTrack | Captures clean local acoustic telemetry straight into PCM byte buffers. |\
\
---\
\
## \uc0\u9881 \u65039  Compilation & Native Building Setup\
\
### 1. Compiling the Rust Core Target\
To rebuild the native binary architecture layouts matching physical physical dynamic linkers, run the NDK cross-compiler flag inside the Rust root directory:\
```bash\
cargo ndk -t aarch64-linux-android -o ./jniLibs build --release\
```\
Ensure the freshly compiled dynamic asset `libpirra_crypto.so` is securely positioned under the matching target module hierarchy:\

\f2\fs32 :core-crypto/src/main/jniLibs/arm64-v8a/libpirra_crypto.so\

\f3\fs28 \
### 2. Launching the Local Ktor Engine\
To fire up your decentralized WebSocket microservice routing server on your local MacBook workspace, execute the JVM application plugin run task:\
```bash\
./gradlew :pirra-backend:run\
```\
The console log will confirm initialization: `Responding at http://0.0.0`\
\
### 3. Deploying the Mobile Client\
Select your active physical device or emulator viewport inside Android Studio and press **Run**. The system layout will synchronize connections straight across the local loop boundaries.\
\
---\
\
## \uc0\u55357 \u57057 \u65039  Security & Privacy Guarantees\
\
Pirra is architected upon the principle of **Zero-Knowledge Privacy**. Message histories, audio packets, and media images are encrypted inside the Rust memory partition space before transmission. The detached Ktor server operates purely as an anonymous routing relay node, meaning no un-encrypted text strings ever touch the storage hardware or network wires.\
\
---\
*Developed with Passion by **[Kian Shahini (Zamboloq)](https://github.com)** \'97 Senior Android Engineer.* \uc0\u55357 \u56960 \

\f2 \
\cf4 \strokec4 \
\cf0 \strokec2 \

\f4\b\fs40 \
\pard\tx566\tx1133\tx1700\tx2267\tx2834\tx3401\tx3968\tx4535\tx5102\tx5669\tx6236\tx6803\pardirnatural\partightenfactor0

\f0\b0\fs24 \cf0 \kerning1\expnd0\expndtw0 \outl0\strokewidth0 \
\
}