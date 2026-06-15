package com.todayplay.app.navigation

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.todayplay.app.localization.SystemStrings
import com.todayplay.app.model.ExternalMapAction

object MapNavigator {
    fun openInAmap(context: Context, action: ExternalMapAction, strings: SystemStrings) {
        val amapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(action.amapUri)).apply {
            setPackage("com.autonavi.minimap")
        }
        val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(action.fallbackUri))

        if (tryStart(context, amapIntent)) {
            return
        }
        if (tryStart(context, fallbackIntent)) {
            Toast.makeText(context, strings.mapOpenedInBrowser, Toast.LENGTH_SHORT).show()
            return
        }

        copyAddress(context, action.address, strings.destinationAddressLabel)
        Toast.makeText(context, strings.mapUnavailableCopiedAddress, Toast.LENGTH_SHORT).show()
    }

    private fun tryStart(context: Context, intent: Intent): Boolean {
        return runCatching {
            context.startActivity(intent)
            true
        }.getOrElse { error ->
            error !is ActivityNotFoundException && false
        }
    }

    private fun copyAddress(context: Context, address: String, label: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, address))
    }
}
