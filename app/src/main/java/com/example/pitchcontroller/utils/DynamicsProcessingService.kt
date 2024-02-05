package com.example.pitchcontroller.utils

import android.media.AudioManager
import android.media.audiofx.BassBoost
import android.media.audiofx.DynamicsProcessing
import android.media.audiofx.LoudnessEnhancer
import android.media.audiofx.Virtualizer
import android.util.Log

private const val TAG = "DynamicAudioProcessor"

data class Gain(
    var curGain: Float = 0f,
    var savedGain: Float = 0f
)

class DynamicsProcessingService {


    private val centerFrequency = arrayListOf(
        31F, 62F, 125F, 250F, 500F, 1000F, 2000F, 4000F, 8000F, 16000F, 20000F
    )

    private val channelCount = 2
    private val preEqInUse = true
    private val preEqBandCount = 10
    private val mbcInUse = true
    private val mbcBandCount = 0
    private val postEqInUse = true
    private val postEqBandCount = 10
    private val limiterInUse = false

    private var dynamicsProcessing: DynamicsProcessing? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null
    private var loudnessEnhancer: LoudnessEnhancer? = null



    private val equalizerGains = mutableListOf<Float>(
        0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F
    )
    private var bassBoostGain = Gain()
    private var loudnessGain = Gain()

    init {
        val audioSessionId = AudioManager.AUDIO_SESSION_ID_GENERATE

        val config = DynamicsProcessing.Config.Builder(
            DynamicsProcessing.VARIANT_FAVOR_FREQUENCY_RESOLUTION,
            channelCount, preEqInUse, preEqBandCount, mbcInUse,
            mbcBandCount, postEqInUse, postEqBandCount, limiterInUse
        ).build()

        dynamicsProcessing = DynamicsProcessing(0, audioSessionId, config)

        bassBoost = BassBoost(2, audioSessionId)
        val mode = Virtualizer.VIRTUALIZATION_MODE_BINAURAL
        virtualizer?.forceVirtualizationMode(mode)
        virtualizer = Virtualizer(1, audioSessionId)
        loudnessEnhancer = LoudnessEnhancer(audioSessionId)

        for (i in 0..9) {
            dynamicsProcessing?.setPreEqBandAllChannelsTo(
                i,
                DynamicsProcessing.EqBand(
                    true,
                    getCufOffFrequency(i),
                    0F
                )
            )

            dynamicsProcessing?.setPostEqBandAllChannelsTo(
                i,
                DynamicsProcessing.EqBand(
                    true,
                    getCufOffFrequency(i),
                    0F
                )
            )
        }
        dynamicsProcessing?.enabled = true
    }

    /*Equalizer Methods*/
    fun setEqualizerEnabled(setting: Boolean) {
        val gain = mutableListOf<Float>()
        if(!setting) {
            gain.addAll(listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f))
        } else {
            for(i in 0..9){
                gain.addAll(equalizerGains)
            }
        }

        for(i in 0..9) {
            dynamicsProcessing?.setPreEqBandAllChannelsTo(
                i,
                DynamicsProcessing.EqBand(
                    true,
                    getCufOffFrequency(i),
                    gain[i]
                )
            )
        }
    }

    fun setEqualizerGainByIndex(index: Int, gain: Float) {
        dynamicsProcessing?.setPreEqBandAllChannelsTo(
            index,
            DynamicsProcessing.EqBand(
                true,
                getCufOffFrequency(index),
                gain
            )
        )
        equalizerGains[index] = gain
    }


    /*Bass Boost Methods*/
    fun setBassBoostEnabled(setting: Boolean) {
        bassBoostGain.curGain =
            if(setting) bassBoostGain.savedGain
            else 0f

        setPostEqStrength()
    }

    fun setBassBoostStrength(strength: Int) {
        bassBoostGain.savedGain = strength.toFloat() * 15 / 100
        bassBoostGain.curGain = bassBoostGain.savedGain
        setPostEqStrength()
    }

    /*Loudness Methods*/
    fun setLoudnessEnhancerEnabled(setting: Boolean) {
        loudnessGain.curGain =
            if(setting) loudnessGain.savedGain
            else 0f

        setPostEqStrength()
    }

    fun setLoudnessEnhancerStrength(strength: Int) {
        loudnessGain.savedGain  = strength.toFloat() * 15 / 100

        loudnessGain.curGain = loudnessGain.savedGain
        setPostEqStrength()
    }

    private fun setPostEqStrength(
        bassBoost:Float = bassBoostGain.curGain,
        loudness:Float = loudnessGain.curGain) {
        for(index in 0..9){
            var gain = loudness

            if(index <= 1){
                gain += bassBoost
            }

            dynamicsProcessing?.setPostEqBandAllChannelsTo(
                index,
                DynamicsProcessing.EqBand(true, getCufOffFrequency(index), gain)
            )
        }
    }

    fun getCufOffFrequency(index: Int): Float {
        return centerFrequency[index] + centerFrequency[index + 1] /2
    }



    /*Virtualizer Methods*/
    fun setVirtualizerEnable(setting: Boolean) {
        virtualizer?.enabled = setting
    }

    fun setVirtualizerStrength(strength: Int) {
        val num = strength * 10
        virtualizer?.setStrength(num.toShort())


        Log.d(TAG, virtualizer?.roundedStrength.toString())
    }

}