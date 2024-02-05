package com.example.pitchcontroller

import androidx.lifecycle.MutableLiveData

object AudioCaptureData {
    val isReady = MutableLiveData<Boolean>(false)
    val isRecording = MutableLiveData<Boolean>(false)
}