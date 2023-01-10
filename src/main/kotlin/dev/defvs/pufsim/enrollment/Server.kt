package dev.defvs.pufsim.enrollment

import dev.defvs.pufsim.CRP
import dev.defvs.pufsim.challenges.ChallengeGenerator
import java.util.*

class Server {
	companion object {
		/** Number of challenges to generate during enrollment */
		private const val ENROLLMENT_CHALLENGE_COUNT = 10_000
		/** Number of challenges to generate during authentication */
		private const val AUTHENTICATION_CHALLENGE_COUNT = 100
		/** Number of correct responses needed to pass authentication */
		private const val AUTHENTICATION_THRESHOLD = 90
	}
	
	/** Map of client UUIDs to their enrolled CRP responses */
	private val clients = hashMapOf<UUID, List<CRP>>()
	
	/**
	 * Generates a set of enrollment challenges for the client to respond to.
	 * @return list of challenges
	 */
	fun getEnrollmentChallenges() =
		ChallengeGenerator.generateChallenge(ENROLLMENT_CHALLENGE_COUNT, Random())
	
	/**
	 * Enrolls a client by storing their CRP responses.
	 * @param clientUUID unique identifier of the client
	 * @param crps list of CRP responses
	 */
	fun enroll(clientUUID: UUID, crps: List<CRP>) = clients.set(clientUUID, crps)
	
	/**
	 * Generates a set of authentication challenges for the client to respond to.
	 * @param clientUUID unique identifier of the client
	 * @return list of challenges
	 * @throws IllegalArgumentException if the client is not enrolled
	 */
	fun getAuthenticationChallenges(clientUUID: UUID) =
		clients[clientUUID]?.shuffled()
			?.take(AUTHENTICATION_CHALLENGE_COUNT)
			?.map { it.first }
			?: throw IllegalArgumentException("Client is not enrolled")
	
	/**
	 * Authenticates a client based on their responses to the authentication challenges.
	 * @param clientUUID unique identifier of the client
	 * @param responses list of responses
	 * @return true if the client passes authentication, false otherwise
	 */
	fun authenticate(clientUUID: UUID, responses: List<CRP>): Boolean {
		val clientCRPs = clients[clientUUID] ?: return false
		return responses.count { clientCRPs.contains(it) } >= AUTHENTICATION_THRESHOLD
	}
}
