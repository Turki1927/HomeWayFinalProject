package com.example.homeway.Repository;

import com.example.homeway.Model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    Review findReviewById(Integer id);

    Review findReviewByRequest_Id(Integer requestId);

    boolean existsByRequest_Id(Integer requestId);

    List<Review> findAllByCustomer_Id(Integer customerId);

    List<Review> findAllByRequest_Company_Id(Integer companyId);
}
