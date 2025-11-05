/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.settings.esimswitcher

import android.app.Service
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log

class EsimObserverService : Service() {

    private lateinit var controller: EsimController
    private lateinit var settingsObserver: ContentObserver

    override fun onCreate() {
        super.onCreate()
        controller = EsimController(this)

        settingsObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                Log.d(TAG, "Settings changed: $uri")
                handleSettingChange()
            }
        }

        contentResolver.registerContentObserver(
            Settings.Global.getUriFor(SETTING_ESIM_POWER_DISABLED),
            true,
            settingsObserver
        )

        Log.d(TAG, "Observer service started")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        contentResolver.unregisterContentObserver(settingsObserver)
        Log.d(TAG, "Observer service stopped")
    }

    private fun handleSettingChange() {
        val disabled = Settings.Global.getInt(
            contentResolver,
            SETTING_ESIM_POWER_DISABLED,
            0
        ) == 1

        Log.d(TAG, "Setting changed: eSIM power disabled = $disabled")
        controller.setEsimPowerDisabled(disabled)
    }

    companion object {
        private const val TAG = "EsimObserverService"
        private const val SETTING_ESIM_POWER_DISABLED = "esim_power_disabled"
    }
}
