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
        // Return default Element colors - no customizations
        return compoundColorsLight
    }
    
    /**
     * Creates custom semantic colors for dark theme
     * Deep forest atmosphere with golden accents
     */
    fun createFeralDarkColors(): SemanticColors {
        // Return default Element colors - no customizations
        return compoundColorsDark
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