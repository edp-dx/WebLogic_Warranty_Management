package com.example.service;

import com.example.model.CostCenter;
import com.example.repository.CostCenterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CostCenterService {

    private final CostCenterRepository costCenterRepository;

    @Autowired
    public CostCenterService(CostCenterRepository costCenterRepository) {
        this.costCenterRepository = costCenterRepository;
    }

    public List<CostCenter> getCostCenters(String unit) {
        return costCenterRepository.findByCompany(unit);
    }
}