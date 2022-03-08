package com.waitfme.simplemusichd.service

import android.content.Context
import android.media.AudioManager

class AudioFocusManager(context: Context) : AudioManager.OnAudioFocusChangeListener {
    val audioPlayer = MusicPlayerService
    private var audioManager: AudioManager? = null
    private var isPauseByFocusLossTransient: Boolean? = false

    init {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    override fun onAudioFocusChange(p0: Int) {
        when(p0){
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (isPauseByFocusLossTransient == true) {
                    // 恢复播放
                }
                isPauseByFocusLossTransient = false
            }
        }
    }

    fun requestAudioFocus(): Boolean {
        return audioManager?.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    fun abandonAudioFocus() {
        audioManager?.abandonAudioFocus(this)
    }
}