/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.login.impl.R
import io.element.android.features.login.impl.login.LoginModeView
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.IconSource
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.FeralTypography
import io.element.android.libraries.matrix.api.auth.OidcDetails
import io.element.android.libraries.testtags.TestTags
import io.element.android.libraries.testtags.testTag
import io.element.android.libraries.ui.strings.CommonStrings

// Refs:
// FTUE:
// - https://www.figma.com/file/o9p34zmiuEpZRyvZXJZAYL/FTUE?type=design&node-id=133-5427&t=5SHVppfYzjvkEywR-0
// ElementX:
// - https://www.figma.com/file/0MMNu7cTOzLOlWb7ctTkv3/Element-X?type=design&node-id=1816-97419
@Composable
fun OnBoardingView(
    state: OnBoardingState,
    onSignInWithQrCode: () -> Unit,
    onSignIn: (mustChooseAccountProvider: Boolean) -> Unit,
    onCreateAccount: () -> Unit,
    onExternalSignup: () -> Unit,
    onOidcDetails: (OidcDetails) -> Unit,
    onNeedLoginPassword: () -> Unit,
    onLearnMoreClick: () -> Unit,
    onCreateAccountContinue: (url: String) -> Unit,
    onReportProblem: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FeralOnBoardingBackground {
        FeralOnBoardingPage(
            modifier = modifier,
            content = {
                OnBoardingContent(state = state)
                LoginModeView(
                    loginMode = state.loginMode,
                    onClearError = {
                        state.eventSink(OnBoardingEvents.ClearError)
                    },
                    onLearnMoreClick = onLearnMoreClick,
                    onOidcDetails = onOidcDetails,
                    onNeedLoginPassword = onNeedLoginPassword,
                    onCreateAccountContinue = onCreateAccountContinue,
                )
            },
            footer = {
                OnBoardingButtons(
                    state = state,
                    onSignInWithQrCode = onSignInWithQrCode,
                    onSignIn = onSignIn,
                    onCreateAccount = onCreateAccount,
                    onExternalSignup = onExternalSignup,
                    onReportProblem = onReportProblem,
                )
            }
        )
    }
}

@Composable
private fun OnBoardingContent(state: OnBoardingState) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.weight(1f))

        FeralLogo(
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Text(
            text = "FERAL",
            style = FeralTypography.welcomeTitle.copy(
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 6.sp,
            ),
            color = Color.White,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.screen_onboarding_welcome_message).uppercase(),
            style = FeralTypography.sectionTitle.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 4.sp,
            ),
            color = Color.White.copy(alpha = 0.45f),
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun OnBoardingButtons(
    state: OnBoardingState,
    onSignInWithQrCode: () -> Unit,
    onSignIn: (mustChooseAccountProvider: Boolean) -> Unit,
    onCreateAccount: () -> Unit,
    onExternalSignup: () -> Unit,
    onReportProblem: () -> Unit,
) {
    val isLoading by remember(state.loginMode) {
        derivedStateOf {
            state.loginMode is AsyncData.Loading
        }
    }

    val frostedGlassShape = RoundedCornerShape(14.dp)
    val frostedGlassColors = ButtonDefaults.outlinedButtonColors(
        containerColor = Color.White.copy(alpha = 0.1f),
        contentColor = Color.White,
    )
    val frostedGlassBorder = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.25f))

    Column(
        modifier = Modifier.padding(horizontal = 4.dp),
        horizontalAlignment = CenterHorizontally,
    ) {
        val signInButtonStringRes = if (state.canLoginWithQrCode || state.canCreateAccount || state.canExternalSignup) {
            R.string.screen_onboarding_sign_in_manually
        } else {
            CommonStrings.action_continue
        }
        if (state.canLoginWithQrCode) {
            OutlinedButton(
                onClick = onSignInWithQrCode,
                modifier = Modifier.fillMaxWidth(),
                shape = frostedGlassShape,
                colors = frostedGlassColors,
                border = frostedGlassBorder,
            ) {
                Text(
                    text = stringResource(id = R.string.screen_onboarding_sign_in_with_qr_code),
                    style = FeralTypography.sectionTitle.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
        val defaultAccountProvider = state.defaultAccountProvider
        if (defaultAccountProvider == null) {
            OutlinedButton(
                onClick = { onSignIn(false) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(TestTags.onBoardingSignIn),
                shape = frostedGlassShape,
                colors = frostedGlassColors,
                border = frostedGlassBorder,
            ) {
                Text(
                    text = stringResource(id = signInButtonStringRes),
                    style = FeralTypography.sectionTitle.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        } else {
            OutlinedButton(
                onClick = { state.eventSink(OnBoardingEvents.OnSignIn(defaultAccountProvider)) },
                modifier = Modifier.fillMaxWidth(),
                shape = frostedGlassShape,
                colors = frostedGlassColors,
                border = frostedGlassBorder,
                enabled = state.submitEnabled || isLoading,
            ) {
                Text(
                    text = stringResource(id = R.string.screen_onboarding_sign_in_to, defaultAccountProvider),
                    style = FeralTypography.sectionTitle.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        }
        if (state.canReportBug) {
            Text(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable(onClick = onReportProblem),
                text = stringResource(id = CommonStrings.common_report_a_problem),
                style = FeralTypography.sectionTitle.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                ),
                color = Color.White.copy(alpha = 0.6f),
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun OnBoardingViewPreview(
    @PreviewParameter(OnBoardingStateProvider::class) state: OnBoardingState
) = ElementPreview {
    OnBoardingView(
        state = state,
        onSignInWithQrCode = {},
        onSignIn = {},
        onCreateAccount = {},
        onExternalSignup = {},
        onReportProblem = {},
        onOidcDetails = {},
        onNeedLoginPassword = {},
        onLearnMoreClick = {},
        onCreateAccountContinue = {},
    )
}
