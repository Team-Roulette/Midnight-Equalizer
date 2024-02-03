package com.example.pitchcontroller.data

data class Preset(
    val gains:Array<Float>
)

object Presets {
    val presetList = arrayListOf<String>("Normal", "Classical", "Dance", "Flat", "Heavy Metal", "Hip Hop", "Jazz", "Pop", "Rock", "Acoustic", "Vocal Boost", "Treble Boost", "Bass Boost", "Deep House", "EDM", "R&B", "Dream")

    private val normal = Preset(arrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f))
    private val classicalPreset =
        Preset(arrayOf(5.0f, 4.0f, 3.0f, 1.0f, -2.0f, 1.0f, 3.0f, 4.0f, 4.0f, 5.0f))
    private val dancePreset = Preset(arrayOf(6.0f, 5.0f, 3.0f, 1.0f, 0.0f, 2.0f, 3.0f, 4.0f, 2.0f, 1.0f))
    private val flatPreset = Preset(arrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f))
    private val folkPreset = Preset(arrayOf(3.0f, 2.0f, 1.0f, 0.5f, 0.0f, 0.0f, 2.0f, 2.0f, 0.0f, -1.0f))
    private val heavyMetalPreset =
        Preset(arrayOf(4.0f, 3.0f, 2.0f, 1.0f, 4.0f, 9.0f, 8.0f, 5.0f, 3.0f, 0.0f))
    private val hipHopPreset = Preset(arrayOf(5.0f, 4.0f, 3.6f, 3.2f, 3.0f, 0.0f, 0.5f, 1.0f, 2.0f, 3.0f))
    private val jazzPreset = Preset(arrayOf(4.0f, 3.0f, 2.0f, 1.0f, 0.0f, -2.0f, 0.0f, 2.0f, 4.0f, 5.0f))
    private val popPreset = Preset(arrayOf(-1.0f, 40.0f, 2.0f, 3.0f, 4.0f, 5.0f, 3.0f, 1.0f, 0.0f, -2.0f))
    private val rockPreset = Preset(arrayOf(5.0f, 4.5f, 4.0f, 3.0f, 1.0f, -1.0f, 1.0f, 3.0f, 4.0f, 5.0f))
    private val acousticPreset = Preset(arrayOf(7.0f, 6.0f, 5.0f, 3.0f, 1.0f, 0.0f, 2.0f, 4.0f, 4.0f, 4.0f))
    private val vocalBoostPreset =
        Preset(arrayOf(-3.0f, -2.0f, -1.5f, -1.0f, 1.0f, 4.0f, 3.5f, 3.0f, 2.5f, 2.0f))
    private val bassBoostPreset =
        Preset(arrayOf(10.0f, 7.0f, 4.0f, 3.0f, 2.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f))
    private val trebleBoostPreset =
        Preset(arrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 2.0f, 3.0f, 5.0f, 6.0f))
    private val deepHousePreset =
        Preset(arrayOf(6.0f, 6.0f, 5.0f, 4.0f, 0.0f, -1.0f, 1.0f, 4.0f, 2.0f, -1.0f))
    private val edmPreset = Preset(arrayOf(6.0f, 4.0f, 2.0f, 0.0f, 1.0f, 3.0f, 1.0f, 0.0f, -0.5f, -1.0f))
    private val rnbPreset = Preset(arrayOf(4.0f, 3.0f, 2.0f, 1.0f, 1.0f, 1.0f, 3.0f, 5.0f, 5.0f, 5.0f))
    private val dreamPreset =
        Preset(arrayOf(8.0f, 7.0f, 6.0f, 1.0f, 0.0f, -4.0f, -4.5f, -5.0f, -5.5f, -6.0f))

    val presets = arrayOf(
        normal, classicalPreset, dancePreset, flatPreset, folkPreset, heavyMetalPreset, hipHopPreset,
        jazzPreset, popPreset, rockPreset, acousticPreset, vocalBoostPreset, bassBoostPreset, trebleBoostPreset,
        deepHousePreset, edmPreset, rnbPreset, dreamPreset
    )
}
