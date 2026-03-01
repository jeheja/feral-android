# Feral Android — Quick Reference

## Upstream Sync (TL;DR)

```bash
git fetch upstream
git merge upstream/develop
# Resolve conflicts: keep Feral files, take upstream for the rest
git push origin main
```

See `FERAL_CUSTOMIZATION.md` for detailed conflict resolution strategy.

## What's Changed from Element X

| Area | Element X | Feral |
|------|-----------|-------|
| App name | Element X | Feral |
| App ID | `io.element.android.x` | `feral.app` |
| Onboarding background | Element blue/gradient | Dark near-black gradient |
| Onboarding logo | Element logo in container | White Feral logo, no container |
| Onboarding title | "Element X" | "FERAL" (44sp, letter-spaced) |
| Onboarding subtitle | Element tagline | "FOR FERALISTS" (faded white) |
| Buttons | Standard Material | Frosted glass (10% white fill) |
| Login screen | Standard | + Members-only serif notice |
| Default server | matrix.org | feralisme.fr |
| Registration | In-app | External (feralisme.fr/inscription/) |
| Color scheme | Element colors | Forest green / golden / cream |
| Typography | Default | Serif-based custom |

## Custom Files (Feral-only)

These files don't exist in upstream and are always safe to keep:

- `FeralLogo.kt` — White logo component
- `FeralOnBoardingOverlay.kt` — Dark gradient background
- `FeralStyledComponents.kt` — Styled page layout
- `FeralButton.kt` — Custom button
- `FeralColors.kt` — Color definitions
- `FeralTypography.kt` — Font definitions

## Building

```bash
./gradlew assembleGplayDebug    # Debug
./gradlew assembleGplayRelease  # Release
```
