#!/usr/bin/env bash
# Feral Android release publisher — run on the BUILD/SIGNING machine (eheyu) after
# building and signing the APKs. Generates the update-channel manifests and (optionally)
# deploys everything to feralisme.fr. See docs/FERAL_MAINTENANCE.md.
#
# What it produces in --out (default: <apk-dir>/publish):
#   Feral-<ver>[-abi].apk           (copied)
#   Feral-<ver>[-abi].apk.sha256    (sha256sum sidecars, used by the member page)
#   update.json                     (read by the in-app updater)
#   version.json                    (read by the member download page on feralisme.fr)
#   latest.json                     (legacy pointer)
#
# Deploy layout on the VPS (both are kept in sync):
#   /var/www/html/feralism/media/downloads/android/   -> PUBLIC (nginx /media/), updater
#   /var/www/html/feralism/protected_downloads/       -> member download page
#
# IMPORTANT: upload the APKs BEFORE update.json (the manifest must never point to a
# file that is not fully uploaded yet). This script deploys in that order.
set -euo pipefail

usage() {
    cat >&2 <<'USAGE'
Usage: publish-release.sh --version <versionName> --apk-dir <dir> [--out <dir>]
                          [--deploy user@host] [--changelog-fr "..."] [--changelog-en "..."]
Example:
  ./tools/feral/publish-release.sh --version 26.08.1 --apk-dir app/build/outputs/apk/gplay/release \
      --deploy loic_feral@172.232.45.124
USAGE
    exit 1
}

VERSION="" APK_DIR="" OUT="" DEPLOY="" CHANGELOG_FR="" CHANGELOG_EN=""
while [ $# -gt 0 ]; do
    case "$1" in
        --version) VERSION="$2"; shift 2 ;;
        --apk-dir) APK_DIR="$2"; shift 2 ;;
        --out) OUT="$2"; shift 2 ;;
        --deploy) DEPLOY="$2"; shift 2 ;;
        --changelog-fr) CHANGELOG_FR="$2"; shift 2 ;;
        --changelog-en) CHANGELOG_EN="$2"; shift 2 ;;
        *) usage ;;
    esac
done
[ -n "$VERSION" ] && [ -n "$APK_DIR" ] || usage
OUT="${OUT:-$APK_DIR/publish}"
BASE_URL="https://feralisme.fr/media/downloads/android"
MEDIA_DIR="/var/www/html/feralism/media/downloads/android"
PROTECTED_DIR="/var/www/html/feralism/protected_downloads"

# --- locate aapt (versionCode extraction) ------------------------------------------
find_aapt() {
    command -v aapt2 && return
    command -v aapt && return
    local sdk="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
    ls "$sdk"/build-tools/*/aapt2 2>/dev/null | sort -V | tail -1
}
AAPT="$(find_aapt || true)"
[ -n "$AAPT" ] || { echo "ERROR: aapt/aapt2 not found (need the Android SDK build-tools)" >&2; exit 1; }

apk_version_code() {
    "$AAPT" dump badging "$1" | sed -n "s/^package:.*versionCode='\([0-9]*\)'.*$/\1/p" | head -1
}
apk_abi() {
    # From the Feral naming convention Feral-<ver>[-<abi>].apk
    case "$(basename "$1")" in
        *-arm64-v8a.apk) echo "arm64-v8a" ;;
        *-armeabi-v7a.apk) echo "armeabi-v7a" ;;
        *-x86_64.apk) echo "x86_64" ;;
        *-x86.apk) echo "x86" ;;
        *.apk) echo "universal" ;;
    esac
}

mkdir -p "$OUT"
shopt -s nullglob
APKS=("$APK_DIR"/Feral-"$VERSION"*.apk)
if [ ${#APKS[@]} -eq 0 ]; then
    # Fall back to raw gradle output names: rename into the Feral convention first.
    for f in "$APK_DIR"/*.apk; do
        abi=$(basename "$f" | grep -oE 'arm64-v8a|armeabi-v7a|x86_64|x86' | head -1 || true)
        dest="$OUT/Feral-$VERSION${abi:+-$abi}.apk"
        cp "$f" "$dest"
    done
    APKS=("$OUT"/Feral-"$VERSION"*.apk)
else
    for f in "${APKS[@]}"; do cp -n "$f" "$OUT/" || true; done
    APKS=("$OUT"/Feral-"$VERSION"*.apk)
fi
[ ${#APKS[@]} -gt 0 ] || { echo "ERROR: no APKs found for version $VERSION in $APK_DIR" >&2; exit 1; }

# --- sidecars + manifest entries ---------------------------------------------------
APKS_JSON=""
UNIVERSAL_ENTRY=""
for f in "${APKS[@]}"; do
    name=$(basename "$f")
    sha=$(sha256sum "$f" | awk '{print $1}')
    size=$(stat -c%s "$f" 2>/dev/null || stat -f%z "$f")
    vcode=$(apk_version_code "$f")
    abi=$(apk_abi "$f")
    [ -n "$vcode" ] || { echo "ERROR: could not read versionCode from $name" >&2; exit 1; }
    ( cd "$(dirname "$f")" && sha256sum "$name" > "$name.sha256" )
    entry="\"$abi\": { \"url\": \"$BASE_URL/$name\", \"sha256\": \"$sha\", \"versionCode\": $vcode, \"size\": $size }"
    APKS_JSON="${APKS_JSON:+$APKS_JSON,
    }$entry"
    if [ "$abi" = "universal" ]; then
        UNIVERSAL_ENTRY="{ \"filename\": \"$name\", \"url\": \"$BASE_URL/$name\", \"sha256\": \"$sha\", \"size\": $size }"
        UNIVERSAL_VCODE="$vcode"
    fi
    echo "  $abi  versionCode=$vcode  sha256=${sha:0:16}…  $name"
done

# --- update.json (read by the in-app updater) --------------------------------------
cat > "$OUT/update.json" <<EOF
{
  "schema": 1,
  "versionName": "$VERSION",
  "minVersionCode": 0,
  "apks": {
    $APKS_JSON
  },
  "notes": {
    "fr": "${CHANGELOG_FR//\"/\\\"}",
    "en": "${CHANGELOG_EN//\"/\\\"}"
  }
}
EOF

# --- version.json + latest.json (member download page compatibility) ---------------
NOW=$(date -u +%Y-%m-%dT%H:%M:%SZ)
cat > "$OUT/version.json" <<EOF
{
  "version": "$VERSION",
  "versionCode": ${UNIVERSAL_VCODE:-0},
  "releaseDate": "$NOW",
  "download": ${UNIVERSAL_ENTRY:-null},
  "changelog": { "fr": "${CHANGELOG_FR//\"/\\\"}", "en": "${CHANGELOG_EN//\"/\\\"}" }
}
EOF
cat > "$OUT/latest.json" <<EOF
{ "version": "$VERSION", "versionCode": ${UNIVERSAL_VCODE:-0}, "downloadUrl": "$BASE_URL/Feral-$VERSION.apk" }
EOF

echo
echo "Manifests written to $OUT"

# --- deploy ------------------------------------------------------------------------
if [ -n "$DEPLOY" ]; then
    echo "Deploying to $DEPLOY…"
    # 1. binaries + sidecars first (public dir + member dir)
    scp "$OUT"/Feral-"$VERSION"*.apk "$OUT"/Feral-"$VERSION"*.apk.sha256 "$DEPLOY:$MEDIA_DIR/"
    scp "$OUT"/Feral-"$VERSION"*.apk "$OUT"/Feral-"$VERSION"*.apk.sha256 "$DEPLOY:$PROTECTED_DIR/"
    # 2. manifests LAST (atomicity of the update channel)
    scp "$OUT"/version.json "$OUT"/latest.json "$DEPLOY:$PROTECTED_DIR/"
    scp "$OUT"/update.json "$OUT"/version.json "$OUT"/latest.json "$DEPLOY:$MEDIA_DIR/"
    echo "Deployed. Sanity check:"
    echo "  curl -s $BASE_URL/update.json | head"
fi
