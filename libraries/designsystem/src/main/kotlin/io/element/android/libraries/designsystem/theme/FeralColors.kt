/*
 * Copyright 2025 Feral
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * Feral custom color palette
 * Based on the web design's wild forest atmosphere
 */
object FeralColors {
    // Primary colors - Deep forest tones
    val deepForest = Color(0xFF0F2C1E)
    val forestShadow = Color(0xFF0A1F15)
    val forestMedium = Color(0xFF1A4D35)
    val forestLight = Color(0xFF0F3425)
    
    // Accent colors - Golden earth tones
    val golden = Color(0xFFD4AF37)
    val goldenBright = Color(0xFFF1C40F)
    val earthBrown = Color(0xFF6D4C31)
    val warmBrown = Color(0xFF8B5A2B)
    
    // Secondary colors - Natural tones
    val cream = Color(0xFFFFF8E7)
    val sageGreen = Color(0xFF87A96B)
    val mossGreen = Color(0xFF4A5D3A)
    
    // Text colors
    val textLight = Color(0xFFFAFAFA)
    val textDark = Color(0xFF2C2416)
    
    // Semantic colors
    val successGreen = Color(0xFF4CAF50)
    val warningOrange = Color(0xFFFF9800)
    val infoBlue = Color(0xFF2196F3)
    
    // Gradient colors for backgrounds
    val gradientStartDark = deepForest
    val gradientEndDark = forestShadow
    val gradientStartLight = forestMedium
    val gradientEndLight = forestLight
    
    // Gradient colors for buttons and accents
    val gradientStartGolden = golden
    val gradientEndGolden = warmBrown
}