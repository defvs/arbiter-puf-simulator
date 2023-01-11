package dev.defvs.pufsim.tests

import dev.defvs.pufsim.enrollment.Client
import dev.defvs.pufsim.enrollment.Server
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main() {
	val server = Server()
	
	val clientsCount = 10
	
	val clients = (0 until clientsCount).map { Client(0.1) }
	
	clients.forEach { it.startEnrollment(server) }
	val authenticationResults = clients.map { it.startAuthentication(server) }
	
	logger.info("${authenticationResults.count { it }} / $clientsCount clients were successfully authenticated.")
}