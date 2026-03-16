package com.urbanease.repository;

import com.urbanease.model.Service;
import com.urbanease.model.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByCategory(ServiceCategory category);
    
    List<Service> findByCategoryId(Long categoryId);
    
    List<Service> findByCategoryIdAndIsActive(Long categoryId, boolean isActive);
    
    List<Service> findByIsActive(boolean isActive);
    
    List<Service> findByCategoryAndIsActive(ServiceCategory category, boolean isActive);
}
