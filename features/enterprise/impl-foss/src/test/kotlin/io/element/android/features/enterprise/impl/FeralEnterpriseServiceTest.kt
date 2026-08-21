/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.enterprise.impl

import com.google.common.truth.Truth.assertThat
import io.element.android.features.enterprise.api.canConnectToAnyHomeserver
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * Guard test for the Feral members-only lock.
 *
 * This is the regression guard called out in `docs/FERAL_MAINTENANCE.md`: a past
 * upstream sync silently reverted the homeserver restriction, and the override was
 * living in a module that was never compiled. If a future rebase/merge drops or
 * neutralises [FeralEnterpriseService], these assertions fail in CI BEFORE an APK
 * that accepts any homeserver can ever be built and signed.
 */
class FeralEnterpriseServiceTest {

    private val service = FeralEnterpriseService()

    @Test
    fun `the members-only lock is active - cannot connect to any homeserver`() {
        // If defaultHomeserverList() is empty or contains "*", onboarding would accept
        // arbitrary servers. This is the single most important invariant.
        assertThat(service.canConnectToAnyHomeserver()).isFalse()
    }

    @Test
    fun `default homeserver list is the Feral allow-list and is never empty`() {
        val list = service.defaultHomeserverList()
        assertThat(list).isNotEmpty()
        assertThat(list).contains("https://feralisme.fr")
        assertThat(list).doesNotContain("*")
    }

    @Test
    fun `allowed only for Feral homeservers`() = runTest {
        assertThat(service.isAllowedToConnectToHomeserver("https://feralisme.fr")).isTrue()
        // scheme / trailing-slash tolerant
        assertThat(service.isAllowedToConnectToHomeserver("feralisme.fr")).isTrue()
        assertThat(service.isAllowedToConnectToHomeserver("https://feralisme.fr/")).isTrue()
    }

    @Test
    fun `rejected for non-Feral homeservers`() = runTest {
        assertThat(service.isAllowedToConnectToHomeserver("https://matrix.org")).isFalse()
        assertThat(service.isAllowedToConnectToHomeserver("https://evil.example")).isFalse()
        assertThat(service.isAllowedToConnectToHomeserver("matrix.org")).isFalse()
    }

    @Test
    fun `isEnterpriseBuild stays false for the FOSS build`() {
        assertThat(service.isEnterpriseBuild).isFalse()
    }
}
