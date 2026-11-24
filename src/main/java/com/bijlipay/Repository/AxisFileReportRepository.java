package com.bijlipay.Repository;

import com.bijlipay.Model.AxisFileReport;
import com.bijlipay.Model.SettlementReport;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AxisFileReportRepository extends JpaRepository<AxisFileReport,Long> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO axis_file_report (SELECT * FROM settlement_report WHERE generated_date>=?1 and generated_date<=?2)", nativeQuery = true)
    void insertAxisReport(String yesterDayFrom,String yesterDayTo);


    @Query(value = "select * from axis_file_report where rrn=?1", nativeQuery = true)
    AxisFileReport findByRrn(String rrn);

    @Query(value = "SELECT * FROM axis_file_report WHERE axis_file = 'YES'",nativeQuery = true)
    List<AxisFileReport> findAxisFileComparison(String fromDate, String toDate);

    @Modifying
    @Transactional
    @Query(value = "truncate table axis_file_report", nativeQuery = true)
    void truncate();

    @Modifying
    @Transactional
    @Query(value = "update axis_file_report a inner join uploaded_files f ON a.rrn = f.rrn SET a.axis_file = 'YES' where a.axis_file='NO'",nativeQuery = true)
    void updateData();


}
