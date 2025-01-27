/**
 * Copyright 2023 Viacheslav Barkov
 * Copyright 2021 Microsoft Corporation.
 *
 * Parts of the following code are a derivative work of the code from the ONNX Runtime project,
 * which is licensed MIT.
 */

package com.slavabarkov.tidy

import android.graphics.*
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

const val DIM_BATCH_SIZE = 1
const val DIM_PIXEL_SIZE = 3
const val IMAGE_SIZE_X = 224
const val IMAGE_SIZE_Y = 224

fun preProcess(bitmap: Bitmap): ByteBuffer {
    val imgData = ByteBuffer.allocate(
        DIM_BATCH_SIZE * DIM_PIXEL_SIZE * bitmap.width * bitmap.height
    )
    imgData.rewind()
    val stride = bitmap.width * bitmap.height
    val bmpData = IntArray(stride)
    bitmap.getPixels(bmpData, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
    for (i in 0 until bitmap.width) {
        for (j in 0 until bitmap.height) {
            val idx = i*bitmap.height + j
            val pixelValue = bmpData[idx]
            imgData.put(idx*3, (pixelValue shr 16 and 0xFF).toByte())
            imgData.put(idx*3 + 1, (pixelValue shr 8 and 0xFF).toByte())
            imgData.put(idx*3 + 2, (pixelValue and 0xFF).toByte())
        }
    }

    imgData.rewind()
    return imgData
}

fun centerCrop(bitmap: Bitmap, imageSize: Int): Bitmap {
    val cropX: Int
    val cropY: Int
    val cropSize: Int
    if (bitmap.width >= bitmap.height) {
        cropX = bitmap.width / 2 - bitmap.height / 2
        cropY = 0
        cropSize = bitmap.height
    } else {
        cropX = 0
        cropY = bitmap.height / 2 - bitmap.width / 2
        cropSize = bitmap.width
    }
    var bitmapCropped = Bitmap.createBitmap(
        bitmap, cropX, cropY, cropSize, cropSize
    )
    bitmapCropped = Bitmap.createScaledBitmap(
        bitmapCropped, imageSize, imageSize, false
    )
    return bitmapCropped
}