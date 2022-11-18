package dev.defvs.pufsim.components

import dev.defvs.pufsim.Bit
import dev.defvs.pufsim.PufChallenge
import java.util.*

class Puf(seed: Long) {
	private val random = Random(seed)
	private val stages: List<PufStage> = List(64) { PufStage(random) }
	private val arbiter = PufArbiter()
	
	fun getResponse(query: PufChallenge): Bit {
		var pufData = PufStage.PufData()
		stages.forEachIndexed { i, it ->
			pufData = it.run(pufData, query.get(i))
		}
		return arbiter.response(pufData)
	}
}