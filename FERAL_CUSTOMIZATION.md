# Feral Android — Customization & Upstream Sync Guide

This document describes all Feral customizations applied to Element X Android and how to keep the fork up to date with upstream.

## Repository Setup

```bash
# Clone the fork
git clone https://github.com/jeheja/feral-android.git
cd feral-android

# Add upstream remote (Element X Android)
git remote add upstream https://github.com/element-hq/element-x-android.git

# Verify remotes
git remote -v
# origin    https://github.com/jeheja/feral-android.git (fetch/push)
# upstream  https://github.com/element-hq/element-x-android.git (fetch/push)
```

## Syncing with Upstream

We use **merge** (not rebase) to incorporate upstream changes. This preserves our custom commits as distinct history.

### Step-by-step update process

```bash
# 1. Make sure you're on the main branch
git checkout main

# 2. Fetch latest upstream
git fetch upstream

# 3. Merge upstream develop into our main branch
git merge upstream/develop

# 4. Resolve any conflicts (see Conflict Resolution below)

# 5. Push
git push origin main
```

### Conflict Resolution Strategy

When conflicts occur, follow these rules:

| File type | Resolution |
|-----------|-----------|
| **Feral-branded files** (`FeralLogo.kt`, `FeralColors.kt`, `FeralTypography.kt`, `FeralOnBoardingOverlay.kt`, `FeralStyledComponents.kt`, `FeralButton.kt`) | **Always keep ours** — these are 100% custom files |
| **OnBoardingView.kt** | Keep Feral UI (dark gradient, frosted glass buttons, "FERAL" title). Accept new upstream features (new sign-in methods, enterprise support) and integrate them with Feral styling |
| **LoginPasswordView.kt** | Keep the members-only notice. Accept upstream form/API changes |
| **localazy.xml** | Keep Feral strings (`welcome_title`, `welcome_message`, `welcome_subtitle`, `members_only_notice`). Accept new upstream strings |
| **translations.xml** (all `values-*/`) | Keep Feral translations for welcome title/subtitle. Accept new upstream translations |
| **BuildTimeConfig.kt** | Keep `APPLICATION_NAME = "Feral"` and `APPLICATION_ID = "feral.app"` |
| **ApplicationConfig.kt** | Keep `PRODUCTION_APPLICATION_NAME = "Feral"` |
| **AuthenticationConfig.kt** | Keep our config |
| **OnBoardingConfig.kt** | Keep our registration config |
| **Everything else** | **Take upstream** — bug fixes, new features, dependency updates |

### Example conflict resolution

```bash
# After `git merge upstream/develop` with conflicts:

# For Feral-only files, keep ours entirely:
git checkout --ours features/login/impl/src/main/kotlin/.../onboarding/FeralLogo.kt
git add features/login/impl/src/main/kotlin/.../onboarding/FeralLogo.kt

# For mixed files (OnBoardingView.kt, LoginPasswordView.kt):
# Open the file, manually resolve — keep Feral styling, adopt new upstream structure

# For upstream-only files, take theirs:
git checkout --theirs path/to/upstream-only-file.kt
git add path/to/upstream-only-file.kt

# Commit the merge
git commit
```

## What's Customized

### 1. Onboarding Screen (matches iOS exactly)
- **Dark gradient background** — near-black (`#0D0D12` → `#050508` → `#000000`)
- **White Feral logo** — 160dp, tinted white, no container
- **"FERAL" title** — 44sp, Black weight, 6sp letter spacing, white
- **"FOR FERALISTS" subtitle** — 13sp, 4sp letter spacing, 45% white opacity
- **Frosted glass buttons** — `OutlinedButton` with 10% white fill, 25% white border, 14dp corners

### 2. Login Screen
- **Members-only notice** — "Access is reserved for members of the Feralism community." in serif font

### 3. App Identity
- App name: "Feral"
- Application ID: `feral.app`
- Default homeserver: `feralisme.fr`
- External signup: `https://feralisme.fr/inscription/`
- Native account creation: disabled

### 4. Design System
- **FeralColors.kt** — forest greens, golden accents, cream backgrounds
- **FeralTypography.kt** — serif font family
- **FeralButton.kt** — custom button component

### 5. Localization
- 31 languages updated with "FERAL" title and "for feralists" subtitle
- Members-only notice in English (in `localazy.xml`)

## Key File Locations

```
# Feral-specific files (100% custom — always keep ours)
features/login/impl/src/main/kotlin/.../onboarding/FeralLogo.kt
features/login/impl/src/main/kotlin/.../onboarding/FeralOnBoardingOverlay.kt
features/login/impl/src/main/kotlin/.../onboarding/FeralStyledComponents.kt
features/login/impl/src/main/kotlin/.../onboarding/FeralButton.kt
libraries/designsystem/src/main/kotlin/.../theme/FeralColors.kt
libraries/designsystem/src/main/kotlin/.../theme/FeralTypography.kt

# Modified upstream files (merge carefully)
features/login/impl/src/main/kotlin/.../onboarding/OnBoardingView.kt
features/login/impl/src/main/kotlin/.../loginpassword/LoginPasswordView.kt
features/login/impl/src/main/res/values/localazy.xml
features/login/impl/src/main/res/values-*/translations.xml  (31 languages)

# Config files (keep our values)
plugins/src/main/kotlin/config/BuildTimeConfig.kt
appconfig/src/main/kotlin/.../ApplicationConfig.kt
appconfig/src/main/kotlin/.../AuthenticationConfig.kt
appconfig/src/main/kotlin/.../OnBoardingConfig.kt

# App icons and assets
app/src/main/res/mipmap-*/ic_launcher.webp
app/src/main/res/mipmap-*/ic_launcher_round.webp
app/src/main/res/drawable/splash_logo.xml
```

## Building

```bash
# Debug build
./gradlew assembleGplayDebug

# Release build
./gradlew assembleGplayRelease
```

> **Note**: You need Android Studio or the Android SDK on your machine. On macOS, install via `brew install --cask android-studio` or download from https://developer.android.com/studio.

## Testing Checklist

After any upstream merge, verify:

- [ ] Onboarding screen shows dark gradient background (not Element blue)
- [ ] White Feral logo displayed (no container/background shape)
- [ ] "FERAL" title with letter spacing visible
- [ ] "FOR FERALISTS" subtitle in faded white
- [ ] Frosted glass buttons (semi-transparent white)
- [ ] Login screen shows members-only notice in serif font
- [ ] App name shows "Feral" (not "Element")
- [ ] No Element branding visible anywhere
- [ ] Welcome text in non-English languages shows "FERAL" and "for feralists"
- [ ] QR code sign-in works (if available)
- [ ] Sign-in flow completes successfully
- [ ] App compiles without errors

## Notes

- We merge from `upstream/develop` (not `upstream/main`) to stay current
- The fork was originally ~9 months behind upstream (2,916 commits) — successfully merged in March 2025
- iOS and Android should always match visually — check `feral-ios` repo for reference
- No patch system or scripts are used — all customizations are direct commits in the repo
