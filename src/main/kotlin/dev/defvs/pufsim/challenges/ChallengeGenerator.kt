package dev.defvs.pufsim.challenges

import dev.defvs.pufsim.toBitSet
import java.util.BitSet
import java.util.Random

typealias Challenge = BitSet

object ChallengeGenerator {
	fun generateChallenge(size: Int, random: Random = Random()): List<Challenge> =
		(1..size).map { random.nextLong().toBitSet() }
}
