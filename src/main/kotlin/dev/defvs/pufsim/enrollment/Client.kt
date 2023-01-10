package dev.defvs.pufsim.enrollment

import dev.defvs.pufsim.arbiter.Puf
import java.util.*

class Client {
	/** PUF instance for the client */
	private val puf = Puf(Random().nextLong())
	/** Unique identifier for the client */
	private val clientUUID = UUID.randomUUID()
	
	/**
	 * Starts the enrollment process with a server.
	 * @param server the server to enroll with
	 */
	fun startEnrollment(server: Server) {
		val challenges = server.getEnrollmentChallenges()
		val responses = challenges.map { puf.getResponse(it) }
		server.enroll(clientUUID, challenges.zip(responses))
	}
	
	/**
	 * Starts the authentication process with a server.
	 * @param server the server to authenticate with
	 * @return true if the authentication is successful, false otherwise
	 */
	fun startAuthentication(server: Server): Boolean {
		val challenges = server.getAuthenticationChallenges(clientUUID)
		val responses = challenges.map { puf.getResponse(it) }
		return server.authenticate(clientUUID, challenges.zip(responses))
	}
}
