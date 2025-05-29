/*
 * Copyright 2025 Feral
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.designsystem.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Feral custom typography
 * Includes serif fonts for headers to match the wild, organic aesthetic
 */
object FeralTypography {
    
    // Times New Roman equivalent serif font for Android
    val serifFontFamily = FontFamily.Serif
    
    // Custom text styles
    val heroTitle = TextStyle(
        fontFamily = serifFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        letterSpacing = (-1).sp
    )
    
    val welcomeTitle = TextStyle(
        fontFamily = serifFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        letterSpacing = (-0.5).sp
    )
    
    val sectionTitle = TextStyle(
        fontFamily = serifFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        letterSpacing = 0.sp
    )
}