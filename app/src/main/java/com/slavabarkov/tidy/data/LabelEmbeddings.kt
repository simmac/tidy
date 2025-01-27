package com.slavabarkov.tidy.data

import android.content.res.Resources
import com.slavabarkov.tidy.dot
import java.nio.ByteBuffer
import java.nio.ByteOrder
import com.slavabarkov.tidy.R

data class LabelResult(
    val name: String,
    val prob: Float,
)

class LabelEmbeddings(resources: Resources) {
    private var labelValues : MutableMap<String, FloatArray> = mutableMapOf()

    init {
        val embeddingBytes = resources.openRawResource(R.raw.label_values).readBytes()
        val classNames = resources.openRawResource(R.raw.imagenet_classes).readBytes().decodeToString().split("\n")
        for (l in 0..999) {
            val floats = FloatArray(512);
            for (b in 0..127) {
                floats[b] = ByteBuffer.wrap(embeddingBytes, l*512*4+b*4, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                // = buf.getFloat(b*4);
            }
            labelValues.put(classNames[l], floats);
        }
    }

    fun findClosestLabels(embedding: FloatArray): List<LabelResult> {
        return labelValues.toList().
        map { (label, labelEmb) -> LabelResult(label, labelEmb.dot(embedding)) }.
        sortedBy { (_, prob) -> prob }.
        takeLast(5).
        reversed()
    }
}