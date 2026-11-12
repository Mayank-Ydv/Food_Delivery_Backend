package com.foodapp.delivery.repository;

import com.foodapp.delivery.entity.AgentStatus;
import com.foodapp.delivery.entity.DeliveryAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryAgentRepository extends JpaRepository<DeliveryAgent, Long> {
    List<DeliveryAgent> findByStatus(AgentStatus status);
}
