package com.nanacone.beatbox

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

class SoundViewModel(private val beatBox: BeatBox) : BaseObservable() {
    fun onButtonClicked() {
        sound?.let {
            beatBox.play(it)
        }
    }

    var sound: Sound? = null
        set(sound) {
            field = sound
            notifyChange() // binding 속성값이 변경되었음을 ListItemSoundBinding 에 알린다.
        }

    @get:Bindable   // 속성값과 같은 바인딩 라이브러리 생성 ex)BR.title 상수 생성
    val title: String?
        get() = sound?.name
}