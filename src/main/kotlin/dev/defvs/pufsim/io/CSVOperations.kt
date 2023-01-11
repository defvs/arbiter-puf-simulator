package dev.defvs.pufsim.io

import dev.defvs.pufsim.CRP
import dev.defvs.pufsim.toBinaryString
import dev.defvs.pufsim.toBitSet
import java.io.File


fun List<List<CRP>>.writeToCSV() = this.forEachIndexed { i, pufCRPs ->
	val text = pufCRPs.joinToString(separator = "\n") {
		"${it.first.toBinaryString(",")},${if (it.second) "1" else "0"}"
	}
	File("puf${i}.csv").writeText(text)
}

fun readFromCsv(file: String): List<CRP> = File(file).readLines().map {
	val split = it.split(",")
	split.take(64).map { it == "1" }.toBitSet() to split.takeLast(1).single().let { it == "1" }
}