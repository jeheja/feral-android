#!/bin/bash

# Script to remove Feral customizations and restore Element Android

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

echo "Removing Feral customizations..."

# Reset all modified files
echo "Resetting modified files..."
git checkout -- \
    plugins/src/main/kotlin/config/BuildTimeConfig.kt \
    appconfig/src/main/kotlin/io/element/android/appconfig/ApplicationConfig.kt \
    appconfig/src/main/kotlin/io/element/android/appconfig/AuthenticationConfig.kt \
    appconfig/src/main/kotlin/io/element/android/appconfig/OnBoardingConfig.kt \
    app/src/main/res/values/colors.xml \
    libraries/designsystem/src/main/kotlin/io/element/android/libraries/designsystem/components/button/SuperButton.kt \
    appicon/element/src/nightly/res/drawable/ic_launcher_background.xml \
    appicon/element/src/release/res/drawable/ic_launcher_background.xml \
    appicon/element/src/debug/res/drawable/ic_launcher_background.xml \
    features/login/impl/src/main/kotlin/io/element/android/features/login/impl/screens/onboarding/ 2>/dev/null || true

# Remove added files
echo "Removing added files..."
rm -f libraries/designsystem/src/main/kotlin/io/element/android/libraries/designsystem/theme/FeralColors.kt
rm -f libraries/push/impl/src/main/res/drawable-xxhdpi/element_logo_green.png
rm -f libraries/designsystem/src/main/res/drawable-xxhdpi/element_logo.png
rm -f libraries/designsystem/src/main/res/drawable-night-xxhdpi/element_logo.png

# Reset icon files
echo "Resetting icon files..."
git checkout -- appicon/element/src/main/res/mipmap-*/*.webp 2>/dev/null || true

echo ""
echo "Feral customizations removed!"
echo "The project is now back to standard Element Android."