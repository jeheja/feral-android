# Feral Android Patches

This directory contains patches that customize Element Android to create Feral Android.

## Patches

1. **001-feral-app-name.patch** - Changes app name from "Element X" to "Feral"
2. **002-feral-server-config.patch** - Sets default homeserver to feralisme.fr
3. **003-feral-external-signup.patch** - Disables native registration and adds external signup
4. **004-feral-colors.patch** - Updates color scheme to Feral branding
5. **005-feral-colors-new-file.patch** - Adds FeralColors.kt with custom color definitions
6. **006-feral-icon-backgrounds.patch** - Updates launcher icon backgrounds
7. **007-feral-enterprise-service.patch** - Restricts to Feral servers only with locale-based defaults
8. **008-fix-server-classification.patch** - Fixes server classification to not mark Feral servers as matrix.org

## Binary Assets

The following binary files need to be generated from Feral logos:
- App launcher icons (all densities)
- In-app logos for light/dark themes
- Notification icon

These are handled by the `apply-feral-patches.sh` script.

## Usage

### Apply Customizations
```bash
./scripts/apply-feral-patches.sh
```

### Remove Customizations
```bash
./scripts/remove-feral-patches.sh
```

### Update from Upstream
```bash
./scripts/update-from-upstream.sh
```

## Creating New Patches

When adding new customizations:

1. Make your changes
2. Create a patch: `git diff -- path/to/file > patches/00X-description.patch`
3. Test the patch: `git apply --check patches/00X-description.patch`
4. Document the patch in this README

## Maintaining Patches

After upstream updates, if patches fail to apply:

1. Apply patches manually or fix conflicts
2. Regenerate the failing patch
3. Test thoroughly

## Notes

- Patches are applied in numerical order
- Binary files (images, icons) are handled separately by the scripts
- Always test the app after applying patches or updating from upstream