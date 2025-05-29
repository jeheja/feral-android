/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.enterprise.impl

import android.content.Context
import com.squareup.anvil.annotations.ContributesBinding
import io.element.android.compound.tokens.generated.SemanticColors
import io.element.android.compound.tokens.generated.compoundColorsDark
import io.element.android.compound.tokens.generated.compoundColorsLight
import io.element.android.features.enterprise.api.EnterpriseService
import io.element.android.libraries.di.AppScope
import io.element.android.libraries.di.ApplicationContext
import io.element.android.libraries.matrix.api.core.SessionId
import java.util.Locale
import javax.inject.Inject

/**
 * Feral Enterprise Service that provides a list of regional Feral Matrix servers
 * Users can choose between different regional servers but cannot add custom servers
 * The default server is selected based on the user's locale/language
 */
@ContributesBinding(AppScope::class)
class FeralEnterpriseService @Inject constructor(
    @ApplicationContext private val context: Context
) : EnterpriseService {
    override val isEnterpriseBuild = true

    override suspend fun isEnterpriseUser(sessionId: SessionId) = true

    // Map of server URLs to their descriptions (will be localized later)
    data class ServerInfo(
        val url: String,
        val description: String,
        val locales: Set<String> = emptySet()  // Language/country codes
    )
    
    private val feralServers = listOf(
        ServerInfo(
            url = "https://feralisme.fr",
            description = "Serveur pour la France",  // "Server for France"
            locales = setOf("fr", "FR")
        ),
        // Add more servers as they become available:
        // ServerInfo(
        //     url = "https://feral.de",
        //     description = "Server für Deutschland",  // "Server for Germany"
        //     locales = setOf("de", "DE")
        // ),
        // ServerInfo(
        //     url = "https://feral.es",
        //     description = "Servidor para España",  // "Server for Spain"
        //     locales = setOf("es", "ES")
        // ),
        // ServerInfo(
        //     url = "https://feral.chat",
        //     description = "International server",
        //     locales = emptySet()  // Default/fallback
        // ),
    )

    // Get the server description
    fun getServerDescription(url: String): String? {
        return feralServers.find { it.url == url }?.description
    }

    // Get the default homeserver based on locale
    private fun getDefaultHomeserverForLocale(): String {
        val locale = Locale.getDefault()
        
        // Find server matching country or language code
        feralServers.find { server ->
            server.locales.contains(locale.country) || server.locales.contains(locale.language)
        }?.let { return it.url }
        
        // Default to first server if no match
        return feralServers.firstOrNull()?.url ?: "https://feralisme.fr"
    }

    // List of allowed Feral regional servers
    // The list is ordered with the locale-appropriate server first
    override fun defaultHomeserverList(): List<String> {
        val defaultServer = getDefaultHomeserverForLocale()
        val allServers = feralServers.map { it.url }
        
        // Put the default server first in the list
        return listOf(defaultServer) + allServers.filter { it != defaultServer }
    }
    
    // Only allow connections to official Feral servers
    override suspend fun isAllowedToConnectToHomeserver(homeserverUrl: String): Boolean {
        val normalizedUrl = normalizeHomeserverUrl(homeserverUrl)
        val allowedServers = feralServers.map { it.url }
        return allowedServers.any { normalizedUrl == it || normalizedUrl == it.removePrefix("https://") }
    }

    private fun normalizeHomeserverUrl(url: String): String {
        return when {
            url.startsWith("https://") -> url
            url.startsWith("http://") -> url.replace("http://", "https://")
            else -> "https://$url"
        }
    }

    override suspend fun isElementCallAvailable(): Boolean = true

    override fun semanticColorsLight(): SemanticColors = compoundColorsLight

    override fun semanticColorsDark(): SemanticColors = compoundColorsDark

    override fun firebasePushGateway(): String? = null
    override fun unifiedPushDefaultPushGateway(): String? = null
}