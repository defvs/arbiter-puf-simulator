package dev.defvs.pufsim.components

class PufArbiter {
	fun response(input: PufStage.PufData) = input.top.delay > input.bottom.delay
}