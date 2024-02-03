package com.example.pitchcontroller.models

import android.content.Context
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.example.pitchcontroller.R

data class Preset(
    @StringRes
    val id: Int,
    val map: Map<Int, Float>
)

val presetList = arrayListOf("Normal", "Classical", "Dance", "Flat", "Folk", "Heavy Metal", "Hip Hop", "Jazz", "Pop", "Rock", "Acoustic", "Vocal Boost", "Bass Boost", "Treble Boost",  "Deep House", "EDM", "RnB", "Dream")

val presets = listOf(
    Preset(
        R.string.preset_normal,
        mapOf(
            Frequencies.LOW to 0.00f,
            Frequencies.BOTTOM to 0.00f,
            Frequencies.THUMB to 0.00f,
            Frequencies.MUD to 0.00f,
            Frequencies.HONK to 0.00f,
            Frequencies.WHACK to 0.00f,
            Frequencies.CRUNCH to 0.00f,
            Frequencies.EDGE to 0.00f,
            Frequencies.DEFINITION to 0.00f,
            Frequencies.AIR to 0.00f
        )
    ),

    Preset(
        R.string.preset_classical,
        mapOf(
            Frequencies.LOW to 5.00f,
            Frequencies.BOTTOM to 4.00f,
            Frequencies.THUMB to 3.00f,
            Frequencies.MUD to 1.00f,
            Frequencies.HONK to 2.00f,
            Frequencies.WHACK to 1.00f,
            Frequencies.CRUNCH to 3.00f,
            Frequencies.EDGE to 4.00f,
            Frequencies.DEFINITION to 4.00f,
            Frequencies.AIR to 5.00f
        )
    ),

    Preset(
        R.string.preset_dance,
        mapOf(
            Frequencies.LOW to 6.00f,
            Frequencies.BOTTOM to 5.00f,
            Frequencies.THUMB to 3.00f,
            Frequencies.MUD to 1.00f,
            Frequencies.HONK to 0.00f,
            Frequencies.WHACK to 2.00f,
            Frequencies.CRUNCH to 3.00f,
            Frequencies.EDGE to 4.00f,
            Frequencies.DEFINITION to 2.00f,
            Frequencies.AIR to 1.00f
        )
    ),

    Preset(
        R.string.preset_flat,
        mapOf(
            Frequencies.LOW to 0.00f,
            Frequencies.BOTTOM to 0.00f,
            Frequencies.THUMB to 0.00f,
            Frequencies.MUD to 0.00f,
            Frequencies.HONK to 0.00f,
            Frequencies.WHACK to 0.00f,
            Frequencies.CRUNCH to 0.00f,
            Frequencies.EDGE to 0.00f,
            Frequencies.DEFINITION to 0.00f,
            Frequencies.AIR to 0.00f
        )
    ),

    Preset(
        R.string.preset_folk,
        mapOf(
            Frequencies.LOW to 3.00f,
            Frequencies.BOTTOM to 2.00f,
            Frequencies.THUMB to 1.00f,
            Frequencies.MUD to 0.50f,
            Frequencies.HONK to 0.00f,
            Frequencies.WHACK to 0.00f,
            Frequencies.CRUNCH to 2.00f,
            Frequencies.EDGE to 2.00f,
            Frequencies.DEFINITION to 0.00f,
            Frequencies.AIR to -1.00f
        )
    ),

    Preset(
        R.string.preset_heavy_metal,
        mapOf(
            Frequencies.LOW to 4.00f,
            Frequencies.BOTTOM to 3.00f,
            Frequencies.THUMB to 2.00f,
            Frequencies.MUD to 1.00f,
            Frequencies.HONK to 5.00f,
            Frequencies.WHACK to 9.00f,
            Frequencies.CRUNCH to 8.00f,
            Frequencies.EDGE to 5.00f,
            Frequencies.DEFINITION to 3.00f,
            Frequencies.AIR to 1.00f
        )
    ),

    Preset(
        R.string.preset_hip_hop,
        mapOf(
            Frequencies.LOW to 5.00f,
            Frequencies.BOTTOM to 4.00f,
            Frequencies.THUMB to 3.60f,
            Frequencies.MUD to 3.20f,
            Frequencies.HONK to 3.00f,
            Frequencies.WHACK to 0.00f,
            Frequencies.CRUNCH to 0.50f,
            Frequencies.EDGE to 1.00f,
            Frequencies.DEFINITION to 2.00f,
            Frequencies.AIR to 3.00f
        )
    ),

    Preset(
        R.string.preset_jazz,
        mapOf(
            Frequencies.LOW to 4.00f,
            Frequencies.BOTTOM to 3.00f,
            Frequencies.THUMB to 2.00f,
            Frequencies.MUD to 1.00f,
            Frequencies.HONK to 0.00f,
            Frequencies.WHACK to -2.00f,
            Frequencies.CRUNCH to 0.00f,
            Frequencies.EDGE to 2.00f,
            Frequencies.DEFINITION to 4.00f,
            Frequencies.AIR to 5.00f
        )
    ),

    Preset(
        R.string.preset_pop,
        mapOf(
            Frequencies.LOW to -1.00f,
            Frequencies.BOTTOM to 0.00f,
            Frequencies.THUMB to 2.00f,
            Frequencies.MUD to 3.00f,
            Frequencies.HONK to 4.00f,
            Frequencies.WHACK to 5.00f,
            Frequencies.CRUNCH to 3.00f,
            Frequencies.EDGE to 1.00f,
            Frequencies.DEFINITION to 0.00f,
            Frequencies.AIR to -2.00f
        )
    ),

    Preset(
        R.string.preset_rock,
        mapOf(
            Frequencies.LOW to 5.00f,
            Frequencies.BOTTOM to 4.50f,
            Frequencies.THUMB to 4.00f,
            Frequencies.MUD to 3.00f,
            Frequencies.HONK to 1.00f,
            Frequencies.WHACK to -1.00f,
            Frequencies.CRUNCH to 1.00f,
            Frequencies.EDGE to 3.00f,
            Frequencies.DEFINITION to 4.00f,
            Frequencies.AIR to 5.00f
        )
    ),

    Preset(
        R.string.preset_acoustic,
        mapOf(
            Frequencies.LOW to 7.00f,
            Frequencies.BOTTOM to 6.00f,
            Frequencies.THUMB to 5.00f,
            Frequencies.MUD to 4.00f,
            Frequencies.HONK to 1.00f,
            Frequencies.WHACK to 0.00f,
            Frequencies.CRUNCH to 2.00f,
            Frequencies.EDGE to 4.00f,
            Frequencies.DEFINITION to 4.00f,
            Frequencies.AIR to 4.00f
        )
    ),

    Preset(
        R.string.preset_vocal_boost,
        mapOf(
            Frequencies.LOW to -3.00f,
            Frequencies.BOTTOM to -2.00f,
            Frequencies.THUMB to -1.50f,
            Frequencies.MUD to -1.00f,
            Frequencies.HONK to 1.00f,
            Frequencies.WHACK to 4.00f,
            Frequencies.CRUNCH to 3.50f,
            Frequencies.EDGE to 3.00f,
            Frequencies.DEFINITION to 2.50f,
            Frequencies.AIR to 2.00f
        )
    ),

    Preset(
        R.string.preset_bass_boost,
        mapOf(
            Frequencies.LOW to 10.00f,
            Frequencies.BOTTOM to 7.00f,
            Frequencies.THUMB to 4.00f,
            Frequencies.MUD to 3.00f,
            Frequencies.HONK to 2.00f,
            Frequencies.WHACK to 0.00f,
            Frequencies.CRUNCH to 0.00f,
            Frequencies.EDGE to 0.00f,
            Frequencies.DEFINITION to 0.00f,
            Frequencies.AIR to 0.00f
        )
    ),

    Preset(
        R.string.preset_treble_boost,
        mapOf(
            Frequencies.LOW to 0.00f,
            Frequencies.BOTTOM to 0.00f,
            Frequencies.THUMB to 0.00f,
            Frequencies.MUD to 0.00f,
            Frequencies.HONK to 0.00f,
            Frequencies.WHACK to 1.00f,
            Frequencies.CRUNCH to 2.00f,
            Frequencies.EDGE to 3.00f,
            Frequencies.DEFINITION to 5.00f,
            Frequencies.AIR to 6.00f
        )
    ),

    Preset(
        R.string.preset_deep_house,
        mapOf(
            Frequencies.LOW to 6.00f,
            Frequencies.BOTTOM to 6.00f,
            Frequencies.THUMB to 5.00f,
            Frequencies.MUD to 4.00f,
            Frequencies.HONK to 0.00f,
            Frequencies.WHACK to -1.00f,
            Frequencies.CRUNCH to 1.00f,
            Frequencies.EDGE to 4.00f,
            Frequencies.DEFINITION to 2.00f,
            Frequencies.AIR to -1.00f
        )
    ),

    Preset(
        R.string.preset_edm,
        mapOf(
            Frequencies.LOW to 6.00f,
            Frequencies.BOTTOM to 4.00f,
            Frequencies.THUMB to 2.00f,
            Frequencies.MUD to 0.00f,
            Frequencies.HONK to 1.00f,
            Frequencies.WHACK to 3.00f,
            Frequencies.CRUNCH to 1.00f,
            Frequencies.EDGE to 0.00f,
            Frequencies.DEFINITION to -0.50f,
            Frequencies.AIR to -1.00f
        )
    ),

    Preset(
        R.string.preset_rnb,
        mapOf(
            Frequencies.LOW to 4.00f,
            Frequencies.BOTTOM to 3.00f,
            Frequencies.THUMB to 2.00f,
            Frequencies.MUD to 1.00f,
            Frequencies.HONK to 1.00f,
            Frequencies.WHACK to 1.00f,
            Frequencies.CRUNCH to 3.00f,
            Frequencies.EDGE to 5.00f,
            Frequencies.DEFINITION to 5.00f,
            Frequencies.AIR to 5.00f
        )
    ),

    Preset(
        R.string.preset_dream,
        mapOf(
            Frequencies.LOW to 8.00f,
            Frequencies.BOTTOM to 7.00f,
            Frequencies.THUMB to 6.00f,
            Frequencies.MUD to 1.00f,
            Frequencies.HONK to 0.00f,
            Frequencies.WHACK to -4.00f,
            Frequencies.CRUNCH to -4.50f,
            Frequencies.EDGE to -5.00f,
            Frequencies.DEFINITION to -5.50f,
            Frequencies.AIR to -6.00f
        )
    )


)
