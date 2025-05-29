#!/bin/bash

# Script to update feral-android from upstream Element Android

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# Configuration
UPSTREAM_REMOTE="element"
UPSTREAM_BRANCH="main"  # or "develop" based on your preference
FERAL_BRANCH="feral-main"

echo "Updating Feral Android from upstream Element Android..."
echo ""

# Check if upstream remote exists
if ! git remote | grep -q "^$UPSTREAM_REMOTE$"; then
    echo "Adding upstream remote..."
    git remote add "$UPSTREAM_REMOTE" https://github.com/element-hq/element-x-android.git
fi

# Remove Feral customizations first
echo "Removing Feral customizations temporarily..."
"$SCRIPT_DIR/remove-feral-patches.sh"

# Fetch upstream changes
echo ""
echo "Fetching upstream changes..."
git fetch "$UPSTREAM_REMOTE" "$UPSTREAM_BRANCH"

# Get current branch
CURRENT_BRANCH=$(git branch --show-current)

# Create or switch to feral branch
if git show-ref --verify --quiet "refs/heads/$FERAL_BRANCH"; then
    git checkout "$FERAL_BRANCH"
else
    git checkout -b "$FERAL_BRANCH"
fi

# Merge upstream changes
echo ""
echo "Merging upstream changes..."
if git merge "$UPSTREAM_REMOTE/$UPSTREAM_BRANCH" --no-edit; then
    echo "✓ Merge successful"
else
    echo "⚠ Merge conflicts detected. Please resolve them and run:"
    echo "  git merge --continue"
    echo "  ./scripts/apply-feral-patches.sh"
    exit 1
fi

# Re-apply Feral customizations
echo ""
echo "Re-applying Feral customizations..."
"$SCRIPT_DIR/apply-feral-patches.sh"

echo ""
echo "Update complete!"
echo ""
echo "Please test the application thoroughly to ensure all customizations work correctly."
echo "If you encounter issues, you can:"
echo "  - Check patch compatibility with: git apply --check patches/*.patch"
echo "  - Manually update patches if needed"