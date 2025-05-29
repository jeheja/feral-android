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
                        FeralColors.forestShadow,
                        FeralColors.deepForest.copy(alpha = 0.7f),
                        FeralColors.forestMedium.copy(alpha = 0.5f),
                        FeralColors.forestLight.copy(alpha = 0.3f)
                    ),
                    startY = 0f,
                    endY = 2000f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Add subtle radial gradient overlay for depth
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.3f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            FeralColors.forestShadow.copy(alpha = 0.5f)
                        ),
                        radius = 1000f
                    )
                )
        )
        content()
    }
}