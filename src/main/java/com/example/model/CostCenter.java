package com.example.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class CostCenter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String costCenterCode;
    private String costCenterName;
    private String company;

    public String getCostCenterCode() {
        return costCenterCode;
    }

    public void setCostCenterCode(String costCenterCode) {
        this.costCenterCode = costCenterCode;
    }

    public String getCostCenterName() {
        return costCenterName;
    }

    public void setCostCenterName(String costCenterName) {
        this.costCenterName = costCenterName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    //Default constructor for JPA
    public CostCenter() {}

    //Constructor for convenience
    public CostCenter(String costCenterCode, String costCenterName, String company) {
        this.costCenterCode = costCenterCode;
        this.costCenterName = costCenterName;
        this.company = company;
    }
}