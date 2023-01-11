package dev.defvs.pufsim.tests

import dev.defvs.pufsim.CRP
import mu.KotlinLogging
import kotlin.math.pow

private val logger = KotlinLogging.logger {}

object PufCharacteristics {
	fun calculateCharacteristics(crpList: List<List<CRP>>) {
		// calculate randomness
		calculateRandomness(crpList).also { logger.info("Randomness = $it") }
		
		// calculate uniqueness
		calculateUniqueness(crpList).also { logger.info("Uniqueness = $it") }
		
		// calculate diffuseness
		calculateDiffuseness(crpList).also { logger.info("Diffuseness = $it") }
	}
	
	private fun calculateRandomness(crpList: List<List<CRP>>) = crpList.map { crps ->
		crps.map { if (it.second) 1 else 0 }.average()
	}.average()
	
	private fun calculateDiffuseness(crpList: List<List<CRP>>) = crpList.map {
		var diffusenessSum = 0.0
		for (i in 0 until it.lastIndex) {
			for (j in (i + 1)..it.lastIndex) {
				diffusenessSum += if (it[i].second xor it[j].second) 1 else 0
			}
		}
		diffusenessSum * 4.0 / it.size.toDouble().pow(2)
	}.average()
	
	private fun calculateUniqueness(crpList: List<List<CRP>>) = crpList[0].indices.sumOf {
		var uniquenessSum = 0.0
		for (i in 0 until crpList.lastIndex) {
			for (j in (i + 1)..crpList.lastIndex) {
				uniquenessSum += if (crpList[i][it].second xor crpList[j][it].second) 1 else 0
			}
		}
		uniquenessSum * 2.0 / (crpList.size * (crpList.size - 1))
	}.times(1.0 / crpList[0].size.toDouble())
}

