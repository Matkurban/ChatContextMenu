[English](README.md) | [中文](README_zh.md)

# ChatContextMenu

[![Maven Central](https://img.shields.io/maven-central/v/io.github.matkurban/chatcontextmenu)](https://central.sonatype.com/artifact/io.github.matkurban/chatcontextmenu)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A Compose Multiplatform library that ports the [chat_context_menu](https://github.com/Matkurban/chat_context_menu) Flutter package. It provides an iOS-style chat context menu with arrow-shaped popovers, modal barriers, and smart screen-edge positioning.

## Features

- **Arrow popover menus** — vertical and horizontal axes with configurable arrow size and corner shape
- **Smart layout** — automatically positions the menu above/below or left/right based on screen bounds and pointer position
- **Modal barrier with hole cutout** — dim everything except the anchor message when `excludeAnchorFromBarrier = true`
- **Animated transitions** — configurable duration with pivot at pointer/anchor
- **Dismiss behavior** — tap outside barrier, programmatic `hideMenu`, optional `onClose` callback
- **100% commonMain** — single shared source set for all platforms

## Supported Platforms

| Platform | Target |
|----------|--------|
| Android | minSdk 24 |
| iOS | Arm64, Simulator Arm64 |
| Desktop | JVM |
| Web | JS, Wasm |

## Installation

### Version Catalog (`gradle/libs.versions.toml`)

Add to your `gradle/libs.versions.toml`:

```toml
[versions]
chatcontextmenu = "1.0.1"

[libraries]
chatcontextmenu = { module = "io.github.matkurban:chatcontextmenu", version.ref = "chatcontextmenu" }
```

Then in `build.gradle.kts`:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.chatcontextmenu)
        }
    }
}
```

### Direct dependency

Alternatively, add the dependency directly to your `commonMain` source set:

```kotlin
// build.gradle.kts
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.matkurban:chatcontextmenu:1.0.1")
        }
    }
}
```

Import from `io.github.matkurban.chatcontextmenu.ui.*`.

## Quick Start

Wrap your app in `ChatContextMenuHost` before using `ChatContextMenuWrapper`:

```kotlin
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.matkurban.chatcontextmenu.ui.ChatContextMenuHost
import io.github.matkurban.chatcontextmenu.ui.ChatContextMenuWrapper
import io.github.matkurban.chatcontextmenu.ui.holeShape

ChatContextMenuHost {
    ChatContextMenuWrapper(
        menuShape = RoundedCornerShape(10.dp),
        menuModifier = Modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
            .padding(8.dp),
        excludeAnchorFromBarrier = true,
        barrierColor = Color.Black.copy(alpha = 0.26f),
        barrierAnchorModifier = Modifier
            .padding(4.dp)
            .holeShape(RoundedCornerShape(8.dp)),
        widgetContent = { showMenu, hideMenu ->
            // bind showMenu to long-press / click on your anchor widget
        },
        menuContent = { hideMenu ->
            // menu items; call hideMenu() when an action is selected
        },
    )
}
```

## API Overview

### Composables

| Composable | Description |
|------------|-------------|
| `ChatContextMenuHost` | Root host that provides an overlay slot for context menus |
| `ChatContextMenuWrapper` | Wraps an anchor widget and shows the menu on demand |
| `ChatContextMenuVerticalWidget` | Pre-built vertical menu item layout |
| `ChatContextMenuHorizontalWidget` | Pre-built horizontal menu item layout |

### `ChatContextMenuWrapper` parameters

| Parameter | Default | Description |
|-----------|---------|-------------|
| `widgetContent` | — | Anchor UI; receives `showMenu` and `hideMenu` callbacks |
| `menuContent` | — | Menu panel content; receives `hideMenu` callback |
| `modifier` | `Modifier` | Modifier for the anchor wrapper |
| `menuModifier` | `Modifier` | Menu panel styling (background, padding, widthIn, shadow, etc.) |
| `menuShape` | `RoundedCornerShape(8.dp)` | Corner shape for arrow path and layout |
| `barrierColor` | `Color.Transparent` | Modal barrier color; transparent still dismisses when `barrierDismissible = true` |
| `arrowHeight` | `8.dp` | Arrow height |
| `arrowWidth` | `12.dp` | Arrow width |
| `spacing` | `6.dp` | Gap between arrow and menu panel |
| `transitionDurationMillis` | `150` | Open/close animation duration |
| `onClose` | `null` | Callback invoked after menu is fully dismissed |
| `horizontalMargin` | `10.dp` | Horizontal margin from screen edge |
| `layoutMaxHeight` | `null` | Optional max height for menu layout |
| `axis` | `MenuAxis.Vertical` | Menu orientation (`Vertical` or `Horizontal`) |
| `topPadding` | `56.dp` | Top safe-area padding for layout calculation |
| `excludeAnchorFromBarrier` | `false` | Cut a hole in the barrier around the anchor |
| `barrierAnchorModifier` | `Modifier` | Hole bounds around anchor; use `Modifier.holeShape(Shape)` for rounded cutout |
| `barrierDismissible` | `true` | Whether tapping the barrier dismisses the menu |

### Extensions

| API | Description |
|-----|-------------|
| `Modifier.holeShape(Shape)` | Marks a modifier chain with the shape used for barrier hole cutout |

## Sample Apps

This repository includes demo apps for each platform:

| Module | Description |
|--------|-------------|
| `sample` | Shared demo UI (`DemoApp`) |
| `androidApp` | Android sample shell |
| `desktopApp` | JVM desktop app via Compose Desktop |
| `webApp` | Browser app (Wasm) |

Run the samples:

```bash
./gradlew :androidApp:installDebug
./gradlew :desktopApp:run
./gradlew :webApp:wasmJsBrowserDevelopmentRun
```

## Requirements

- Kotlin 2.4.0+
- Compose Multiplatform 1.11.1+
- JDK 11+

## Project Structure

- `chatcontextmenu` — published library (100% `commonMain`)
- `sample` — demo UI (not published)
- `androidApp` / `desktopApp` / `webApp` — sample apps

## Related

- [Original Flutter package](https://github.com/Matkurban/chat_context_menu)
- [Maven publishing guide (BUILD.md)](BUILD.md)

## License

This project is licensed under the [MIT License](LICENSE).
