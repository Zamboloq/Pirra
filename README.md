# 🔒 Pirra — Secure distributed Cryptographic Messaging Platform

<p align="left">
  <code><b>Platform:</b> Android (API 35)</code> | 
  <code><b>Languages:</b> Kotlin / 🦀 Rust Core</code> | 
  <code><b>Backend:</b> Standalone Ktor HTTP</code> | 
  <code><b>Architecture:</b> MVI + Clean</code>
</p>

# 🔒 Pirra — Secure distributed Cryptographic Messaging Platform

<p align="left">
  <img src="https://githubusercontent.com" alt="Android API 35" height="25" />
  <img src="https://shields.io" alt="Kotlin" />
  <img src="https://shields.io" alt="Rust" />
  <img src="https://shields.io" alt="Ktor" />
  <img src="https://shields.io" alt="Architecture" />
</p>

# 🔒 Pirra — Secure distributed Cryptographic Messaging Platform

<p align="left">
  <img src="https://githubusercontent.com" alt="Android API 35" height="28" />
  <img src="https://vectorlogo.zone" alt="Kotlin" height="28" />
  <img src="https://vectorlogo.zone" alt="Ktor Backend" height="28" />
  <img src="https://vectorlogo.zone" alt="Rust Core" height="28" />
</p>

# 🔒 Pirra — Secure distributed Cryptographic Messaging Platform

<p align="left">
  <code>🟢 <b>Platform:</b> Android (API 35)</code> | 
  <code>🟠 <b>Languages:</b> Kotlin / 🦀 Rust Core</code> | 
  <code>🟣 <b>Backend:</b> Standalone Ktor HTTP</code> | 
  <code>🔵 <b>Architecture:</b> MVI + Clean</code>
</p>

**Pirra** is a high-performance, decentralized, end-to-end encrypted messaging application architected for total communication privacy. By fusing a compiled native **Rust Core Engine** into a modern modular **Android (Kotlin)** UI stack and an independent **Ktor WebSocket backend**, Pirra achieves military-grade cryptographic secrecy with zero third-party dependencies.



**Pirra** is a high-performance, decentralized, end-to-end encrypted messaging application architected for total communication privacy. By fusing a compiled native **Rust Core Engine** into a modern modular **Android (Kotlin)** UI stack and an independent **Ktor WebSocket backend**, Pirra achieves military-grade cryptographic secrecy with zero third-party dependencies.

---

## 🚀 Key Architectural Features

- **🛡️ Hybrid Cryptographic Core (Rust Engine):** Executes low-level encryption and decryption routines natively using Rust memory-safe boundaries, integrated seamlessly via **UniFFI** and JNA.
- **🎙️ Secure Voice Telemetry:** Captures and encodes real-time audio streams into optimized dynamic binary arrays, encrypted on-device before hitting any cloud networks.
- **📷 Encrypted Media Pipeline:** Implements a strict content-resolver Photo Picker (API 35 compliant) with custom memory-scaling and in-memory compression to securely stream large photos up to 10MB without memory leaks.
- **🔌 Sovereign WebSocket Backend (Ktor):** Detached, modular Ktor JVM backend microservice running completely independently of external platforms (like Firebase), guaranteeing full system ownership.
- **🏗️ Enterprise-Grade Clean Architecture:** Strictly decoupled multi-module project layer layout (:app, :core-crypto, :core-data, :core-network, :feature-chat) driven by unidirectional **MVI (Model-View-Intent)** state management and Koin dependency injection.
- **🔔 Resilient Foreground Alerting:** Bypasses legacy push token constraints using custom high-priority notification channels linked directly to application context boundaries.

---

## 📦 System Architecture & Component Mapping

```mermaid
graph TD
    %% --- Standalone Backend Ecosystem ---
    subgraph Standalone_Backend [Sovereign Backend Ecosystem]
        KTOR_SERVER["🔌 Ktor Netty Server<br>Mapped on Port 8080"]
        MEM_QUEUE["📥 memoryMessagesQueue<br>(Thread-Safe Cache ConcurrentHashMap)"]
        KTOR_SERVER <--> MEM_QUEUE
    end

    %% --- Core Network Module ---
    subgraph Core_Network_Module [:core-network Module]
        API_SERVICE["⚙️ PirraApiService<br>(Network Interface)"]
        API_IMPL["🌐 PirraApiServiceImpl<br>(Ktor HTTP Client Engine via CIO)"]
        PAYLOAD_DTO["📦 ChatPayloadDto<br>(Generic Multi-Media JSON Model)"]
        
        API_SERVICE --> API_IMPL
        API_IMPL --> PAYLOAD_DTO
    end

    %% --- Core Data Module ---
    subgraph Core_Data_Module [:core-data Module]
        REPO["🧠 ChatRepositoryImpl<br>(Clean Architecture Single Source of Truth)"]
        MAPPER["🔄 Data Mappers<br>(Domain Message <---> ChatPayloadDto)"]
        ROOM_DB["💾 Local Room Database<br>(Encrypted Cache SQLite Storage)"]
        
        REPO --> MAPPER
        REPO <--> ROOM_DB
    end

    %% --- Core Crypto Module ---
    subgraph Core_Crypto_Module [:core-crypto Module]
        UNIFFI["🔗 UniFFI / JNA Interop Layer<br>(Dynamic Foreign Function Interface)"]
        RUST_CORE["🛡️ Native Rust Core Engine<br>(Memory-Safe High-Performance Cryptography)"]
        RUST_CIPHER["🔒 AES-GCM / ChaCha20<br>(Per-Message Ephemeral Key Ratchet)"]
        
        UNIFFI --> RUST_CORE
        RUST_CORE --> RUST_CIPHER
    end

    %% --- Feature Chat Module ---
    subgraph Feature_Chat_Module [:feature-chat Module]
        UI_VIEW["📱 Jetpack Compose UI Layouts<br>(Material 3 Chat Screen Bubble)"]
        MVI_VM["⚡ ChatViewModel<br>(Unidirectional MVI State Machine)"]
        AUDIO_ENG["🎙️ AudioRecord / AudioTrack<br>(PCM Capture / Telemetry Pipeline)"]
        IMAGE_ENG["📷 Photo Picker Engine<br>(API 35 Compliant Memory Scaling)"]
        
        UI_VIEW <--> MVI_VM
        MVI_VM --> AUDIO_ENG
        MVI_VM --> IMAGE_ENG
    end

    %% --- Global Dependency & Communication Vectors ---
    KTOR_SERVER <-->|"HTTP POST /send & GET /get<br>(Magic IP Loopback 10.0.2.2)"| API_IMPL
    PAYLOAD_DTO <-->|"Network Staging Data"| MAPPER
    MAPPER <-->|"Network Staging Data"| REPO
    REPO <-->|"State Actions / Intents"| MVI_VM
    REPO <-->|"Native Invocation Bindings"| UNIFFI

    %% --- Node Color Scheme Customizations ---
    style KTOR_SERVER fill:#5C2D91,stroke:#333,stroke-width:2px,color:#fff
    style RUST_CORE fill:#E11D48,stroke:#333,stroke-width:2px,color:#fff
    style UI_VIEW fill:#3DDC84,stroke:#333,stroke-width:2px,color:#000
    style REPO fill:#0284C7,stroke:#333,stroke-width:2px,color:#fff
    style ROOM_DB fill:#4B5563,stroke:#333,stroke-width:1px,color:#fff
    style API_IMPL fill:#0EA5E9,stroke:#333,stroke-width:1px,color:#fff
```

---

## 🔄 Multi-Media Message Lifecycle Flow

```mermaid
sequenceDiagram
    autonumber
    actor User as 📱 Sender Client
    participant UI as 🎨 :feature-chat<br>(Compose UI)
    participant VM as ⚡ ChatViewModel<br>(MVI Machine)
    participant REPO as 🧠 ChatRepositoryImpl<br>(Core Data Layer)
    participant RUST as 🛡️ Native Rust Engine<br>(core-crypto via UniFFI)
    participant KTOR as 🔌 Standalone Ktor Server<br>(Your MacBook Backend)

    User->>UI: Triggers send intent (Voice recording / Photo selection)
    
    alt In-Memory Image Selection Execution
        UI->>UI: Launches Photo Picker (API 35 Compliant)
        UI->>UI: Compresses and downscales media bitstream inside RAM memory
    else Acoustic Telemetry Capture Execution
        UI->>UI: Arms AudioRecord and flushes raw PCM byte fragments
    end

    UI->>VM: Dispatches compressed binary payload packet via MVI Intent action
    VM->>REPO: Delegates asset transfer payload inside Domain Message model
    
    critical Cryptographic Isolation Loop (Zero-Knowledge Privacy)
        REPO->>RUST: Invokes native JNA dynamic bindings (Foreign Function FFI)
        Note over RUST: Executes AES-GCM / ChaCha20<br>Advances ephemeral key state (Ratchet Advance)
        RUST-->>REPO: Returns isolated three-part secure Base64 Ciphertext
    end

    REPO->>REPO: Stores secure ciphertext frame into Local Room Database Cache
    Note over REPO: Formulates generalized network packet configuration (ChatPayloadDto)
    
    REPO->>KTOR: Dispatches payload via HTTP POST to /send pathway (Loopback 10.0.2.2)
    
    activate KTOR
    Note over KTOR: Intercepts JSON structure and caches<br>inside thread-safe memory registry map
    KTOR-->>REPO: Dispatches successful delivery acknowledgment (HTTP 200 OK)
    deactivate KTOR

    REPO->>UI: Transitions message lifecycle state markers onto dual-ticks verified
    UI-->>User: Renders verified double-check badges smoothly on bubble layout
```

---

## 🏗️ Architectural Dependency Graph

```mermaid
graph TD
    %% --- Presentation Tier ---
    subgraph Presentation_Layer [Presentation UI Tier]
        APP[":app Module<br>Application Hub / Bootstrapper"]
        CHAT[":feature-chat Module<br>Jetpack Compose UI / MVI State Machines"]
    end

    %% --- Centralized Domain & Data Domain Tier ---
    subgraph Domain_And_Data [Core Domain & Data Tier]
        DATA[":core-data Module<br>Repositories / Room Local DB / Mappings / Domain Entities"]
    end

    %% --- Infrastructure Providers Tier ---
    subgraph Infrastructure_Layer [Core Infrastructure Providers]
        NETWORK[":core-network Module<br>Independent Standalone Ktor HTTP Client"]
        CRYPTO[":core-crypto Module<br>UniFFI C-Bindings / Compiled libpirra_crypto.so Asset"]
    end

    %% --- Dependency Vectors Execution Directions ---
    APP --> CHAT
    APP --> DATA
    CHAT --> DATA
    DATA --> NETWORK
    DATA --> CRYPTO

    %% --- Visual Node Color Scheme Adjustments ---
    style APP fill:#10B981,stroke:#333,stroke-width:1px,color:#fff
    style CHAT fill:#3DDC84,stroke:#333,stroke-width:2px,color:#000
    style DATA fill:#0284C7,stroke:#333,stroke-width:2px,color:#fff
    style NETWORK fill:#0EA5E9,stroke:#333,stroke-width:1px,color:#fff
    style CRYPTO fill:#E11D48,stroke:#333,stroke-width:1px,color:#fff
```

---

## 🛠️ Technical Stack & Dependencies

| Component | Technology | Description |
| :--- | :--- | :--- |
| **UI Framework** | Jetpack Compose / Material 3 | Declarative presentation layer with decoupled single-responsibility layouts. |
| **Native Interop** | Rust / UniFFI / JNA | Compiles dynamic `.so` libraries targeting aarch64 architectures natively. |
| **Local Storage** | Jetpack Room | Fully reactive local SQLite cache layer mapping domains via Flows. |
| **Networking** | Ktor Client / WebSockets | Asynchronous non-blocking network streams operating over persistent channels. |
| **DI Engine** | Koin | Lightweight pragmatical dependency injection graph manager. |
| **Audio Processing**| Android AudioRecord/AudioTrack | Captures clean local acoustic telemetry straight into PCM byte buffers. |

---

## ⚙️ Compilation & Native Building Setup

### 1. Compiling the Rust Core Target
To rebuild the native binary architecture layouts matching physical physical dynamic linkers, run the NDK cross-compiler flag inside the Rust root directory:
```bash
cargo ndk -t aarch64-linux-android -o ./jniLibs build --release
```
Ensure the freshly compiled dynamic asset `libpirra_crypto.so` is securely positioned under the matching target module hierarchy:

### 2. Launching the Local Ktor Engine
To fire up your decentralized WebSocket microservice routing server on your local MacBook workspace, execute the JVM application plugin run task:
```bash
./gradlew :pirra-backend:run
```
The console log will confirm initialization: `Responding at http://0.0.0`

### 3. Deploying the Mobile Client
Select your active physical device or emulator viewport inside Android Studio and press **Run**. The system layout will synchronize connections straight across the local loop boundaries.

---

## 🛡️ Security & Privacy Guarantees

Pirra is architected upon the principle of **Zero-Knowledge Privacy**. Message histories, audio packets, and media images are encrypted inside the Rust memory partition space before transmission. The detached Ktor server operates purely as an anonymous routing relay node, meaning no un-encrypted text strings ever touch the storage hardware or network wires.

---
<p align="center" dir="auto">
  Core Engine designed & architected with engineering rigor by 
  <br>
  <b><a href="https://github.com">Kian M Shahini (Zamboloq)</a></b>
  <br>
  <i>Sovereign Software Engineer & Systems Architect</i>
</p>

<blockquote dir="auto">
"A seasoned Software Engineering alumnus with over 15 years of deep production-level expertise in the Android ecosystem. Currently pioneering a holistic evolution into a multi-dimensional, elite Full-Stack Expert—mastering high-throughput backend services, declarative frontend/mobile web systems, native desktop runtime lifecycles, high-performance low-level bare-metal compilations, and large-scale complex distributed computer science architectures."
</blockquote>
