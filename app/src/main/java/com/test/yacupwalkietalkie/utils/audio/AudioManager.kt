package com.test.yacupwalkietalkie.utils.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import com.test.yacupwalkietalkie.base.Consts

object AudioManager {
    val bufferRecordSize
        get() = getMinimumBufferSize()

    @SuppressLint("MissingPermission")
    fun getAudioRecorder(): AudioRecord? =
        AudioRecord(
            MediaRecorder.AudioSource.VOICE_RECOGNITION,
            Consts.AUDIO_SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferRecordSize
        ).takeIf { it.state == AudioRecord.STATE_INITIALIZED }

    fun getAudioPlayer(): AudioTrack {
        val bufferSize = AudioTrack.getMinBufferSize(
            Consts.AUDIO_SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).takeIf { it != AudioTrack.ERROR && it != AudioTrack.ERROR_BAD_VALUE }
            ?: Consts.AUDIO_SAMPLE_RATE * 2

        return AudioTrack(
            android.media.AudioManager.STREAM_MUSIC,
            Consts.AUDIO_SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize,
            AudioTrack.MODE_STREAM
        )
    }

    private fun getMinimumBufferSize(): Int {
        val size = AudioRecord.getMinBufferSize(
            Consts.AUDIO_SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        return if (size == AudioTrack.ERROR || size == AudioTrack.ERROR_BAD_VALUE) {
            Consts.AUDIO_SAMPLE_RATE * 2
        } else {
            size
        }
    }
}
