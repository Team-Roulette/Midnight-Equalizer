package com.example.pitchcontroller

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.util.Log

object AudioController {
    private lateinit var audioManager: AudioManager
    private lateinit var focusChangeListener: AudioManager.OnAudioFocusChangeListener
    private lateinit var audioFocusRequest: AudioFocusRequest

    fun init(context: Context) {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        focusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
            when(focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> Log.i("test", "Audio Focus Gained")
                AudioManager.AUDIOFOCUS_LOSS -> Log.i("test", "Audio Focus Lossed")
                // else...
            }
        }
        audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
            setAudioAttributes(AudioAttributes.Builder().run {
                setUsage(AudioAttributes.USAGE_MEDIA)
                setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                build()
            })
            setAcceptsDelayedFocusGain(true)
            setOnAudioFocusChangeListener(focusChangeListener)
            build()
        }
    }

//    fun registerAudioReceiver(context: Context) {
//        Log.i("test", "registerAudioReceiver")
//
//        val intentFilter = IntentFilter(Service.AUDIO_SERVICE).apply{
//            priority = Int.MAX_VALUE
//        }
//        val audioBroadcastReceiver = AudioBroadcastReceiver(audioManager)
//        registerReceiver(context, audioBroadcastReceiver, intentFilter, ContextCompat.RECEIVER_EXPORTED)
//    }

    fun requestAudioFocus() {
        Log.i("test", "requestAudioFocus")
        val result = audioManager.requestAudioFocus(audioFocusRequest)
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) Log.i("test", "Audio Focus Gained")
    }

    fun abandonAudioFocus(): Int {
        return audioManager.abandonAudioFocusRequest(audioFocusRequest)
    }
}