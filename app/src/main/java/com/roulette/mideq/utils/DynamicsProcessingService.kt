package com.roulette.mideq.utils

import android.media.audiofx.DynamicsProcessing

class DynamicsProcessingService(
    private val audioSessionId: Int,
    private val channelCount:Int,
    private val preEqInUse:Boolean,
    private val preEqBandCount: Int,
    private val mbcInUse:Boolean,
    private val mbcBandCount: Int,
    private val postEqInUse:Boolean,
    private val postEqBandCount: Int,
    private val limiterInUse:Boolean
) {
    private val centerFrequency = arrayOf(
        31F, 62F, 125F, 250F, 500F, 1000F, 2000F, 4000F, 8000F, 16000F, 20000F
    )

    var dynamicsProcessing: DynamicsProcessing? = null

    init {
        initDynamicProcessing()
        initPreEq()
        initPostEq()

        dynamicsProcessing?.enabled = true
    }

    private fun initDynamicProcessing() {
        val config = DynamicsProcessing.Config.Builder(
            DynamicsProcessing.VARIANT_FAVOR_FREQUENCY_RESOLUTION,
            channelCount, preEqInUse, preEqBandCount, mbcInUse,
            mbcBandCount, postEqInUse, postEqBandCount, limiterInUse
        ).build()
        dynamicsProcessing = DynamicsProcessing(0, audioSessionId, config)
    }

    private fun initPreEq() {
        for(index in 0..< preEqBandCount) {
            dynamicsProcessing?.setPreEqBandAllChannelsTo(
                index,
                DynamicsProcessing.EqBand(
                    true,
                    getCufOffFrequency(index),
                    0F
                )
            )
        }
    }

    private fun initPostEq() {
        for(index in 0 ..< postEqBandCount) {
            dynamicsProcessing?.setPostEqBandAllChannelsTo(
                index,
                DynamicsProcessing.EqBand(
                    true,
                    getCufOffFrequency(index),
                    0F
                )
            )
        }
    }


    fun setPreEqBandByIndex(index: Int, gain: Float) {
        dynamicsProcessing?.setPreEqBandAllChannelsTo(
            index,
            DynamicsProcessing.EqBand(
                true,
                getCufOffFrequency(index),
                gain
            )
        )
    }

    fun setPostEqStrength(bassBoostStrength:Float, loudnessStrength:Float) {
        for(index in 0..< postEqBandCount){
            var strength = loudnessStrength

            if(index <= 1){
                strength += bassBoostStrength
            }

            dynamicsProcessing?.setPostEqBandAllChannelsTo(
                index,
                DynamicsProcessing.EqBand(true, getCufOffFrequency(index), strength)
            )
        }
    }

    private fun getCufOffFrequency(index: Int): Float {
        return centerFrequency[index] + centerFrequency[index + 1] / 2
    }
}