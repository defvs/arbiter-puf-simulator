package dev.defvs.pufsim.arbiter

import dev.defvs.pufsim.Bit
import dev.defvs.pufsim.PufChallenge
import java.util.*

class Puf(seed: Long, private val jitterStdDev: Double = 0.0) {
	private val random = Random(seed)
	private val stages: List<PufStage> = List(64) { PufStage(random) }
	private val arbiter = PufArbiter()
	
	fun getResponse(query: PufChallenge): Bit {
		var pufData = PufStage.PufData()
		stages.forEachIndexed { i, it ->
			pufData = it.run(pufData, query.get(i), jitterStdDev)
		}
		return arbiter.response(pufData)
	}
}