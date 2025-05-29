/*
 * Copyright 2025 Feral
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import io.element.android.libraries.designsystem.theme.FeralColors

/**
 * Feral custom overlay for the onboarding screen
 * Adds the forest gradient background effect
 */
@Composable
fun FeralOnBoardingBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A2F1A), // Dark forest green at top
                        Color(0xFF2D4A2D), // Medium dark forest
                        Color(0xFF4A6B4A), // Medium forest
                        Color(0xFF6B8B6B), // Lighter medium forest
                        Color(0xFF8FAC8F), // Light forest green
                        Color(0xFFB5CDB5), // Soft light green
                        Color(0xFFE5F0E5), // Very light green
                        Color(0xFFF5FAF5)  // Near white at bottom
                    ),
                    startY = 0f,
                    endY = 1800f  // Smoother gradient spread
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}