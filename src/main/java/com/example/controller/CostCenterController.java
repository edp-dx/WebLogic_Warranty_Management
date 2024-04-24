package com.example.controller;

import com.example.model.CostCenter;
import com.example.service.CostCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/costcenters")
public class CostCenterController {

    private final CostCenterService costCenterService;

    @Autowired
    public CostCenterController(CostCenterService costCenterService) {
        this.costCenterService = costCenterService;
    }

    @GetMapping("/{unit}")
    public List<CostCenter> getCostCentersByCompany(@PathVariable String unit) {
        return costCenterService.getCostCenters(unit);
    }
}