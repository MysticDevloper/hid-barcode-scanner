package dev.fabik.bluetoothhid.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import dev.fabik.bluetoothhid.MainActivity
import dev.fabik.bluetoothhid.R

class ScannerWidgetProvider : AppWidgetProvider() {
    
    companion object {
        const val ACTION_UPDATE_WIDGET = "dev.fabik.bluetoothhid.ACTION_UPDATE_WIDGET"
        const val EXTRA_LAST_SCAN = "extra_last_scan"
        const val EXTRA_CONNECTION_STATUS = "extra_connection_status"
        
        private var lastScanValue: String? = null
        private var isConnected: Boolean = false
        
        fun updateWidget(context: Context, lastScan: String? = null, connected: Boolean? = null) {
            lastScan?.let { lastScanValue = it }
            connected?.let { isConnected = it }
            
            val intent = Intent(context, ScannerWidgetProvider::class.java).apply {
                action = ACTION_UPDATE_WIDGET
            }
            context.sendBroadcast(intent)
        }
        
        fun getLastScan(): String? = lastScanValue
        fun isConnected(): Boolean = isConnected
    }
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        if (intent.action == ACTION_UPDATE_WIDGET) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, ScannerWidgetProvider::class.java)
            )
            onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }
    
    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_scanner)
        
        views.setTextViewText(
            R.id.widget_last_scan,
            lastScanValue ?: context.getString(R.string.widget_last_scan)
        )
        
        val connectionText = if (isConnected) {
            context.getString(R.string.widget_connected)
        } else {
            context.getString(R.string.widget_not_connected)
        }
        views.setTextViewText(R.id.widget_connection_status, connectionText)
        
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("target", "Scanner")
        }
        val launchPendingIntent = PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_container, launchPendingIntent)
        
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
