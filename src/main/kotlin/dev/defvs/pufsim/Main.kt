package dev.defvs.pufsim

import dev.defvs.pufsim.components.Puf
import java.io.File
import kotlin.random.Random

fun main() {
	// Counts
	val pufCount = 10
	val challengesCount = 10_000
	
	// Seeds
	val pufSeed = 1
	val challengeSeed = 2
	
	// Generate challenges and PUFs
	val challengeGenerationRandom = Random(challengeSeed)
	val pufGenerationRandom = Random(pufSeed)
	val challenges = (1..challengesCount).map { challengeGenerationRandom.nextLong().toBitSet() }
	val pufInstances = (1..pufCount).map { Puf(pufGenerationRandom.nextLong()) }
	
	// Run the challenges through the PUFs
	val crpList: List<List<CRP>> = pufInstances.map { instance ->
		challenges.associateWith { challenge -> instance.getResponse(challenge) }.toList()
	}
	
	// Output to csv
	crpList.forEachIndexed { i, pufCRPs ->
		val text = pufCRPs.joinToString(separator = "\n") {
			"${it.first.toBinaryString(",")},${if (it.second) "1" else "0"}"
		}
		File("puf${i}.csv").writeText(text)
	}
}