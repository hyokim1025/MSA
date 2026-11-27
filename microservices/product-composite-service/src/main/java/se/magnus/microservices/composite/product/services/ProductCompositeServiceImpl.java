package se.magnus.microservices.composite.product.services;

import java.util.List;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.composite.product.ProductAggregate;
import se.magnus.api.composite.product.ProductCompositeService;
import se.magnus.api.composite.product.RecommendationSummary;
import se.magnus.api.composite.product.ReviewSummary;
import se.magnus.api.composite.product.ServiceAddresses;
import se.magnus.api.core.product.Product;
import se.magnus.api.core.recommendation.Recommendation;
import se.magnus.api.core.review.Review;
import se.magnus.util.http.ServiceUtil;

@RestController
public class ProductCompositeServiceImpl implements ProductCompositeService {
    private final ProductCompositeIntegration integration;
    private final ServiceUtil serviceUtil;

    public ProductCompositeServiceImpl(
            ProductCompositeIntegration integration, ServiceUtil serviceUtil) {
        this.integration = integration;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public ProductAggregate getProduct(int productId) {
        Product product = integration.getProduct(productId);
        List<Recommendation> recommendations = integration.getRecommendations(productId);
        List<Review> reviews = integration.getReviews(productId);

        List<RecommendationSummary> recommendationSummaries = recommendations.stream()
                .map(item -> new RecommendationSummary(
                        item.getRecommendationId(), item.getAuthor(), item.getRate()))
                .toList();

        List<ReviewSummary> reviewSummaries = reviews.stream()
                .map(item -> new ReviewSummary(
                        item.getReviewId(), item.getAuthor(), item.getSubject()))
                .toList();

        ServiceAddresses addresses = new ServiceAddresses(
                serviceUtil.getServiceAddress(),
                product.getServiceAddress(),
                reviews.isEmpty() ? "" : reviews.getFirst().getServiceAddress(),
                recommendations.isEmpty() ? "" : recommendations.getFirst().getServiceAddress());

        return new ProductAggregate(
                product.getProductId(), product.getName(), product.getWeight(),
                recommendationSummaries, reviewSummaries, addresses);
    }
}