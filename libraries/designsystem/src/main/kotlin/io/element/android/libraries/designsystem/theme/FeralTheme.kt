/*
 * Copyright 2025 Feral
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.designsystem.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import io.element.android.compound.tokens.generated.SemanticColors
import io.element.android.compound.tokens.generated.compoundColorsDark
import io.element.android.compound.tokens.generated.compoundColorsLight

/**
 * Feral custom theme overrides
 * Creates a wild, forest-like atmosphere with deep greens and earth tones
 */
object FeralTheme {
    
    /**
     * Creates custom semantic colors for light theme
     * Based on the web design with cream backgrounds and dark forest accents
     */
    fun createFeralLightColors(): SemanticColors {
        return compoundColorsLight.copy(
            // Backgrounds - cream and light tones
            bgCanvasDefault = FeralColors.cream,
            bgSubtleSecondary = Color(0xFFFAFAFA),
            bgSubtlePrimary = Color.White,
            
            // Action colors - keep Element defaults (commenting out golden)
            // bgActionPrimaryRest = FeralColors.golden,
            // bgActionPrimaryHovered = FeralColors.goldenBright,
            // bgActionPrimaryPressed = FeralColors.warmBrown,
            // bgActionPrimaryDisabled = FeralColors.golden.copy(alpha = 0.4f),
            
            // Text colors
            textPrimary = FeralColors.textDark,
            textSecondary = FeralColors.textDark.copy(alpha = 0.7f),
            // textActionPrimary = FeralColors.golden,
            textOnSolidPrimary = Color.White,
            
            // Borders
            // borderInteractivePrimary = FeralColors.golden,
            borderInteractiveSecondary = FeralColors.mossGreen,
            
            // Icon colors
            iconPrimary = FeralColors.deepForest,
            iconSecondary = FeralColors.mossGreen,
            // iconAccentTertiary = FeralColors.golden,  // Using iconAccentTertiary instead of iconActionPrimary
            iconOnSolidPrimary = Color.White,
            
            // Critical colors (keep original for safety)
            bgCriticalPrimary = Color(0xFFD32F2F),
            textCriticalPrimary = Color(0xFFD32F2F),
            
            // Success colors
            bgSuccessSubtle = FeralColors.successGreen,  // Using bgSuccessSubtle instead of bgSuccessPrimary
            textSuccessPrimary = FeralColors.successGreen,
        )
    }
    
    /**
     * Creates custom semantic colors for dark theme
     * Deep forest atmosphere with golden accents
     */
    fun createFeralDarkColors(): SemanticColors {
        return compoundColorsDark.copy(
            // Backgrounds - deep forest tones
            bgCanvasDefault = FeralColors.forestShadow,
            bgSubtleSecondary = FeralColors.deepForest,
            bgSubtlePrimary = FeralColors.forestMedium,
            
            // Action colors - keep Element defaults (commenting out golden)
            // bgActionPrimaryRest = FeralColors.golden,
            // bgActionPrimaryHovered = FeralColors.goldenBright,
            // bgActionPrimaryPressed = FeralColors.warmBrown,
            // bgActionPrimaryDisabled = FeralColors.golden.copy(alpha = 0.4f),
            
            // Text colors
            textPrimary = FeralColors.textLight,
            textSecondary = FeralColors.textLight.copy(alpha = 0.7f),
            // textActionPrimary = FeralColors.golden,
            textOnSolidPrimary = FeralColors.deepForest,
            
            // Borders
            // borderInteractivePrimary = FeralColors.golden,
            borderInteractiveSecondary = FeralColors.sageGreen,
            
            // Icon colors
            iconPrimary = FeralColors.textLight,
            iconSecondary = FeralColors.sageGreen,
            // iconAccentTertiary = FeralColors.golden,  // Using iconAccentTertiary instead of iconActionPrimary
            iconOnSolidPrimary = FeralColors.deepForest,
            
            // Critical colors (keep original for safety)
            bgCriticalPrimary = Color(0xFFEF5350),
            textCriticalPrimary = Color(0xFFEF5350),
            
            // Success colors
            bgSuccessSubtle = FeralColors.successGreen,  // Using bgSuccessSubtle instead of bgSuccessPrimary
            textSuccessPrimary = FeralColors.successGreen,
        )
    }
}

/**
 * Feral gradient background modifier
 * Creates the signature forest gradient effect
 */
@Composable
fun Modifier.feralGradientBackground(isDark: Boolean = false): Modifier {
    val gradientColors = if (isDark) {
        listOf(
            FeralColors.deepForest,
            FeralColors.forestShadow
        )
    } else {
        listOf(
            Color.White,
            FeralColors.cream
        )
    }
    
    return this.background(
        brush = Brush.verticalGradient(
            colors = gradientColors,
            startY = 0f,
            endY = Float.POSITIVE_INFINITY
        )
    )
}

/**
 * Feral forest gradient background
 * More dramatic gradient for hero sections
 */
@Composable
fun FeralForestBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        FeralColors.forestMedium,
                        FeralColors.deepForest,
                        FeralColors.forestShadow
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        content()
    }
}