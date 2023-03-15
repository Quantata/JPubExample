package com.nanacone.beatbox

private const val WAV = ".wav"
class Sound(val assetPath: String, var soundId: Int? = null) {
    // name: 경로 문자열의 맨 끝에 있는 파일 이름을 얻고, removeSuffix()를 통해 확장자(.wav) 제거
    val name = assetPath.split("/").last().removeSuffix(WAV)
}