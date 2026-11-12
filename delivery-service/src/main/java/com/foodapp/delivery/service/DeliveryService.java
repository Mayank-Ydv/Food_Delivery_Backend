package com.foodapp.delivery.service;

import com.foodapp.delivery.entity.AgentStatus;
import com.foodapp.delivery.entity.DeliveryAgent;
import com.foodapp.delivery.entity.DeliveryAssignment;
import com.foodapp.delivery.exception.ResourceNotFoundException;
import com.foodapp.delivery.repository.DeliveryAgentRepository;
import com.foodapp.delivery.repository.DeliveryAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
@Slf4j
@Service
public class DeliveryService {

    private final DeliveryAgentRepository agentRepository;
    private final DeliveryAssignmentRepository assignmentRepository;

    @Autowired
    public DeliveryService(DeliveryAgentRepository agentRepository, DeliveryAssignmentRepository assignmentRepository) {
        this.agentRepository = agentRepository;
        this.assignmentRepository = assignmentRepository;
    }

    public List<DeliveryAgent> getAvailableAgents() {
        return agentRepository.findByStatus(AgentStatus.AVAILABLE);
    }

    public DeliveryAgent registerAgent(DeliveryAgent agent) {
        return agentRepository.save(agent);
    }

    /**
     * Picks the FIRST available agent and assigns them to the order.
     * (A production system would pick the NEAREST agent using the same
     * haversine logic as restaurant-service — left as a clear extension
     * point so you can demonstrate that pattern again here.)
     */
    public DeliveryAssignment assignAgentToOrder(Long orderId) {
        // Prevent duplicate assignment if already assigned
        Optional<DeliveryAssignment> existingAssignment = assignmentRepository.findByOrderId(orderId);
        if (existingAssignment.isPresent()) {
            log.info("Delivery partner already assigned for orderId: {}. Skipping duplicate assignment.", orderId);
            return existingAssignment.get();
        }
        DeliveryAgent agent = agentRepository.findByStatus(AgentStatus.AVAILABLE).stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No available delivery agents right now"));

        agent.setStatus(AgentStatus.ON_DELIVERY);
        agentRepository.save(agent);

        DeliveryAssignment assignment = new DeliveryAssignment();
        assignment.setOrderId(orderId);
        assignment.setAgent(agent);
    
        assignment.setEstimatedMinutes(25);

        return assignmentRepository.save(assignment);
    }

    public DeliveryAssignment markDelivered(Long orderId) {
        DeliveryAssignment assignment = assignmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No assignment found for order: " + orderId));

        assignment.setDeliveredAt(java.time.LocalDateTime.now());

        DeliveryAgent agent = assignment.getAgent();
        agent.setStatus(AgentStatus.AVAILABLE); // agent becomes available again
        agentRepository.save(agent);

        return assignmentRepository.save(assignment);
    }

    public DeliveryAssignment getAssignmentForOrder(Long orderId) {
        return assignmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No assignment found for order: " + orderId));
    }
}
