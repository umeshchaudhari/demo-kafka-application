package com.kafka.demokafka.repository;

import com.kafka.demokafka.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    Optional<OrderEntity> findByOrderId(String orderId);

    boolean existsByOrderId(String orderId);

    @Query("""
        SELECT o FROM OrderEntity o
        WHERE (:customerName IS NULL 
               OR LOWER(o.customerName) LIKE LOWER(CONCAT('%', CAST(:customerName AS string), '%')))
          AND (:minAmount IS NULL 
               OR o.amount >= :minAmount)
          AND (:maxAmount IS NULL 
               OR o.amount <= :maxAmount)
        """)
    Page<OrderEntity> findOrders(@Param("customerName") String customerName,
                                 @Param("minAmount") Double minAmount,
                                 @Param("maxAmount") Double maxAmount,
                                 Pageable pageable);

    long count();

    // day wise count
    @Query(value = """
            SELECT DATE(created_at) AS order_date,
                   COUNT(*) AS order_count
            FROM orders
            GROUP BY DATE(created_at)
            ORDER BY order_date DESC
            """,
            nativeQuery = true)
    List<Object[]> findDailyCount();

    // ount dates
    @Query(""" 
            Select COUNT(o)
            from OrderEntity o
            where o.createdAt >= :start
        AND o.createdAt < : end
        """)
    long countAsPerCreated(@Param("start") LocalDateTime start,
                           @Param("end") LocalDateTime end);

}
