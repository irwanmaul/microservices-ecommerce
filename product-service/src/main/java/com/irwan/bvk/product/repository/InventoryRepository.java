package com.irwan.bvk.product.repository;

import com.irwan.bvk.product.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    List<Inventory> findByProductIdIn(List<Integer> productNumbers);
}
