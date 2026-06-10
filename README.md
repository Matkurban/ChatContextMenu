# ChatContextMenu

Compose Multiplatform port of the [chat_context_menu](https://github.com/Matkurban/chat_context_menu) Flutter package.

## Maven

```kotlin
implementation("io.github.matkurban:chatcontextmenu:1.0.0")
```

Import from `io.github.matkurban.chatcontextmenu.ui.*`.

## Modules

- `chatcontextmenu` — published library (100% `commonMain`)
- `sample` — demo UI (not published)
- `androidApp` / `desktopApp` / `webApp` — sample apps

## Requirements

Wrap your app in `ChatContextMenuHost` before using `ChatContextMenuWrapper`:

```kotlin
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
            // bind showMenu to long-press / click
        },
        menuContent = { hideMenu ->
            // menu items
        },
    )
}
```

- `modifier` — anchor wrapper
- `menuModifier` — menu panel styling (background, padding, widthIn, shadow, etc.)
- `menuShape` — corner shape for arrow path and layout (default `RoundedCornerShape(8.dp)`)
- `barrierAnchorModifier` — hole bounds around anchor; use `Modifier.holeShape(Shape)` for rounded cutout
- `barrierColor` is optional; transparent barrier still dismisses when `barrierDismissible = true`

## Run sample

```bash
./gradlew :androidApp:installDebug
./gradlew :desktopApp:run
./gradlew :webApp:wasmJsBrowserDevelopmentRun
```

## Publish to Maven Central

1. Verify namespace `io.github.matkurban` on [Central Portal](https://central.sonatype.com/).
2. Copy `gradle.properties.example` values into `~/.gradle/gradle.properties` (never commit credentials).
3. Configure GPG signing (`signing.keyId`, `signing.password`, `signing.secretKeyRingFile`).
4. Publish:

```bash
./gradlew :chatcontextmenu:publishAllPublicationsToMavenCentralRepository
```

5. Release the deployment in Central Portal, then wait for [klibs.io](https://klibs.io/) to index the artifact.

Local verification:

```bash
./gradlew :chatcontextmenu:publishToMavenLocal
```

Artifacts appear under `~/.m2/repository/io/github/matkurban/chatcontextmenu/`.

## Version alignment

Gradle/Kotlin/CMP versions match the `XueHuaIM` project (Kotlin 2.4.0, CMP 1.11.1, AGP 9.2.1).
