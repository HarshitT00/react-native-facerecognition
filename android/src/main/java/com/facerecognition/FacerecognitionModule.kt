package com.facerecognition

import com.facebook.react.bridge.ReactApplicationContext

class FacerecognitionModule(reactContext: ReactApplicationContext) :
  NativeFacerecognitionSpec(reactContext) {

  override fun multiply(a: Double, b: Double): Double {
    return a * b
  }

  companion object {
    const val NAME = NativeFacerecognitionSpec.NAME
  }
}
