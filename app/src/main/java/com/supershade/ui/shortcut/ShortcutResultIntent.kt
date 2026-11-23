package com.supershade.ui.shortcut

import android.app.Activity
import android.content.Intent

internal object ShortcutResultIntent {
    fun create(
        activity: Activity,
        target: Class<out Activity>,
        name: String,
        iconResourceId: Int,
    ): Intent {
        val shortcutLaunchIntent = Intent(activity, target).apply {
            action = Intent.ACTION_MAIN
        }
        return Intent().apply {
            putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutLaunchIntent)
            putExtra(Intent.EXTRA_SHORTCUT_NAME, name)
            putExtra(
                Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(activity, iconResourceId),
            )
        }
    }
}
