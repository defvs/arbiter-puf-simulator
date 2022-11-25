package dev.defvs.pufsim.arbiter

class PufArbiter {
	fun response(input: PufStage.PufData) = input.top.delay > input.bottom.delay
}