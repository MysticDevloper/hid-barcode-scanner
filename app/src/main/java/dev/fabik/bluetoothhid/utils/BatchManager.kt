package dev.fabik.bluetoothhid.utils

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

data class BatchItem(
    val id: Long = System.currentTimeMillis(),
    val barcode: String,
    val format: String,
    val timestamp: Long = System.currentTimeMillis()
)

class BatchManager(private val context: Context) {
    
    companion object {
        private const val TAG = "BatchManager"
        
        private val BATCH_ENABLED_KEY = booleanPreferencesKey("batch_mode_enabled")
        private val BATCH_DELAY_KEY = floatPreferencesKey("batch_delay")
    }
    
    private val _queue = MutableStateFlow<List<BatchItem>>(emptyList())
    val queue: StateFlow<List<BatchItem>> = _queue.asStateFlow()
    
    private val _isEnabled = MutableStateFlow(false)
    val isEnabled: StateFlow<Boolean> = _isEnabled.asStateFlow()
    
    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()
    
    private var _batchDelay = 100f
    
    private val scope = CoroutineScope(Dispatchers.Main)
    
    init {
        loadPreferences()
    }
    
    private fun loadPreferences() {
        runBlocking {
            val prefs = context.dataStore.data.first()
            _isEnabled.value = prefs[BATCH_ENABLED_KEY] ?: false
            _batchDelay = prefs[BATCH_DELAY_KEY] ?: 100f
        }
    }
    
    fun setEnabled(enabled: Boolean) {
        _isEnabled.value = enabled
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[BATCH_ENABLED_KEY] = enabled
            }
        }
        if (!enabled) {
            clearQueue()
        }
    }
    
    fun getBatchDelay(): Float = _batchDelay
    
    fun setBatchDelay(delay: Float) {
        _batchDelay = delay.coerceIn(0f, 5000f)
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[BATCH_DELAY_KEY] = _batchDelay
            }
        }
    }
    
    fun addToQueue(barcode: String, format: String) {
        val item = BatchItem(barcode = barcode, format = format)
        _queue.value = _queue.value + item
        Log.d(TAG, "Added to queue: $barcode (${_queue.value.size} items)")
    }
    
    fun removeFromQueue(id: Long) {
        _queue.value = _queue.value.filter { it.id != id }
    }
    
    fun clearQueue() {
        _queue.value = emptyList()
    }
    
    fun editQueueItem(id: Long, newBarcode: String) {
        _queue.value = _queue.value.map { item ->
            if (item.id == id) item.copy(barcode = newBarcode) else item
        }
    }
    
    fun moveQueueItem(fromIndex: Int, toIndex: Int) {
        val currentList = _queue.value.toMutableList()
        if (fromIndex in currentList.indices && toIndex in currentList.indices) {
            val item = currentList.removeAt(fromIndex)
            currentList.add(toIndex, item)
            _queue.value = currentList
        }
    }
    
    fun getQueueSize(): Int = _queue.value.size
    
    fun isEmpty(): Boolean = _queue.value.isEmpty()
    
    fun setSending(sending: Boolean) {
        _isSending.value = sending
    }
    
    suspend fun sendAllItems(
        sendFunction: suspend (String, String) -> Boolean
    ): Boolean {
        if (_isSending.value || _queue.value.isEmpty()) {
            return false
        }
        
        _isSending.value = true
        var allSuccess = true
        
        for (item in _queue.value) {
            val success = sendFunction(item.barcode, item.format)
            if (!success) {
                allSuccess = false
                break
            }
            
            delay(_batchDelay.toLong())
        }
        
        clearQueue()
        _isSending.value = false
        
        return allSuccess
    }
}
