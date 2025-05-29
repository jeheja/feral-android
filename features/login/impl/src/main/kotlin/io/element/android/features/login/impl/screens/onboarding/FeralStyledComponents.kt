/*
 * Copyright 2025 Feral
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Text component with soft white shadow for better readability on forest gradient
 */
@Composable
fun FeralShadowText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
    shadowIntensity: Float = 0.7f
) {
    Box(modifier = modifier) {
        // Multiple shadow layers for soft, organic look
        // Far shadow layer
        Text(
            text = text,
            style = style,
            color = Color.White.copy(alpha = shadowIntensity * 0.3f),
            textAlign = textAlign,
            modifier = Modifier
                .offset(x = 0.dp, y = 0.dp)
                .alpha(0.5f)
        )
        
        // Medium shadow layer
        Text(
            text = text,
            style = style,
            color = Color.White.copy(alpha = shadowIntensity * 0.5f),
            textAlign = textAlign,
            modifier = Modifier
                .offset(x = 0.dp, y = 0.dp)
                .alpha(0.7f)
        )
        
        // Main text
        Text(
            text = text,
            style = style,
            color = color,
            textAlign = textAlign
        )
    }
}

/**
 * Enhanced logo with size increase
 */
@Composable
fun FeralEnhancedLogo(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .scale(1.25f), // Increase size by 25%
        contentAlignment = Alignment.Center
    ) {
        // Main logo only, no shadow
        content()
    }
}