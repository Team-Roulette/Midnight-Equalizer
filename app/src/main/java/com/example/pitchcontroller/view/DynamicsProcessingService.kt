package com.example.pitchcontroller.view

import android.content.Context
import android.media.AudioFormat
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.media.audiofx.BassBoost
import android.media.audiofx.DynamicsProcessing
import android.media.audiofx.LoudnessEnhancer
import android.media.audiofx.Virtualizer
import android.util.Log
import androidx.annotation.RawRes
import kotlinx.coroutines.flow.MutableStateFlow

private const val TAG = "DynamicAudioProcessor"

class DynamicsProcessingService (
    context: Context,
    @RawRes
    audioSource: Int,
) {
    private val centerFrequency = arrayListOf(
        31F, 62F, 125F, 250F, 500F, 1000F, 2000F, 4000F, 8000F, 16000F, 20000F
    )

    private val channelCount = 2
    private val preEqInUse = true
    private val preEqBandCount = 10
    private val mbcInUse = true
    private val mbcBandCount = 0
    private val postEqInUse = true
    private val postEqBandCount = 0
    private val limiterInUse = true
    private var audioSessionId = 0

    private var dynamicsProcessing: DynamicsProcessing? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null
    private var loudnessEnhancer: LoudnessEnhancer? = null

    var mediaPlayer: MediaPlayer ?= null
    init {
        mediaPlayer = MediaPlayer.create(context, audioSource)
        mediaPlayer?.isLooping = true
        if(mediaPlayer != null) {
            audioSessionId = mediaPlayer?.audioSessionId!!
            val config = DynamicsProcessing.Config.Builder(
                DynamicsProcessing.VARIANT_FAVOR_FREQUENCY_RESOLUTION,
                channelCount, preEqInUse, preEqBandCount, mbcInUse,
                mbcBandCount, postEqInUse, postEqBandCount, limiterInUse
            ).build()

            dynamicsProcessing = DynamicsProcessing(0, audioSessionId, config)
            bassBoost = BassBoost(1, audioSessionId)
            val mode = Virtualizer.VIRTUALIZATION_MODE_BINAURAL
            virtualizer?.forceVirtualizationMode(mode)
            virtualizer = Virtualizer(2, audioSessionId)
            loudnessEnhancer = LoudnessEnhancer(audioSessionId)

            for (i in 0..9) {
                dynamicsProcessing?.setPreEqBandAllChannelsTo(
                    i,
                    DynamicsProcessing.EqBand(
                        true,
                        (centerFrequency[i] + centerFrequency[i + 1]) / 2,
                        0F
                    )
                )

                dynamicsProcessing?.setPostEqBandAllChannelsTo(
                    i,
                    DynamicsProcessing.EqBand(
                        true,
                        (centerFrequency[i] + centerFrequency[i + 1]) / 2,
                        0F
                    )
                )
            }
        }

    }


    fun setEqualizerEnabled(setting:Boolean) {
        dynamicsProcessing?.enabled = setting
        printAll()
    }

    fun setEqualizerGainByIndex(index: Int, gain: Float) {
        dynamicsProcessing?.setPreEqBandAllChannelsTo(
            index,
            DynamicsProcessing.EqBand(true, centerFrequency[index], gain)
        )
    }

    fun setBassBoostEnable(setting: Boolean) {
        bassBoost?.enabled = setting
    }
    fun setBassBoostStrength(strength: Int) {
        val num = strength * 10
        bassBoost?.setStrength(num.toShort())
    }

    fun setVirtualizerEnable(setting: Boolean) {
        virtualizer?.enabled = setting
    }

    fun setVirtualizerStrength(strength: Int) {
        val num = strength * 10
        virtualizer?.setStrength(num.toShort())
    }

    fun setLoudnessEnhancerEnable(setting: Boolean) {
        loudnessEnhancer?.enabled = setting
        Log.d(TAG, loudnessEnhancer?.enabled.toString())
    }

    fun setLoudnessEnhancerStrength(strength: Int) {
        loudnessEnhancer?.setTargetGain(strength)
    }


    fun printAll() {
        val num1 = dynamicsProcessing?.getInputGainByChannelIndex(0)
        val num2 = dynamicsProcessing?.getInputGainByChannelIndex(0)
        Log.d(TAG, "${num1.toString()}, ${num2.toString()}")
    }
}