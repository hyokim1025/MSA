package se.magnus.microservices.composite.product.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import se.magnus.api.core.product.Product;
import se.magnus.api.core.recommendation.Recommendation;
import se.magnus.api.core.review.Review;
import se.magnus.api.exceptions.InvalidInputException;
import se.magnus.api.exceptions.NotFoundException;
import se.magnus.util.http.HttpErrorInfo;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Component
public class ProductCompositeIntegration {
    private final RestTemplate client;
    private final JsonMapper errorMapper = JsonMapper.builder().build();
    private final String productUrl;
    private final String recommendationUrl;
    private final String reviewUrl;

    public ProductCompositeIntegration(
            RestTemplate client,
            @Value("${app.product-service.url}") String productUrl,
            @Value("${app.recommendation-service.url}") String recommendationUrl,
            @Value("${app.review-service.url}") String reviewUrl) {
        this.client = client;
        this.productUrl = productUrl;
        this.recommendationUrl = recommendationUrl;
        this.reviewUrl = reviewUrl;
    }

    public Product getProduct(int productId) {
        try {
            return client.getForObject(
                    productUrl + "/product/{id}", Product.class, productId);
        } catch (HttpClientErrorException exception) {
            switch (exception.getStatusCode().value()) {
                case 404 -> throw new NotFoundException(readErrorMessage(exception));
                case 422 -> throw new InvalidInputException(readErrorMessage(exception));
                default -> throw exception;
            }
        }
    }

    public List<Recommendation> getRecommendations(int productId) {
        return client.exchange(
                recommendationUrl + "/recommendation?productId={id}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Recommendation>>() {},
                productId
        ).getBody();
    }

    public List<Review> getReviews(int productId) {
        return client.exchange(
                reviewUrl + "/review?productId={id}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Review>>() {},
                productId
        ).getBody();
    }

    private String readErrorMessage(HttpClientErrorException exception) {
        try {
            HttpErrorInfo error = errorMapper.readValue(
                    exception.getResponseBodyAsString(), HttpErrorInfo.class);
            if (error != null && error.message() != null) {
                return error.message();
            }
        } catch (JacksonException parseException) {
            return exception.getMessage();
        }

        return exception.getMessage();
    }
}