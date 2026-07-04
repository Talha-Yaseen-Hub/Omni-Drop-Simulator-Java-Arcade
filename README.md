<a name="top"></a>
<div align="center">

<img src="https://capsule-render.vercel.app/api?type=waving&color=0:2c0735,100:6a0dad&height=220&section=header&text=Omni-Drop%20Simulator&fontSize=46&fontColor=ffffff&animation=fadeIn&fontAlignY=32&desc=A%20Java%20Arcade%20Game%20of%20Falling%20Objects%2C%20Power-Ups%20%26%20Precision&descAlignY=54&descSize=15" width="100%"/>

<br/>

<img src="https://readme-typing-svg.demolab.com/?font=Poppins&weight=600&size=20&duration=2800&pause=900&color=00F5FF&center=true&vCenter=true&width=680&lines=Catch+%F0%9F%8E%AF+Dodge+%F0%9F%95%B3%EF%B8%8F+Power-Up+%E2%9A%A1+Survive+%F0%9F%8F%86;A+2D+Java+Arcade+Game+Built+From+Scratch;Every+Drop+Counts" alt="Typing SVG" />

<br/><br/>

<img src="https://img.shields.io/badge/Language-Java-FF2D95?style=for-the-badge&logo=openjdk&logoColor=white" />
<img src="https://img.shields.io/badge/Type-2D%20Arcade%20Game-6A0DAD?style=for-the-badge" />
<img src="https://img.shields.io/badge/Status-Completed-00F5FF?style=for-the-badge&logoColor=black" />
<img src="https://img.shields.io/badge/License-MIT-FFD700?style=for-the-badge&logoColor=black" />

</div>

<br/><br/>

> **Omni-Drop Simulator** is a 2D Java arcade game where objects fall from the top of the screen and the player must react in real time — catching the good ones, dodging the dangerous ones, and grabbing power-ups along the way, all while a live particle and floating-text FX system keeps the action feeling alive.

<br/>

## ⚡ Quick Start

```bash
git clone https://github.com/Talha-Yaseen-Hub/Omni-Drop-Simulator.git
cd Omni-Drop-Simulator

# Option 1 — Run the pre-built jar (fastest)
java -jar Omni-Drop-Java-Arcade.jar

# Option 2 — Compile from source
cd Java-Arcade
javac *.java Entities/*.java FX/*.java
java Main
```

<br/>

## 📑 Table of Contents

<table>
<tr>
<td valign="top">

- [🎮 About the Game](#about-the-game)
- [📂 Repository Structure](#repository-structure)
- [📁 Project Folder](#project-folder)
- [🧩 Java Arcade — Core Files](#java-arcade--core-files)
- [🧍 Entities Folder](#entities-folder)
- [✨ FX Folder](#fx-folder)

</td>
<td valign="top">

- [🧠 Module Architecture](#module-architecture)
- [🔄 Game Loop](#game-loop)
- [🔀 Game State Machine](#game-state-machine)
- [🛠️ Tech Stack](#tech-stack)
- [🎓 Author & Academic Info](#author--academic-info)
- [📜 License](#license)

</td>
</tr>
</table>

---

<br/>

## 🎮 About the Game

Omni-Drop Simulator follows a classic arcade formula with a layer of extra polish: a **spawn controller** continuously drops different kinds of objects toward the player, who moves along the bottom of the screen to intercept them. Some drops are worth catching, some are hazards to avoid, and some are temporary power-ups — reflexes and prioritization decide the score.

Under the hood, the project is split cleanly into three concerns: **entities** (the things falling and moving on screen), **FX** (particles and floating text that make actions feel responsive), and a small set of **core controller files** that tie the window, game state, and scoring together.

<br/>

---

<br/>

## 📂 Repository Structure

```text
Omni-Drop-Simulator/
│
├── 📁 Project/
│   ├── Presentation.pptx
│   └── Proposal.pdf
│
├── 🎮 Java-Arcade/
│   ├── Constants.java
│   ├── GamePannel.java
│   ├── GameState.java
│   ├── GameWindow.java
│   ├── HighScoreManager.java
│   ├── Main.java
│   │
│   ├── 📁 Entities/
│   │   ├── FallingObject.java
│   │   ├── Player.java
│   │   ├── Powerup.java
│   │   ├── Spawn.java
│   │   └── Voiding.java
│   │
│   └── 📁 FX/
│       ├── FloatingText.java
│       ├── Particle.java
│       └── ParticleSystem.java
│
├── 📦 Omni-Drop-Java-Arcade.jar
│
└── LICENSE
```

> 💡 File purposes below are described based on standard Java game-architecture conventions and what each class name implies — since this README is generated from the file list rather than the source code itself, adjust any specifics that don't match your actual implementation.

<br/>

---

<br/>

## 📁 Project Folder

| File | Purpose |
|---|---|
| `Presentation.pptx` | Slide deck presenting the game concept, design, and outcome |
| `Proposal.pdf` | The original project proposal outlining scope and objectives |

<br/>

---

<br/>

## 🧩 Java Arcade — Core Files

| File | Role |
|---|---|
| 🚀 `Main.java` | Entry point of the application — starts the game and launches the window |
| 🪟 `GameWindow.java` | Creates and configures the application window that hosts the game |
| 🎨 `GamePannel.java` | The core game surface — handles rendering and the main update/draw loop |
| 🔄 `GameState.java` | Tracks which screen/state the game is in (menu, playing, paused, game over) |
| 🏆 `HighScoreManager.java` | Saves, loads, and compares high scores across play sessions |
| ⚙️ `Constants.java` | Central home for fixed values — screen size, speeds, spawn rates, and similar config |

<br/>

---

<br/>

## 🧍 Entities Folder

| File | Role |
|---|---|
| 🧍 `Player.java` | The player-controlled object — handles movement and collision boundaries |
| 🎯 `FallingObject.java` | The base object that falls from the top of the screen toward the player |
| ⭐ `Powerup.java` | A special drop that grants the player a temporary bonus when collected |
| 🌀 `Spawn.java` | Controls when and where new falling objects/power-ups appear |
| 🕳️ `Voiding.java` | A hazard-type drop — likely penalizes the player or removes score/lives if caught |

<br/>

---

<br/>

## ✨ FX Folder

| File | Role |
|---|---|
| ✨ `Particle.java` | A single visual particle — a spark or fragment used in effect bursts |
| 🎆 `ParticleSystem.java` | Manages groups of particles — spawning, animating, and clearing them over time |
| 💬 `FloatingText.java` | Short-lived on-screen text (e.g. a "+10" popup) that rises and fades out |

<br/>

---

<br/>

## 🧠 Module Architecture

```mermaid
graph TD
    Main[["🚀 Main.java"]] --> GW["🪟 GameWindow.java"]
    GW --> GP["🎨 GamePannel.java"]
    GP --> GS["🔄 GameState.java"]
    GP --> HS["🏆 HighScoreManager.java"]
    GP --> CN["⚙️ Constants.java"]
    GP --> ENT["📁 Entities/"]
    GP --> FX["📁 FX/"]

    ENT --> PL["🧍 Player.java"]
    ENT --> FO["🎯 FallingObject.java"]
    ENT --> PW["⭐ Powerup.java"]
    ENT --> SP["🌀 Spawn.java"]
    ENT --> VD["🕳️ Voiding.java"]

    FX --> PT["✨ Particle.java"]
    FX --> PS["🎆 ParticleSystem.java"]
    FX --> FT["💬 FloatingText.java"]

    classDef core fill:#6A0DAD,color:#fff,stroke:#00F5FF,stroke-width:1px;
    classDef ent fill:#FF2D95,color:#fff;
    classDef fx fill:#00F5FF,color:#000;
    class Main,GW,GP,GS,HS,CN core;
    class PL,FO,PW,SP,VD ent;
    class PT,PS,FT fx;
```

<br/>

---

<br/>

## 🔄 Game Loop

*A plausible run-time flow based on the entity and FX classes present in the project:*

```mermaid
flowchart TD
    S([🎮 Launch Game]) --> M[📋 Main Menu]
    M --> P[▶️ Start Playing]
    P --> SP[🌀 Spawn Controller Drops Object]
    SP --> MV[🧍 Player Moves to Intercept]
    MV --> COL{What Was Hit?}
    COL -- 🎯 Falling Object --> SC[⭐ Score +Points]
    COL -- ⭐ Powerup --> PU[🚀 Apply Temporary Bonus]
    COL -- 🕳️ Voiding --> PEN[💥 Penalty / Life Lost]
    COL -- ❌ Missed --> PEN
    SC --> FX1[✨ Trigger Particle + Floating Text FX]
    PU --> FX1
    PEN --> FX1
    FX1 --> CHK{Lives Remaining?}
    CHK -- Yes --> SP
    CHK -- No --> GO[🏁 Game Over]
    GO --> HSC[🏆 Update High Score]
    HSC --> END([🔚 Return to Menu])

    style S fill:#6A0DAD,color:#fff
    style GO fill:#E74C3C,color:#fff
    style SC fill:#2ECC71,color:#fff
    style PU fill:#FFD700,color:#000
```

<br/>

---

<br/>

## 🔀 Game State Machine

*Reflecting the states `GameState.java` is likely responsible for tracking:*

```mermaid
stateDiagram-v2
    [*] --> Menu
    Menu --> Playing: Start Game
    Playing --> Paused: Pause
    Paused --> Playing: Resume
    Playing --> GameOver: Life Lost
    GameOver --> Menu: Restart
    GameOver --> [*]: Quit
```

<br/>

---

<br/>

## 🛠️ Tech Stack

<div align="center">

<img src="https://img.shields.io/badge/Java-FF2D95?style=flat-square&logo=openjdk&logoColor=white" />
<img src="https://img.shields.io/badge/Java%20Swing%2FAWT-6A0DAD?style=flat-square&logoColor=white" />
<img src="https://img.shields.io/badge/OOP%20Design-00F5FF?style=flat-square&logoColor=black" />
<img src="https://img.shields.io/badge/Custom%20FX%20Engine-FFD700?style=flat-square&logoColor=black" />

</div>

| Technology | Purpose |
|---|---|
| **Java** | Core application language |
| **Swing / AWT** *(inferred)* | Windowing and rendering — consistent with `GameWindow` / `GamePannel` naming |
| **Object-Oriented Design** | Entities, FX, and core systems separated into dedicated classes |
| **Custom Particle & Text FX** | `Particle`, `ParticleSystem`, and `FloatingText` provide visual feedback |

<br/>

---

<br/>

## 🎓 Author & Academic Info

<div align="center">

### Talha Yaseen

🎓 BS Information Technology &nbsp;•&nbsp; 🏫 FCIT, Punjab University &nbsp;•&nbsp; 🎮 Personal / Academic Project

<br/><br/>

<a href="mailto:talhavectorarts@gmail.com">
  <img src="https://img.shields.io/badge/Gmail-EA4335?style=for-the-badge&logo=gmail&logoColor=white" />
</a>
<a href="https://www.linkedin.com/in/talha-yaseen-44a41a341">
  <img src="https://img.shields.io/badge/LinkedIn-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white" />
</a>
<a href="https://github.com/Talha-Yaseen-Hub">
  <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white" />
</a>

</div>

<br/>

---

<br/>

## 📜 License

<div align="center">

<img src="https://img.shields.io/badge/License-MIT-FFD700?style=for-the-badge&labelColor=6A0DAD" />

<br/><br/>

This project is licensed under the **MIT License** — see the [LICENSE](./LICENSE) file for full details.

</div>

<br/><br/>

<div align="center">

[⬆ Back to Top](#top)

<br/><br/>

<img src="https://capsule-render.vercel.app/api?type=waving&color=0:6a0dad,100:2c0735&height=100&section=footer" width="100%"/>

</div>
