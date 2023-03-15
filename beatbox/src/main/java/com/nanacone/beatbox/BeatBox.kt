package com.nanacone.beatbox

import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.media.SoundPool
import android.util.Log

private const val TAG = "BeatBox"
private const val SOUNDS_FOLDER = "sample_sounds"
private const val MAX_SOUNDS = 5
// 에셋 관리 클래스
class BeatBox(private val assets: AssetManager) {
    val sounds: List<Sound>
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(MAX_SOUNDS)  // 현재 시점에서 재생할 음원의 최대 개수
        .build()

    init {
        sounds = loadSounds()
    }

    // 음원 재생 함수
    fun play(sound: Sound) {
        sound.soundId?.let {
            /**
             * priority : stream 우선 순위(0이면 최저 우선순위)
             * loop : 반복 재생 여부(0: 반복 안함, -1: 무한 반복, 그 외: 숫자만큼 반복 횟수)
             * rate: 재생률(1: 녹음 속도 그대로, 2: 두 배 빠르게, 0.5: 절반 느리게)
             */
            soundPool.play(it, 1.0f, 1.0f, 1, 0, 1.0f)
        }
    }

    fun release() {
        soundPool.release() // 음원 재생 끝났을 때 release() 호출하여 SoundPool을 클린업(해제) 해야 함.
    }

    // asset 폴더에서 파일들을 불러오고, soundName 에 따라서 path를 지정하고 해당 path를 Sound 객체를 만들면서 넘겨줌
    // 그리고 sounds 리스트를 완성
    private fun loadSounds(): List<Sound> {
        val soundNames: Array<String>

        try {
            soundNames = assets.list(SOUNDS_FOLDER)!!
        } catch (e: Exception) {
            Log.e(TAG, "loadSounds: Cound not list assets", e)
            return emptyList()
        }
        val sounds = mutableListOf<Sound>() // 가변적
        soundNames.forEach { filename ->
            val assetPath = "$SOUNDS_FOLDER/$filename"
            val sound = Sound(assetPath)
//            sounds.add(sound)
            try {
                load(sound)
                sounds.add(sound)
            } catch (ioe: Exception) {
                Log.e(TAG, "loadSounds: Could not load sound $filename", ioe)
            }
        }

        return sounds
    }

    // SoundPool에 Sound 인스턴스를 load하기 위해 load 함수 추가
    private fun load(sound: Sound) {
        val afd: AssetFileDescriptor = assets.openFd(sound.assetPath) // SoundPool 을 사용할때는 InputStream 대신 FileDescriptor 필요
        val soundId = soundPool.load(afd, 1) // soundId 반환
        sound.soundId = soundId // 설정
    }
}