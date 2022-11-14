package dev.defvs.pufsim

import java.util.*
import kotlin.math.absoluteValue

typealias Delay = Double
typealias PufChallenge = BitSet
typealias Bit = Boolean
typealias CRP = Pair<PufChallenge, Bit>

fun Long.toBitSet(): BitSet = BitSet.valueOf(longArrayOf(this))
fun BitSet.toBinaryString() = (0 until this.size()).joinToString(separator = "") { if (this[it]) "1" else "0" }

fun getRandomDelay(mean: Double = 0.0, stdDev: Double = 1.0, random: Random = Random()) =
	random.nextGaussian(mean, stdDev).absoluteValue
