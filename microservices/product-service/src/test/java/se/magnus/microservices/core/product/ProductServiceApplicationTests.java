package se.magnus.microservices.core.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ProductServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Test
	void returnsProductForValidId() {
		client.get()
				.uri("/product/1")
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.productId").isEqualTo(1)
				.jsonPath("$.name").isEqualTo("name-1")
				.jsonPath("$.weight").isEqualTo(123);
	}

	@Test
	void returnsNotFoundForMissingProduct() {
		client.get()
				.uri("/product/13")
				.exchange()
				.expectStatus().isNotFound()
				.expectBody()
				.jsonPath("$.message")
				.isEqualTo("No product found for productId: 13");
	}

	@Test
	void returnsUnprocessableContentForNegativeId() {
		client.get()
				.uri("/product/-1")
				.exchange()
				.expectStatus().isEqualTo(422)
				.expectBody()
				.jsonPath("$.message")
				.isEqualTo("Invalid productId: -1");
	}

	@Test
	void returnsBadRequestForNonNumericId() {
		client.get()
				.uri("/product/no-integer")
				.exchange()
				.expectStatus().isBadRequest();
	}
}