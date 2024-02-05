package com.example.pitchcontroller

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.session.MediaSession
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaButtonReceiver

class AudioBroadcastReceiver() : BroadcastReceiver() {

    @OptIn(UnstableApi::class) @RequiresApi(Build.VERSION_CODES.S)
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("test", "Broadcast Receiver ON")

//        val result = audioManager.requestAudioFocus(audioFocusRequest.build())

        val mediaSession = MediaSession(context!!, "TAG")
        mediaSession.currentControllerInfo
        val mediaButtonReceiver = ComponentName(context, MediaButtonReceiver::class.java)
        mediaSession.setMediaButtonBroadcastReceiver(mediaButtonReceiver)
        PendingIntent.getBroadcast(context, 0, Intent(Intent.ACTION_MEDIA_BUTTON).setComponent(mediaButtonReceiver),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }
}