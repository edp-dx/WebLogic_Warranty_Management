package com.example.repository;

import com.example.model.CostCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CostCenterRepository extends JpaRepository<CostCenter, Long> {

    @Query("SELECT DISTINCT new com.example.model.CostCenter(PCAL.segment3 as costCenterCode, FV.description as costCenterName, COMPANY) FROM ... [the rest of the SQL query from CostCenterLOV.xml] ... WHERE ... AND (:Unit IS NULL OR COMPANY = :Unit)")
    List<CostCenter> findByCompany(@Param("Unit") String unit);
}