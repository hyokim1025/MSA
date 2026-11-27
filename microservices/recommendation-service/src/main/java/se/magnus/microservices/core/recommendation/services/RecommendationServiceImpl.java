package se.magnus.microservices.core.recommendation.services;

import java.util.List;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.recommendation.Recommendation;
import se.magnus.api.core.recommendation.RecommendationService;
import se.magnus.util.http.ServiceUtil;

@RestController
public class RecommendationServiceImpl implements RecommendationService {
    private final ServiceUtil serviceUtil;

    public RecommendationServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Recommendation> getRecommendations(int productId) {
        if (productId == 113) {
            return List.of();
        }

        String address = serviceUtil.getServiceAddress();
        return List.of(
                new Recommendation(productId, 1, "Author 1", 1, "Content 1", address),
                new Recommendation(productId, 2, "Author 2", 2, "Content 2", address),
                new Recommendation(productId, 3, "Author 3", 3, "Content 3", address)
        );
    }
}