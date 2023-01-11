package dev.defvs.pufsim.tests

import dev.defvs.pufsim.io.readFromCsv

fun main() {
	val readPUFs = (0 until 10).map { readFromCsv("input/puf-${it + 1}.csv") }
	PufCharacteristics.calculateCharacteristics(readPUFs)
}
