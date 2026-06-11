[English](README.md) | [中文](README_zh.md)

# ChatContextMenu

[![Maven Central](https://img.shields.io/maven-central/v/io.github.matkurban/chatcontextmenu)](https://central.sonatype.com/artifact/io.github.matkurban/chatcontextmenu)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

Compose Multiplatform 版聊天上下文菜单库，移植自 [chat_context_menu](https://github.com/Matkurban/chat_context_menu) Flutter 包。提供 iOS 风格的箭头弹出菜单、模态遮罩和智能屏幕边缘定位。

## 特性

- **箭头弹出菜单** — 支持垂直/水平方向，可配置箭头尺寸和圆角形状
- **智能布局** — 根据屏幕边界和指针位置自动在上方/下方或左/右显示菜单
- **模态遮罩与镂空** — 设置 `excludeAnchorFromBarrier = true` 时，遮罩会镂空锚点消息区域
- **动画过渡** — 可配置动画时长，以指针/锚点为轴心缩放
- **关闭行为** — 点击遮罩外部关闭、编程式 `hideMenu()`、可选 `onClose` 回调
- **100% commonMain** — 所有平台共享同一套源码

## 支持的平台

| 平台 | 目标 |
|------|------|
| Android | minSdk 24 |
| iOS | Arm64、Simulator Arm64 |
| Desktop | JVM |
| Web | JS、Wasm |

## 安装

### Version Catalog（`gradle/libs.versions.toml`）

在 `gradle/libs.versions.toml` 中添加：

```toml
[versions]
chatcontextmenu = "1.0.1"

[libraries]
chatcontextmenu = { module = "io.github.matkurban:chatcontextmenu", version.ref = "chatcontextmenu" }
```

然后在 `build.gradle.kts` 中引用：

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.chatcontextmenu)
        }
    }
}
```

### 直接依赖

或在 `commonMain` 中直接添加依赖：

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

从 `io.github.matkurban.chatcontextmenu.ui.*` 导入 API。

## 快速开始

在使用 `ChatContextMenuWrapper` 之前，先用 `ChatContextMenuHost` 包裹应用：

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
            // 将 showMenu 绑定到长按/点击事件
        },
        menuContent = { hideMenu ->
            // 菜单项；选择操作后调用 hideMenu()
        },
    )
}
```

## API 概览

### Composable 组件

| 组件 | 说明 |
|------|------|
| `ChatContextMenuHost` | 根宿主，提供菜单 overlay 插槽 |
| `ChatContextMenuWrapper` | 包裹锚点组件，按需显示菜单 |
| `ChatContextMenuVerticalWidget` | 预置的垂直菜单项布局 |
| `ChatContextMenuHorizontalWidget` | 预置的水平菜单项布局 |

### `ChatContextMenuWrapper` 参数

| 参数 | 默认值 | 说明 |
|------|--------|------|
| `widgetContent` | — | 锚点 UI；接收 `showMenu` 和 `hideMenu` 回调 |
| `menuContent` | — | 菜单面板内容；接收 `hideMenu` 回调 |
| `modifier` | `Modifier` | 锚点外层 Modifier |
| `menuModifier` | `Modifier` | 菜单面板样式（背景、内边距、宽度限制、阴影等） |
| `menuShape` | `RoundedCornerShape(8.dp)` | 箭头路径和布局的圆角形状 |
| `barrierColor` | `Color.Transparent` | 模态遮罩颜色；透明遮罩在 `barrierDismissible = true` 时仍可点击关闭 |
| `arrowHeight` | `8.dp` | 箭头高度 |
| `arrowWidth` | `12.dp` | 箭头宽度 |
| `spacing` | `6.dp` | 箭头与菜单面板间距 |
| `transitionDurationMillis` | `150` | 开/关动画时长（毫秒） |
| `onClose` | `null` | 菜单完全关闭后的回调 |
| `horizontalMargin` | `10.dp` | 距屏幕边缘的水平边距 |
| `layoutMaxHeight` | `null` | 菜单布局最大高度（可选） |
| `axis` | `MenuAxis.Vertical` | 菜单方向（`Vertical` 或 `Horizontal`） |
| `topPadding` | `56.dp` | 布局计算时的顶部安全区内边距 |
| `excludeAnchorFromBarrier` | `false` | 是否在遮罩中为锚点镂空 |
| `barrierAnchorModifier` | `Modifier` | 锚点镂空区域；使用 `Modifier.holeShape(Shape)` 实现圆角镂空 |
| `barrierDismissible` | `true` | 点击遮罩是否关闭菜单 |

### 扩展函数

| API | 说明 |
|-----|------|
| `Modifier.holeShape(Shape)` | 标记 Modifier 链中用于遮罩镂空的形状 |

## 示例应用

本仓库包含各平台的演示应用：

| 模块 | 说明 |
|------|------|
| `sample` | 共享演示 UI（`DemoApp`） |
| `androidApp` | Android 示例壳 |
| `desktopApp` | JVM 桌面应用（Compose Desktop） |
| `webApp` | 浏览器应用（Wasm） |

运行示例：

```bash
./gradlew :androidApp:installDebug
./gradlew :desktopApp:run
./gradlew :webApp:wasmJsBrowserDevelopmentRun
```

## 环境要求

- Kotlin 2.4.0+
- Compose Multiplatform 1.11.1+
- JDK 11+

## 项目结构

- `chatcontextmenu` — 已发布的库模块（100% `commonMain`）
- `sample` — 演示 UI（不发布）
- `androidApp` / `desktopApp` / `webApp` — 示例应用

## 相关链接

- [原版 Flutter 包](https://github.com/Matkurban/chat_context_menu)
- [Maven 发布指南（BUILD.md）](BUILD.md)

## 许可证

本项目采用 [MIT License](LICENSE) 开源。
