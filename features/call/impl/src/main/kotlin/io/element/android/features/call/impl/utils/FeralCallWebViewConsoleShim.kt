/*
 * Copyright (c) 2026 Element Creations Ltd.
 * Feral-owned file: written for the Feral fork, absent upstream (docs/FERAL_MAINTENANCE.md §4).
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.utils

/**
 * The `console.*` override injected into the Element Call WebView by
 * [WebViewWidgetMessageInterceptor] (upstream issue element-hq/element-x-android#4097:
 * objects must be serialised into the log line, otherwise they reach logcat as
 * `[object Object]`).
 *
 * Invariant: **a console call must never throw into Element Call.** Upstream's shim did a
 * bare `JSON.stringify(a)`. Since Element Call 0.24.0 the `Connection` constructor logs the
 * LiveKit room options, whose E2EE key provider references the MatrixClient graph (a cycle):
 * `JSON.stringify` threw "Converting circular structure to JSON" synchronously inside Element
 * Call, the LiveKit SFU was never contacted and every call ended on
 * "Something went wrong (UNKNOWN_ERROR)" — element-hq/element-call#4164. `Error` objects
 * also used to serialise to `{}`.
 *
 * This version: cycles become `"[Circular]"`, errors keep their stack, bigints and
 * unserialisable values fall back to `String(a)`, one line is capped at 20 kB (a whole client
 * graph must not flood logcat), and the forwarding call itself is wrapped in try/catch.
 *
 * The script must stay free of `$` (Kotlin raw string) and of template literals.
 */
object FeralCallWebViewConsoleShim {
    val SCRIPT: String = """
        function safeStringify(a) {
            if (typeof a === "string") return a;
            if (a instanceof Error) return a.stack || (a.name + ": " + a.message);
            try {
                const seen = new WeakSet();
                const s = JSON.stringify(a, function (k, v) {
                    if (typeof v === "bigint") return v.toString();
                    if (v instanceof Error) return { name: v.name, message: v.message, stack: v.stack };
                    if (v && typeof v === "object") {
                        if (seen.has(v)) return "[Circular]";
                        seen.add(v);
                    }
                    return v;
                });
                if (s === undefined) return String(a);
                return s.length > 20000 ? s.slice(0, 20000) + "...[truncated]" : s;
            } catch (e) {
                try { return String(a); } catch (e2) { return "[unserializable]"; }
            }
        }
        function logFn(consoleLogFn, ...args) {
            try {
                consoleLogFn(args.map(safeStringify).join(' '));
            } catch (e) {
                try { consoleLogFn(args.map(String).join(' ')); } catch (e2) {}
            }
        };
        globalThis.console.debug = logFn.bind(null, console.debug);
        globalThis.console.log = logFn.bind(null, console.log);
        globalThis.console.info = logFn.bind(null, console.info);
        globalThis.console.warn = logFn.bind(null, console.warn);
        globalThis.console.error = logFn.bind(null, console.error);
    """.trimIndent()
}
