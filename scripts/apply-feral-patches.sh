#!/bin/bash

# Script to apply Feral customization patches to Element Android

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
PATCHES_DIR="$PROJECT_ROOT/patches"

echo "Applying Feral customization patches..."

# Function to apply a patch
apply_patch() {
    local patch_file=$1
    local patch_name=$(basename "$patch_file")
    
    echo "Applying $patch_name..."
    
    if git apply --check "$patch_file" 2>/dev/null; then
        git apply "$patch_file"
        echo "✓ Applied $patch_name"
    else
        echo "⚠ Patch $patch_name already applied or failed"
    fi
}

# Apply all patches in order
for patch in "$PATCHES_DIR"/*.patch; do
    if [ -f "$patch" ]; then
        apply_patch "$patch"
    fi
done

# Handle binary files (icons and logos)
echo ""
echo "Copying Feral assets..."

# Check if we have the logo assets
if [ -d "$PROJECT_ROOT/../logo" ]; then
    LOGO_DIR="$PROJECT_ROOT/../logo"
    
    # Generate launcher icons
    if command -v convert &> /dev/null || command -v magick &> /dev/null; then
        echo "Generating launcher icons..."
        
        # Use magick if available, otherwise convert
        CONVERT_CMD="convert"
        if command -v magick &> /dev/null; then
            CONVERT_CMD="magick"
        fi
        
        # Icon sizes for different densities
        declare -A SIZES=(
            ["mdpi"]=48
            ["hdpi"]=72
            ["xhdpi"]=96
            ["xxhdpi"]=144
            ["xxxhdpi"]=192
        )
        
        ICON_DIR="$PROJECT_ROOT/appicon/element/src/main/res"
        
        for density in "${!SIZES[@]}"; do
            size=${SIZES[$density]}
            fg_size=$((size * 72 / 100))
            
            # Create foreground icons
            $CONVERT_CMD "$LOGO_DIR/logo_blanc_alpha.png" \
                -resize ${fg_size}x${fg_size} \
                -gravity center \
                -background transparent \
                -extent ${size}x${size} \
                -define webp:lossless=true \
                "$ICON_DIR/mipmap-$density/ic_launcher_foreground.webp"
            
            # Create monochrome icons
            $CONVERT_CMD "$LOGO_DIR/logo_blanc_alpha.png" \
                -resize ${fg_size}x${fg_size} \
                -gravity center \
                -background transparent \
                -extent ${size}x${size} \
                -colorspace Gray \
                -define webp:lossless=true \
                "$ICON_DIR/mipmap-$density/ic_launcher_monochrome.webp"
            
            # Create launcher icons
            $CONVERT_CMD -size ${size}x${size} xc:'#0F2C1E' \
                \( "$LOGO_DIR/logo_blanc_alpha.png" -resize ${fg_size}x${fg_size} \) \
                -gravity center -composite \
                -define webp:lossless=true \
                "$ICON_DIR/mipmap-$density/ic_launcher.webp"
            
            # Create round launcher icons
            $CONVERT_CMD -size ${size}x${size} xc:transparent \
                -fill '#0F2C1E' \
                -draw "circle $((size/2)),$((size/2)) $((size/2)),0" \
                \( "$LOGO_DIR/logo_blanc_alpha.png" -resize ${fg_size}x${fg_size} \) \
                -gravity center -composite \
                -define webp:lossless=true \
                "$ICON_DIR/mipmap-$density/ic_launcher_round.webp"
        done
        
        # Create in-app logos
        mkdir -p "$PROJECT_ROOT/libraries/designsystem/src/main/res/drawable-xxhdpi"
        mkdir -p "$PROJECT_ROOT/libraries/designsystem/src/main/res/drawable-night-xxhdpi"
        mkdir -p "$PROJECT_ROOT/libraries/push/impl/src/main/res/drawable-xxhdpi"
        
        $CONVERT_CMD "$LOGO_DIR/logo_noir_alpha.png" -resize 240x240 \
            "$PROJECT_ROOT/libraries/designsystem/src/main/res/drawable-xxhdpi/element_logo.png"
        
        $CONVERT_CMD "$LOGO_DIR/logo_blanc_alpha.png" -resize 240x240 \
            "$PROJECT_ROOT/libraries/designsystem/src/main/res/drawable-night-xxhdpi/element_logo.png"
        
        $CONVERT_CMD "$LOGO_DIR/logo_blanc_alpha.png" -resize 240x240 \
            "$PROJECT_ROOT/libraries/push/impl/src/main/res/drawable-xxhdpi/element_logo_green.png"
        
        echo "✓ Feral assets generated"
    else
        echo "⚠ ImageMagick not found. Please install it to generate icons."
        echo "  On Fedora: sudo dnf install ImageMagick"
        echo "  On Ubuntu: sudo apt-get install imagemagick"
    fi
else
    echo "⚠ Logo directory not found at $PROJECT_ROOT/../logo"
    echo "  Please ensure the Feral logo files are available."
fi

echo ""
echo "Feral customization complete!"
echo ""
echo "Note: To remove customizations, run: ./scripts/remove-feral-patches.sh"