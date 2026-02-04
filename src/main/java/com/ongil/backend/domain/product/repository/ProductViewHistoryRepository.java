package com.ongil.backend.domain.product.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ongil.backend.domain.product.entity.ProductViewHistory;

public interface ProductViewHistoryRepository extends JpaRepository<ProductViewHistory, Long> {

	@Query("SELECT DISTINCT pvh.product.id FROM ProductViewHistory pvh " +
		"WHERE pvh.user.id = :userId AND pvh.createdAt > :since")
	List<Long> findDistinctProductIdsByUserIdAndCreatedAtAfter(
		@Param("userId") Long userId,
		@Param("since") LocalDateTime since
	);
}
