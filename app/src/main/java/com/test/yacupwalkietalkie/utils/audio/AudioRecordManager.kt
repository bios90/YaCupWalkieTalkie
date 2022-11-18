package com.test.yacupwalkietalkie.utils.audio

import android.media.AudioRecord
import com.test.yacupwalkietalkie.data.messages.MessageVoice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

class AudioRecordManager(
    private val recorder: AudioRecord,
    private val scope: CoroutineScope,
) {
    private var jobRecording: Job? = null
    private var recording: Boolean = false
    fun startRecording(onBytesReaded: (ByteArray) -> Unit) {
        jobRecording?.cancel()
        jobRecording = scope.launch(
            Dispatchers.IO,
            block = {
                recording = true
                recorder.startRecording()
                val audioBuffer = ByteArray(AudioManager.bufferRecordSize)
                var readLength = recorder.read(audioBuffer, 0, audioBuffer.size)
                while (recording && readLength != -1) {
                    yield()
                    onBytesReaded(audioBuffer)
                    readLength = recorder.read(audioBuffer, 0, audioBuffer.size)
                }
            }
        )
    }

    fun stopRecording() {
        recording = false
        jobRecording?.cancel()
        recorder.stop()
    }
}
