package dev.defvs.pufsim.arbiter

import dev.defvs.pufsim.Bit
import dev.defvs.pufsim.Delay
import dev.defvs.pufsim.getRandomDelay
import java.util.*

class PufStage(pufRandom: Random) {
	private val delayModel = PufStageDelayModel(pufRandom)
	
	data class DelayedBit(val bit: Bit, val delay: Double = 0.0)
	data class PufData(
		val top: DelayedBit = DelayedBit(true, 0.0),
		val bottom: DelayedBit = DelayedBit(true, 0.0),
	)
	
	fun run(input: PufData, challenge: Bit): PufData {
		var (bitTop, delayTop) = input.top
		var (bitBottom, delayBottom) = input.bottom
		
		delayTop += delayModel.topMux
		delayBottom += delayModel.bottomMux
		
		return if (!challenge) {
			delayTop += delayModel.parallelPathTop
			delayBottom += delayModel.parallelPathBottom
			PufData(DelayedBit(bitTop, delayTop), DelayedBit(bitBottom, delayBottom))
		} else {
			delayTop += delayModel.crossoverPathDownwards
			delayBottom += delayModel.crossoverPathUpwards
			PufData(DelayedBit(bitBottom, delayBottom), DelayedBit(bitTop, delayTop))
		}
	}
}

data class PufStageDelayModel(
	private val random: Random = Random(),
	val parallelPathTop: Delay = getRandomDelay(random = random),
	val parallelPathBottom: Delay = getRandomDelay(random = random),
	val crossoverPathUpwards: Delay = getRandomDelay(random = random),
	val crossoverPathDownwards: Delay = getRandomDelay(random = random),
	val topMux: Delay = getRandomDelay(random = random),
	val bottomMux: Delay = getRandomDelay(random = random),
)
