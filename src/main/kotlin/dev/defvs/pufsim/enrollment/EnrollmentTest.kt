package dev.defvs.pufsim.enrollment

fun main() {
	val server = Server()
	
	val clientsCount = 10
	
	val clients = (0 until clientsCount).map { Client() }
	
	clients.forEach { it.startEnrollment(server) }
	val authenticationResults = clients.map { it.startAuthentication(server) }
	
	println("${authenticationResults.count { it }} / $clientsCount clients were successfully authenticated.")
}