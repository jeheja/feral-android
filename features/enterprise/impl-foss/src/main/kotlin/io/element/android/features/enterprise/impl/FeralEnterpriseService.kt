/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.enterprise.impl

import androidx.compose.ui.graphics.Color
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.compound.colors.SemanticColorsLightDark
import io.element.android.features.enterprise.api.BugReportUrl
import io.element.android.features.enterprise.api.EnterpriseService
import io.element.android.libraries.matrix.api.core.SessionId
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Feral members-only [EnterpriseService].
 *
 * This restores the Feral homeserver restriction (members-only lock) inside the
 * module that is ACTUALLY compiled for the public/FOSS build,
 * `:features:enterprise:impl-foss`, by REPLACING the upstream
 * [DefaultEnterpriseService] binding through Metro's `replaces`.
 *
 * ## Why this file exists here (and not in a `features/enterprise/impl` directory)
 * The previous override sat in `features/enterprise/impl/FeralEnterpriseService.kt`,
 * a directory with NO `build.gradle.kts`. `settings.gradle.kts` only includes a
 * project when a build file is present, so that directory was never a Gradle module
 * and its content was NEVER COMPILED -- dead code. It also carried a stale Anvil
 * `@ContributesBinding` (upstream migrated Anvil -> Metro). Net effect: the
 * members-only lock was SILENTLY INACTIVE and the FOSS APK accepted any homeserver,
 * because the compiled `DefaultEnterpriseService` returns an empty allow-list and
 * `isAllowedToConnectToHomeserver = true`. Putting the override here, in the module
 * that is actually compiled, fixes that.
 *
 * Keeping the override as a single Feral-owned file in `impl-foss` that `replaces`
 * the default binding is the thin, conflict-resistant pattern: upstream never edits
 * this file, so a rebase/merge cannot silently revert it. See
 * `docs/FERAL_MAINTENANCE.md`.
 */
@ContributesBinding(AppScope::class, replaces = [DefaultEnterpriseService::class])
class FeralEnterpriseService : EnterpriseService {

    /**
     * A Feral regional Matrix homeserver the app is allowed to connect to.
     * `locales` maps ISO language/country codes to this server for locale-based
     * pre-selection in onboarding.
     */
    data class FeralServer(
        val url: String,
        val description: String,
        val locales: Set<String> = emptySet(),
    )

    /**
     * The ONLY homeservers a Feral build may connect to. This is the members-only
     * allow-list.
     *
     * Verified 2026-08-21: `feralism.net` resolves to the same VPS but does NOT serve
     * Matrix (404 on /_matrix/client/versions, no well-known), so it must not be
     * offered in onboarding. Re-add it here if it ever becomes a real homeserver.
     */
    private val feralServers = listOf(
        FeralServer(
            url = "https://feralisme.fr",
            description = "Serveur France",
            locales = setOf("fr", "FR"),
        ),
        // FeralServer(
        //     url = "https://feralism.net",
        //     description = "International server",
        // ),
    )

    // A Feral FOSS build is NOT an Element "enterprise" build; keep this false so we
    // never accidentally enable enterprise-only code paths elsewhere. The members-only
    // restriction is expressed purely through the homeserver allow-list below.
    override val isEnterpriseBuild = false

    override suspend fun isEnterpriseUser(sessionId: SessionId) = false

    override fun defaultHomeserverList(): List<String> {
        val default = defaultHomeserverForLocale()
        return listOf(default) + feralServers.map { it.url }.filter { it != default }
    }

    override suspend fun isAllowedToConnectToHomeserver(homeserverUrl: String): Boolean {
        val normalized = normalize(homeserverUrl)
        return feralServers.any { normalized == normalize(it.url) }
    }

    private fun defaultHomeserverForLocale(): String {
        val locale = Locale.getDefault()
        return feralServers.firstOrNull { server ->
            server.locales.contains(locale.country) || server.locales.contains(locale.language)
        }?.url ?: "https://feralisme.fr"
    }

    private fun normalize(url: String): String {
        val trimmed = url.trim().removeSuffix("/")
        return when {
            trimmed.startsWith("https://") -> trimmed
            trimmed.startsWith("http://") -> "https://" + trimmed.removePrefix("http://")
            else -> "https://$trimmed"
        }
    }

    // --- Non-lock members: keep the upstream FOSS defaults. Visual branding is done
    // in the UI layer (FeralTheme), not here, to keep this file minimal. ---

    override suspend fun overrideBrandColor(sessionId: SessionId?, brandColor: String?) = Unit

    override fun brandColorsFlow(sessionId: SessionId?): Flow<Color?> = flowOf(null)

    override fun semanticColorsFlow(sessionId: SessionId?): Flow<SemanticColorsLightDark> =
        flowOf(SemanticColorsLightDark.default)

    override fun firebasePushGateway(): String? = null

    override fun unifiedPushDefaultPushGateway(): String? = null

    override fun bugReportUrlFlow(sessionId: SessionId?): Flow<BugReportUrl> =
        flowOf(BugReportUrl.UseDefault)

    override fun getNoisyNotificationChannelId(sessionId: SessionId): String? = null
}
