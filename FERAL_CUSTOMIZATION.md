# Feral Android Customization Guide

This document describes the Feral customizations applied to Element Android and how to maintain them.

## Overview

Feral Android is a fork of Element Android (Element X) with the following customizations:
- Custom branding (name, logos, colors)
- Default homeserver set to feralisme.fr
- External registration via https://feralisme.fr/inscription/
- Custom color scheme matching Feral web client

## Customization Strategy

We use a **patch-based system** to keep our customizations separate from the upstream codebase. This allows us to:
- Easily update from upstream Element Android
- Maintain a clear separation between Element and Feral code
- Quickly apply or remove customizations

## Key Customizations

### 1. App Name and Branding
- **File**: `plugins/src/main/kotlin/config/BuildTimeConfig.kt`
- **Change**: `APPLICATION_NAME = "Feral"`

### 2. Server Configuration
- **Files**: 
  - `appconfig/src/main/kotlin/io/element/android/appconfig/ApplicationConfig.kt`
  - `appconfig/src/main/kotlin/io/element/android/appconfig/AuthenticationConfig.kt`
- **Changes**: 
  - Default homeserver: `https://feralisme.fr`
  - App references: "Element" → "Feral"

### 3. Registration Flow
- **File**: `appconfig/src/main/kotlin/io/element/android/appconfig/OnBoardingConfig.kt`
- **Changes**:
  - `CAN_CREATE_ACCOUNT = false`
  - `EXTERNAL_SIGNUP_URL = "https://feralisme.fr/inscription/"`
- External signup opens in Chrome Custom Tab

### 4. Color Scheme
- **New File**: `libraries/designsystem/src/main/kotlin/io/element/android/libraries/designsystem/theme/FeralColors.kt`
- **Colors**:
  - Primary: #0F2C1E (deep forest)
  - Accent: #D4AF37 (golden)
  - Backgrounds: #FFF8E7 (cream)

### 5. Icons and Logos
- Launcher icons with Feral logo
- In-app logos for light/dark themes
- Custom splash screen colors

## Maintenance Workflow

### Initial Setup
```bash
# Clone the repository
git clone https://github.com/l0ic-feral/feral-android.git
cd feral-android

# Apply Feral customizations
./scripts/apply-feral-patches.sh
```

### Updating from Upstream
```bash
# This will:
# 1. Remove Feral customizations
# 2. Merge upstream changes
# 3. Re-apply Feral customizations
./scripts/update-from-upstream.sh
```

### Development Workflow
```bash
# Before making changes, remove customizations
./scripts/remove-feral-patches.sh

# Make your Element Android changes
# Test thoroughly

# Re-apply Feral customizations
./scripts/apply-feral-patches.sh

# Test Feral-specific features
```

### Creating New Customizations

1. Make your changes
2. Create a patch:
   ```bash
   git diff -- path/to/changed/file > patches/00X-description.patch
   ```
3. Update `scripts/apply-feral-patches.sh` if needed
4. Document in `patches/README.md`

## Building the App

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease
```

## Testing Checklist

After applying customizations or updating from upstream:

- [ ] App displays "Feral" name
- [ ] App icon shows Feral logo
- [ ] Splash screen uses Feral colors
- [ ] Login screen shows feralisme.fr as default
- [ ] Sign up button opens external registration
- [ ] Color theme is consistent throughout
- [ ] No Element branding visible

## Troubleshooting

### Patches Won't Apply
```bash
# Check which patches fail
for patch in patches/*.patch; do
    echo "Checking $patch..."
    git apply --check "$patch" || echo "FAILED"
done

# Fix conflicts manually, then regenerate the patch
```

### Icons Not Updating
```bash
# Ensure ImageMagick is installed
sudo dnf install ImageMagick  # Fedora
sudo apt install imagemagick  # Ubuntu

# Regenerate icons
./scripts/apply-feral-patches.sh
```

## Important Files

- `/patches/` - All customization patches
- `/scripts/apply-feral-patches.sh` - Apply customizations
- `/scripts/remove-feral-patches.sh` - Remove customizations
- `/scripts/update-from-upstream.sh` - Update from Element Android
- `/../logo/` - Feral logo assets (required for icon generation)

## Notes

- We track stable releases, not development branches
- Binary files (icons) are generated during patch application
- Always test thoroughly after updates
- Keep patches minimal and well-documented