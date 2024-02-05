package com.example.pitchcontroller.viewmodels

import android.media.AudioManager
import androidx.lifecycle.ViewModel
import com.example.pitchcontroller.utils.DynamicsProcessingService

private const val TAG = "MainViewModel"

data class Gain(
    var currentStrength: Float = 0f,
    var savedStrength: Float = 0f
)

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
        limiterInUse = false)

    private val _equalizerGains = mutableListOf(0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F)
    private var _bassBoostStrength = Gain()
    private var _loudnessStrength = Gain()

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

        _dynamicProcessing.setPostEqStrength(_bassBoostStrength.currentStrength, _loudnessStrength.currentStrength)
    }
    fun setBassBoostStrength(strength: Int) {
        _bassBoostStrength.savedStrength = strength.toFloat() * 15 / 100
        _bassBoostStrength.currentStrength = _bassBoostStrength.savedStrength

        if(isBassBoostEnabled)
            _dynamicProcessing.setPostEqStrength(_bassBoostStrength.currentStrength, _loudnessStrength.currentStrength)

    }

    /**
     * Loudness Modification Codes
     */
    fun setLoudnessEnabled(isEnabled: Boolean) {
        isLoudnessEnabled = isEnabled
        _loudnessStrength.currentStrength =
            if(isLoudnessEnabled) _loudnessStrength.savedStrength
            else 0f

        _dynamicProcessing.setPostEqStrength(_bassBoostStrength.currentStrength, _loudnessStrength.currentStrength)
    }
    fun setLoudnessStrength(strength: Int) {
        _loudnessStrength.savedStrength = strength.toFloat() * 15 / 100
        _loudnessStrength.currentStrength = _loudnessStrength.savedStrength

        if(isLoudnessEnabled)
            _dynamicProcessing.setPostEqStrength(_bassBoostStrength.currentStrength, _loudnessStrength.currentStrength)

    }

    fun releaseEqualizer() {
        if(_dynamicProcessing.dynamicsProcessing != null)
            _dynamicProcessing.dynamicsProcessing?.release()
    }


}