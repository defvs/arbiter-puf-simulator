package dev.defvs.pufsim.enrollment

import dev.defvs.pufsim.CRP
import dev.defvs.pufsim.PufChallenge
import dev.defvs.pufsim.challenges.ChallengeGenerator
import mu.KotlinLogging
import java.util.*

private val logger = KotlinLogging.logger {}

class Server {
	companion object {
		/** Number of challenges to generate during enrollment */
		private const val ENROLLMENT_CHALLENGE_COUNT = 100_000
		
		/** Number of challenges to generate during authentication */
		private const val AUTHENTICATION_CHALLENGE_COUNT = 1_000
		
		/** Percentage of challenges required to pass for succesful authentication */
		private const val AUTHENTICATION_THRESHOLD = 0.90
	}
	
	/** Map of client UUIDs to their enrolled CRP responses */
	private val clients = hashMapOf<UUID, List<CRP>>()
	
	/**
	 * Generates a set of enrollment challenges for the client to respond to.
	 * @return list of challenges
	 */
	fun getEnrollmentChallenges(clientUUID: UUID) =
		ChallengeGenerator.generateChallenge(ENROLLMENT_CHALLENGE_COUNT, Random())
			.also { logger.debug { "Client $clientUUID requested new enrollment challenges" } }
	
	/**
	 * Enrolls a client by storing their CRP responses.
	 * @param clientUUID unique identifier of the client
	 * @param crps list of CRP responses
	 */
	fun enroll(clientUUID: UUID, crps: List<CRP>) = crps.let {
		if (crps.size != ENROLLMENT_CHALLENGE_COUNT) {
			logger.warn { "Client $clientUUID enrolled with ${crps.size} CRPs instead of $ENROLLMENT_CHALLENGE_COUNT" }
			false
		} else {
			logger.debug { "Client $clientUUID enrolled." }
			clients[clientUUID] = crps
			true
		}
	}
	
	private val currentAuthentications = hashMapOf<UUID, List<PufChallenge>>()
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
			?.also { logger.debug { "Client $clientUUID requested new authentication challenges" } }
			?.also { currentAuthentications[clientUUID] = it }
			?: throw IllegalArgumentException("Client is not enrolled")
	
	/**
	 * Authenticates a client based on their responses to the authentication challenges.
	 * @param clientUUID unique identifier of the client
	 * @param responses list of responses
	 * @return true if the client passes authentication, false otherwise
	 */
	fun authenticate(clientUUID: UUID, responses: List<CRP>): Boolean {
		if (responses.size != AUTHENTICATION_CHALLENGE_COUNT) {
			logger.warn { "Client $clientUUID sent ${responses.size} responses, expected $AUTHENTICATION_CHALLENGE_COUNT" }
			return false
		}
		if (currentAuthentications[clientUUID]?.equals(responses.map { it.first }) != true) {
			logger.warn { "Client $clientUUID sent different challenges than the ones requested earlier" }
			return false
		}
		currentAuthentications.remove(clientUUID)
		
		val clientCRPs = clients[clientUUID] ?: return false
		
		val successRate =
			responses.count { clientCRPs.contains(it) }.toDouble() / AUTHENTICATION_CHALLENGE_COUNT.toDouble()
		logger.debug { "Authentication of $clientUUID success rate: $successRate" }
		
		return successRate >= AUTHENTICATION_THRESHOLD
	}
}
