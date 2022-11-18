package com.test.yacupwalkietalkie.base.di

import androidx.lifecycle.viewModelScope
import com.test.yacupwalkietalkie.screens.ActTalk.ActTalkVm
import com.test.yacupwalkietalkie.utils.audio.AudioManager
import com.test.yacupwalkietalkie.utils.audio.AudioRecordManager

class ActTalkInjector {
    private var audioRecordManager: AudioRecordManager? = null
    fun provideAudioRecordManager(
        vm: ActTalkVm,
    ): AudioRecordManager? {
        return if (audioRecordManager != null) {
            audioRecordManager
        } else {
            val recorder = AudioManager.getAudioRecorder() ?: return null
            AudioRecordManager(
                recorder = recorder,
                scope = vm.viewModelScope,
            ).also {
                audioRecordManager = it
            }
        }
    }
}
