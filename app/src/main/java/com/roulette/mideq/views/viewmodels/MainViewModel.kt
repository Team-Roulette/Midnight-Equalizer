package com.roulette.mideq.views.viewmodels

import android.media.AudioManager
import androidx.lifecycle.ViewModel
import com.roulette.mideq.models.Strength
import com.roulette.mideq.utils.DynamicsProcessingService

private const val TAG = "MainViewModel"

class MainViewModel : ViewModel() {
    private var _dynamicProcessing = DynamicsProcessingService(
        audioSessionId = AudioManager.AUDIO_SESSION_ID_GENERATE,
        channelCount = 2,
        preEqInUse = true,
        preEqBandCount = 10,
        mbcInUse = true,
        mbcBandCount = 0,
        postEqInUse = true,
        postEqBandCount = 10,
        limiterInUse = false
    )

    private val _equalizerGains = mutableListOf(0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F)
    private var _bassBoostStrength = Strength()
    private var _loudnessStrength = Strength()

    private var isEqualizerEnabled = false
    private var isBassBoostEnabled = false
    private var isLoudnessEnabled = false


    /**
     * Equalizer Modification Codes
     */
    fun setEqualizerEnabled(isEnabled: Boolean) {
        isEqualizerEnabled = isEnabled
        val gain = if(!isEqualizerEnabled) {
            listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        } else {
            _equalizerGains
        }
        for(index in gain.indices){
            _dynamicProcessing.setPreEqBandByIndex(index, gain[index])
        }
    }
    fun setEqualizerGainByIndex(index: Int, gain: Float) {
        _equalizerGains[index] = gain

        if(isEqualizerEnabled) {
            _dynamicProcessing.setPreEqBandByIndex(index, gain)
        }
    }

    /**
     * BassBoost Modification Codes
     */
    fun setBassBoostEnabled(isEnabled: Boolean) {
        isBassBoostEnabled = isEnabled
        _bassBoostStrength.currentStrength =
            if(isBassBoostEnabled) _bassBoostStrength.savedStrength
            else 0f

        setPostEqStrength()
    }
    fun setBassBoostStrength(strength: Int) {
        _bassBoostStrength.savedStrength = strength.toFloat() * 15 / 100
        _bassBoostStrength.currentStrength = _bassBoostStrength.savedStrength

        if(isBassBoostEnabled)
            setPostEqStrength()
    }

    /**
     * Loudness Modification Codes
     */
    fun setLoudnessEnabled(isEnabled: Boolean) {
        isLoudnessEnabled = isEnabled
        _loudnessStrength.currentStrength =
            if(isLoudnessEnabled) _loudnessStrength.savedStrength
            else 0f

        setPostEqStrength()
    }
    fun setLoudnessStrength(strength: Int) {
        _loudnessStrength.savedStrength = strength.toFloat() * 15 / 100
        _loudnessStrength.currentStrength = _loudnessStrength.savedStrength

        if(isLoudnessEnabled)
            setPostEqStrength()

    }

    private fun setPostEqStrength() {
        _dynamicProcessing.setPostEqStrength(_bassBoostStrength.currentStrength, _loudnessStrength.currentStrength)
    }

    fun releaseEqualizer() {
        if(_dynamicProcessing.dynamicsProcessing != null)
            _dynamicProcessing.dynamicsProcessing?.release()
    }
}