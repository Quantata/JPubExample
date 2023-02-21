package com.nancone.criminalintent

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.os.Build
import android.view.WindowManager
import android.view.WindowMetrics
import kotlin.math.roundToInt

// Bitmap 은 pixel 데이터를 저장하는 간단한 객체
// 원래 파일이 압축되어 있더라도 Bitmap 자체는 압축되어 있지 않음 -> Bitmap 의 크기를 직접 줄여야 함.
fun getScaledBitmap(path: String, destWidth: Int, destHeight: Int) : Bitmap {
    // 이미지 파일의 크기를 읽는다
    var options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path, options)

    val srcWidth = options.outWidth.toFloat()
    val srcHeight = options.outHeight.toFloat()

    // 크기를 얼마나 줄일지 파악한다
    var inSampleSize = 1
    if (srcHeight > destHeight || srcWidth > destWidth) {
        val heightScale = srcHeight / destHeight
        val widthScale = srcWidth / destWidth

        val sampleScale =
            if (heightScale > widthScale)
                heightScale
            else
                widthScale

        inSampleSize = sampleScale.roundToInt()
    }

    options = BitmapFactory.Options()
    options.inSampleSize = inSampleSize

    // 최종 Bitmap을 생성한다.
    return BitmapFactory.decodeFile(path, options)
}

// Fragment가 최초로 시작될 때는 PhotoView의 크기를 미리 알 수 없음.
// Fragment 의 onCreate()와 onStart(), onResume()이 차례대로 호출되어 실행된 후에 layout이 view객체로 생성되기 때문
// 이때문에 PhotoView의 크기를 알기위해 두가지 방법이 있음
// 1. 크기추정 2. layout이 view 객체로 생성될 때까지 기다리기
// 크기 추정 함수 = getScaledBitmap(String, Activity) -> 특정 Activity의 화면 크기에 맞춰 Bitmap 크기를 조정한다.
fun getScaledBitmap(path: String, activity: Activity): Bitmap {
    val size = Point()

    return if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
        // 화면 크기를 확인
        activity.windowManager.defaultDisplay.getSize(size) // defaultDisplay is deprecated

        // 확인한 화면의 크기에 맞춰 이미지크기를 getScaledBitmap(String, Int, Int) 함수를 호출
        getScaledBitmap(path, size.x, size.y)
    }
    else {
        val metrics: WindowMetrics =
            activity.getSystemService(WindowManager::class.java).currentWindowMetrics
        getScaledBitmap(path, metrics.bounds.width(), metrics.bounds.height())
    }

}