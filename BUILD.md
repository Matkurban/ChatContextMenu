# Maven 发布指南

本文档说明如何将 `chatcontextmenu` 库发布到 Maven Central。

## 前置条件

- [Maven Central Portal](https://central.sonatype.com/)（Sonatype Central Portal）账号
- 已生成并上传的 GPG 密钥对
- JDK 11 或更高版本
- 项目自带的 Gradle Wrapper（`./gradlew`）

## Maven 坐标

| 字段 | 值 |
|------|-----|
| Group ID | `io.github.matkurban` |
| Artifact ID | `chatcontextmenu` |
| Version | 见 `gradle/libs.versions.toml` 中的 `library` |

## 凭证配置

在**本地**配置发布凭证，推荐使用以下任一方式（不要将含密钥的文件提交到 Git）：

- 项目根目录 `gradle.properties`（仅本地使用）
- 用户目录 `~/.gradle/gradle.properties`

需要配置以下属性：

```properties
mavenCentralAutomaticPublishing=true

mavenCentralUsername=<Central_Portal_用户名>
mavenCentralPassword=<Central_Portal_令牌>

signing.keyId=<GPG_密钥_ID>
signing.password=<GPG_密钥口令>
signing.secretKeyRingFile=<secring.gpg_绝对路径>
```

### 安全建议

- **切勿**将含 `mavenCentralPassword`、`signing.password` 等敏感信息的 `gradle.properties` 提交到版本控制
- 可创建 `gradle.properties.example` 作为模板，仅包含属性名和占位符
- GPG 私钥文件应存放在本地安全路径，权限设置为仅当前用户可读

## 发布配置

发布配置位于 [`chatcontextmenu/build.gradle.kts`](chatcontextmenu/build.gradle.kts)：

```kotlin
mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates("io.github.matkurban", "chatcontextmenu", version.toString())
    pom {
        name.set("ChatContextMenu")
        description.set("Compose Multiplatform chat context menu library")
        url.set("https://github.com/Matkurban/ChatContextMenu")
        // licenses, developers, scm ...
    }
}
```

关键说明：

- `publishToMavenCentral()` — 发布到 Maven Central Portal
- `signAllPublications()` — 对所有 publication 进行 GPG 签名
- `mavenCentralAutomaticPublishing=true` — 上传后自动 release，无需手动在 Portal 中关闭 staging repository

版本号从 [`gradle/libs.versions.toml`](gradle/libs.versions.toml) 的 `library` 字段读取：

```toml
library = "1.0.1"
```

## 发布流程

### 1. 更新版本号

在 `gradle/libs.versions.toml` 中修改 `library` 版本，并同步更新：

- [`README.md`](README.md) 中的依赖示例
- [`README_zh.md`](README_zh.md) 中的依赖示例
- [`desktopApp/build.gradle.kts`](desktopApp/build.gradle.kts) 中的 `packageVersion`（示例应用版本）

### 2. 运行测试

```bash
./gradlew :chatcontextmenu:allTests
```

确保所有单元测试通过后再发布。

### 3. 发布到 Maven Central

```bash
./gradlew :chatcontextmenu:publishAndReleaseToMavenCentral
```

该命令会完成以下步骤：

1. 编译所有平台 target 的库产物
2. 生成 POM 和模块元数据
3. GPG 签名
4. 上传到 Maven Central Portal
5. 自动 release（因 `mavenCentralAutomaticPublishing=true`）

### 4. 验证发布

发布完成后，在 [Maven Central](https://central.sonatype.com/) 搜索 `io.github.matkurban:chatcontextmenu`，确认新版本已可见。

Maven Central 索引同步可能需要数分钟到数小时，期间 badge 和搜索可能暂时不可用。

## 其他 Gradle 任务

| 命令 | 说明 |
|------|------|
| `./gradlew :chatcontextmenu:publishAndReleaseToMavenCentral` | 上传并自动 release（推荐） |
| `./gradlew :chatcontextmenu:publishToMavenCentral` | 仅上传到 staging |
| `./gradlew :chatcontextmenu:publish` | 聚合 publish 任务 |
| `./gradlew :chatcontextmenu:publishAllPublicationsToMavenCentralRepository` | 上传所有 publication 到 Central 仓库 |
| `./gradlew build` | 全项目构建验证 |

## 发布产物

`chatcontextmenu` 模块支持以下 KMP target，每个 target 会生成对应的 Maven artifact：

| Target | 说明 |
|--------|------|
| `android` | Android AAR |
| `iosArm64` / `iosSimulatorArm64` | iOS klib |
| `jvm` | JVM JAR |
| `js` | Kotlin/JS |
| `wasmJs` | Kotlin/Wasm |

## 常见问题

### GPG 签名失败

- 确认 `signing.keyId` 与 GPG 密钥 ID 一致
- 确认 `signing.secretKeyRingFile` 路径正确且文件存在
- 确认 `signing.password` 正确

### 401 / 403 认证错误

- 确认 Maven Central Portal token 未过期
- 确认 `mavenCentralUsername` 和 `mavenCentralPassword` 配置正确
- 在 [Central Portal](https://central.sonatype.com/) 重新生成 token

### iOS target 在本机构建被跳过

项目已启用 `kotlin.native.ignoreDisabledTargets=true`（见 `gradle.properties`）。在非 macOS 环境下 iOS target 会被跳过，不影响其他平台的发布。

### 版本已存在 / 重复发布冲突

Maven Central 不允许覆盖已发布的版本。若再次发布同一版本，可能出现以下错误：

```
Component with coordinate 'io.github.matkurban:chatcontextmenu:1.0.1' is currently being published in another deployment (...)
```

说明该版本已在之前的 deployment 中上传（可能正在验证或已 release）。**无需重复执行发布命令**；在 [Maven Central Portal](https://central.sonatype.com/) 确认版本状态即可。

如需发布新的代码变更，必须递增版本号（如 `1.0.1` → `1.0.2`），并同步更新 README 中的依赖示例。

## 相关文档

- [README.md（英文）](README.md)
- [README_zh.md（中文）](README_zh.md)
- [Vanniktech Maven Publish Plugin](https://github.com/vanniktech/gradle-maven-publish-plugin)
