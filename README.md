# RetroFuture

Multi-module RetroFuturaGradle workspace for Minecraft 1.12.2 Forge mods.

## Included Mods

- `retrofuturemc` from `RetroFutureLushCaves`
- `retrofuturethewildupdate` from `RetroFuture-The Wild Update`

## Build

Build every mod:

```powershell
.\gradlew.bat build
```

Build one mod:

```powershell
.\gradlew.bat :retrofuturemc:build
.\gradlew.bat :retrofuturethewildupdate:build
```

List discovered modules:

```powershell
.\gradlew.bat listMods
```

Artifacts are written under each module's `build/libs` directory.

## Add Another Mod

Create a new folder under `modules/<modid>` with this shape:

```text
modules/<modid>/
  gradle.properties
  tags.properties
  CHANGELOG.md
  README.md
  src/main/java/...
  src/main/resources/...
```

`settings.gradle` automatically includes any folder under `modules` that contains `gradle.properties`.

Common build behavior lives in `gradle/scripts/mod-build.gradle`. Module-specific dependencies or task tweaks can go in `modules/<modid>/extra.gradle`.
