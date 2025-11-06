/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.settings.esimswitcher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_LOCKED_BOOT_COMPLETED) {
            return
        }

        val shouldBeDisabled = Settings.Global.getInt(
            context.contentResolver,
            SETTING_ESIM_POWER_DISABLED,
            0
        ) == 1

        val controller = EsimController(context)
        controller.setEsimPowerDisabled(shouldBeDisabled)
        context.startService(Intent(context, EsimObserverService::class.java))
    }

    companion object {
        private const val SETTING_ESIM_POWER_DISABLED = "esim_power_disabled"
    }
}
