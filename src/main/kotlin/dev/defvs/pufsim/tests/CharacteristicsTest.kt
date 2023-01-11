package dev.defvs.pufsim.tests

import dev.defvs.pufsim.CRP
import dev.defvs.pufsim.arbiter.Puf
import dev.defvs.pufsim.challenges.ChallengeGenerator
import dev.defvs.pufsim.io.writeToCSV
import mu.KotlinLogging
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

private val logger = KotlinLogging.logger {}

@OptIn(ExperimentalTime::class)
fun main() {
	val crps: List<List<CRP>>
	
	measureTime {
		// Counts
		val pufCount = 10
		val challengesCount = 10_000
		
		// Seeds
		val pufSeed = 1L
		val challengeSeed = 2L
		
		crps = generateCRPs(challengeSeed, pufSeed, challengesCount, pufCount)
	}.also { logger.debug("CRP generation took $it") }
	
	// Write to CSV
	crps.writeToCSV()
	// Calculate characteristics
	PufCharacteristics.calculateCharacteristics(crps)
}

fun generateCRPs(
	challengeSeed: Long,
	pufSeed: Long,
	challengesCount: Int,
	pufCount: Int,
	jitterDev: Double = 0.0,
): List<List<CRP>> {
	// Generate challenges and PUFs
	val challengeGenerationRandom = Random(challengeSeed)
	val pufGenerationRandom = Random(pufSeed)
	val challenges = ChallengeGenerator.generateChallenge(challengesCount, challengeGenerationRandom)
	val pufInstances = (1..pufCount).map { Puf(pufGenerationRandom.nextLong(), jitterDev) }
	
	// Run the challenges through the PUFs
	return pufInstances.map { instance ->
		challenges.associateWith { challenge -> instance.getResponse(challenge) }.toList()
	}
}