package dev.fabik.bluetoothhid.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.fabik.bluetoothhid.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Locale
import java.util.UUID

class VoiceFeedbackManager(private val context: Context) : TextToSpeech.OnInitListener {
    
    companion object {
        private const val TAG = "VoiceFeedback"
        
        private val VOICE_ENABLED_KEY = booleanPreferencesKey("voice_enabled")
        private val VOICE_LANGUAGE_KEY = stringPreferencesKey("voice_language")
        private val VOICE_VOLUME_KEY = floatPreferencesKey("voice_volume")
        private val VOICE_INCLUDE_FORMAT_KEY = booleanPreferencesKey("voice_include_format")
    }
    
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    
    private val scope = CoroutineScope(Dispatchers.Main)
    
    private val _isEnabled = MutableStateFlow(false)
    val isEnabled: StateFlow<Boolean> = _isEnabled.asStateFlow()
    
    private var _includeFormat = true
    private var _volume = 1.0f
    
    init {
        initialize()
    }
    
    private fun initialize() {
        tts = TextToSpeech(context, this)
    }
    
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.getDefault())
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w(TAG, "Language not supported")
            }
            
            isInitialized = true
            Log.d(TAG, "TTS initialized successfully")
            
            setupTtsListener()
            loadPreferences()
        } else {
            Log.e(TAG, "TTS initialization failed")
        }
    }
    
    private fun setupTtsListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                Log.d(TAG, "TTS started: $utteranceId")
            }
            
            override fun onDone(utteranceId: String?) {
                Log.d(TAG, "TTS completed: $utteranceId")
            }
            
            override fun onError(utteranceId: String?) {
                Log.e(TAG, "TTS error: $utteranceId")
            }
        })
    }
    
    private fun loadPreferences() {
        runBlocking {
            val prefs = context.dataStore.data.first()
            _isEnabled.value = prefs[VOICE_ENABLED_KEY] ?: false
            _includeFormat = prefs[VOICE_INCLUDE_FORMAT_KEY] ?: true
            _volume = prefs[VOICE_VOLUME_KEY] ?: 1.0f
            
            prefs[VOICE_LANGUAGE_KEY]?.let { langCode ->
                if (langCode.isNotEmpty()) {
                    val locale = when {
                        langCode.contains("_") -> Locale(langCode.substringBefore("_"), langCode.substringAfter("_"))
                        else -> Locale(langCode)
                    }
                    tts?.setLanguage(locale)
                }
            }
        }
    }
    
    fun setEnabled(enabled: Boolean) {
        _isEnabled.value = enabled
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[VOICE_ENABLED_KEY] = enabled
            }
        }
    }
    
    fun setIncludeFormat(include: Boolean) {
        _includeFormat = include
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[VOICE_INCLUDE_FORMAT_KEY] = include
            }
        }
    }
    
    fun setVolume(volume: Float) {
        _volume = volume.coerceIn(0f, 1f)
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[VOICE_VOLUME_KEY] = _volume
            }
        }
    }
    
    fun setLanguage(locale: Locale) {
        tts?.setLanguage(locale)
        val langCode = if (locale.country.isNotEmpty()) {
            "${locale.language}_${locale.country}"
        } else {
            locale.language
        }
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[VOICE_LANGUAGE_KEY] = langCode
            }
        }
    }
    
    fun speakBarcode(barcode: String, formatName: String) {
        if (!isInitialized || !_isEnabled.value) {
            return
        }
        
        val text = buildString {
            append(barcode)
            if (_includeFormat && formatName.isNotEmpty() && formatName != "UNKNOWN") {
                append(". ")
                append(context.getString(R.string.voice_format, formatName))
            }
        }
        
        speak(text)
    }
    
    fun speak(text: String) {
        if (!isInitialized || !_isEnabled.value) {
            return
        }
        
        val utteranceId = UUID.randomUUID().toString()
        
        val params = android.os.Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, _volume)
        }
        
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }
    
    fun speakSuccess() {
        speak(context.getString(R.string.voice_success))
    }
    
    fun stop() {
        tts?.stop()
    }
    
    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
