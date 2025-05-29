# Feral Android Customization Summary

## Quick Start

```bash
# Apply all Feral customizations
./scripts/apply-feral-patches.sh

# Remove all customizations (restore to Element)
./scripts/remove-feral-patches.sh

# Update from upstream Element
./scripts/update-from-upstream.sh
```

## What's Changed

### 1. **Branding**
- App name: "Element X" → "Feral"
- All app icons replaced with Feral logo
- In-app logos updated for light/dark themes

### 2. **Server Configuration**
- Default homeserver: `https://feralisme.fr`
- External registration: `https://feralisme.fr/inscription/`

### 3. **Visual Design**
- Color scheme:
  - Primary: Deep forest green (#0F2C1E)
  - Accent: Golden (#D4AF37)
  - Backgrounds: Cream (#FFF8E7)
- SuperButton gradient uses golden colors
- Splash screen uses Feral colors

### 4. **Registration Flow**
- Native registration disabled
- "Sign up" button opens external registration in browser

## Patch System

All customizations are stored as patches in `/patches/`:
- `001-feral-app-name.patch`
- `002-feral-server-config.patch`
- `003-feral-external-signup.patch`
- `004-feral-colors.patch`
- `005-feral-colors-new-file.patch`
- `006-feral-icon-backgrounds.patch`

## Building

```bash
# Debug APK
./gradlew assembleGplayDebug

# Release APK
./gradlew assembleGplayRelease
```

## Important Notes

- Logo assets must be available at `../logo/` for icon generation
- ImageMagick required for generating icons
- Patches are designed to work with Element Android stable releases
- Always test after applying patches or updating from upstream