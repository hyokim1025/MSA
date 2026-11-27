package se.magnus.microservices.core.recommendation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class RecommendationServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Test
	void returnsThreeRecommendationsForProduct() {
		client.get()
				.uri("/recommendation?productId=1")
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.length()").isEqualTo(3)
				.jsonPath("$[0].productId").isEqualTo(1)
				.jsonPath("$[0].recommendationId").isEqualTo(1);
	}

	@Test
	void returnsEmptyListForProductWithoutRecommendations() {
		client.get()
				.uri("/recommendation?productId=113")
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$").isArray()
				.jsonPath("$.length()").isEqualTo(0);
	}

	@Test
	void returnsBadRequestWhenProductIdIsMissing() {
		client.get()
				.uri("/recommendation")
				.exchange()
				.expectStatus().isBadRequest();
	}
}