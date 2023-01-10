package dev.defvs.pufsim

import dev.defvs.pufsim.arbiter.Puf
import dev.defvs.pufsim.challenges.ChallengeGenerator
import java.io.File
import java.util.*
import kotlin.math.pow
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
	measureTime {
		// Counts
		val pufCount = 10
		val challengesCount = 10_000
		
		// Seeds
		val pufSeed = 1L
		val challengeSeed = 2L
		
		generateCRPs(challengeSeed, pufSeed, challengesCount, pufCount).also {
			// Output to csv
			it.writeToCSV()
			
			// Calculate characteristics
			calculateCharacteristics(it)
		}
		
	}.also { println("Program took $it") }
	val readPUFs = (0 until 10).map { readFromCsv("input/puf-${it+1}.csv") }
	calculateCharacteristics(readPUFs)
}

fun generateCRPs(challengeSeed: Long, pufSeed: Long, challengesCount: Int, pufCount: Int): List<List<CRP>> {
	// Generate challenges and PUFs
	val challengeGenerationRandom = Random(challengeSeed)
	val pufGenerationRandom = Random(pufSeed)
	val challenges = ChallengeGenerator.generateChallenge(challengesCount, challengeGenerationRandom)
	val pufInstances = (1..pufCount).map { Puf(pufGenerationRandom.nextLong()) }
	
	// Run the challenges through the PUFs
	return pufInstances.map { instance ->
		challenges.associateWith { challenge -> instance.getResponse(challenge) }.toList()
	}
}

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

fun calculateCharacteristics(crpList: List<List<CRP>>) {
	// calculate randomness
	crpList.map {
		it.map { if (it.second) 1 else 0 }.average()
	}.average().also { println("Randomness =\n\t$it") }
	
	// calculate uniqueness
	crpList[0].indices.sumOf {
		var uniquenessSum = 0.0
		for (i in 0 until crpList.lastIndex) {
			for (j in (i + 1)..crpList.lastIndex) {
				uniquenessSum += if (crpList[i][it].second xor crpList[j][it].second) 1 else 0
			}
		}
		uniquenessSum * 2.0 / (crpList.size * (crpList.size - 1))
	}.times(1.0 / crpList[0].size.toDouble()).also { println("Uniqueness =\n\t$it") }
	
	// calculate diffuseness
	crpList.map {
		var diffusenessSum = 0.0
		for (i in 0 until it.lastIndex) {
			for (j in (i + 1)..it.lastIndex) {
				diffusenessSum += if (it[i].second xor it[j].second) 1 else 0
			}
		}
		diffusenessSum * 4.0 / it.size.toDouble().pow(2)
	}.average().also { println("Diffuseness =\n\t$it") }
}