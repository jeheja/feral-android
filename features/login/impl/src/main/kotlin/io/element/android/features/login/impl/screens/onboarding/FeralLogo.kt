/*
 * Copyright 2025 Feral
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.element.android.libraries.designsystem.R

/**
 * Feral logo with forest-tinted white background
 */
@Composable
fun FeralLogo(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(150.dp) // Optimal size for mobile
            .clip(RoundedCornerShape(40.dp))
            .background(
                Color(0xFFD5E5D5) // Stronger green-tinted white
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize()
                .scale(1.11f), // Scale up by 11% - maximum breathing room
            painter = painterResource(id = R.drawable.element_logo),
            contentDescription = null,
            contentScale = ContentScale.Crop // Crop to fill
        )
    }
}