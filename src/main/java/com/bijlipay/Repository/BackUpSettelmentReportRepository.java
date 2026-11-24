package com.bijlipay.Repository;

import com.bijlipay.Model.BackUpSettelmentReport;
import com.bijlipay.Model.NodalAccount;
import com.bijlipay.Model.SettlementReport;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BackUpSettelmentReportRepository extends JpaRepository<BackUpSettelmentReport,Long> {
    @Modifying
    @Transactional
    @Query(value = "INSERT IGNORE INTO back_up_settelment_report (SELECT * FROM settlement_report WHERE generated_date>=?1 and generated_date<=?2)", nativeQuery = true)
    void insertReport(String yesterDayFrom,String yesterDayTo);

    BackUpSettelmentReport findByRrn(String rrn);

//    @Query("SELECT s FROM BackUpSettelmentReport s WHERE s.axisFile = 'YES'" )
//    @Query(value = "SELECT * FROM backup_settelment_report WHERE axis_file = 'YES' AND generated_date >= ?1 AND generated_date <= ?2",nativeQuery = true)
//    List<BackUpSettelmentReport> findAxisFileComparison( String fromDate, String toDate);
}
