package com.bijlipay.Repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bijlipay.Model.SettlementReport;

import jakarta.transaction.Transactional;

@Repository
public interface SettlementReportRepository extends JpaRepository<SettlementReport,Long> {
    @Modifying
    @Transactional
    @Query(value = "truncate table settlement_report",nativeQuery = true)
    void truncate();


    @Query(value = "SELECT * FROM settlement_report WHERE nodel_account in ('NO') and axis_callback in ('YES') and axis_file in ('NO') and (generated_date>=?1 and generated_date<=?2)", nativeQuery = true)
    List<SettlementReport> fetchNodalReport(String fromDate,String todate);

    @Query(value = "SELECT * FROM settlement_report WHERE nodel_account in ('YES') and axis_callback in ('NO') and axis_file in ('NO') and (generated_date>=?1 and generated_date<=?2)", nativeQuery = true)
    List<SettlementReport> fetchAxisCallbackReport(String fromDate,String todate);

    @Query(value = "SELECT * FROM settlement_report WHERE nodel_account in ('YES') and (generated_date>=?1 and generated_date<=?2)", nativeQuery = true)
    List<SettlementReport> fetchAxisFileReport(String fromDateTwo,String toDateTwo);

    // VPA-filtered queries for slot reports (LOT 1, 2, 3)
    @Query(value = "SELECT * FROM settlement_report WHERE nodel_account in ('NO') and axis_callback in ('YES') and axis_file in ('NO') and (generated_date>=?1 and generated_date<=?2) and credit_vpa IN (SELECT vpa FROM uploaded_vpa)", nativeQuery = true)
    List<SettlementReport> fetchNodalReportByVpa(String fromDate, String todate);

    @Query(value = "SELECT * FROM settlement_report WHERE nodel_account in ('YES') and axis_callback in ('NO') and axis_file in ('NO') and (generated_date>=?1 and generated_date<=?2) and credit_vpa IN (SELECT vpa FROM uploaded_vpa)", nativeQuery = true)
    List<SettlementReport> fetchAxisCallbackReportByVpa(String fromDate, String todate);

    @Query(value = "SELECT * FROM settlement_report WHERE nodel_account in ('YES') and (generated_date>=?1 and generated_date<=?2) and credit_vpa IN (SELECT vpa FROM uploaded_vpa)", nativeQuery = true)
    List<SettlementReport> fetchAxisFileReportByVpa(String fromDateTwo, String toDateTwo);

    // 24-hour report queries — exclude transactions already flagged in slot reports
    @Query(value = "SELECT * FROM settlement_report WHERE nodel_account in ('NO') and axis_callback in ('YES') and axis_file in ('NO') and (generated_date>=?1 and generated_date<=?2) and (report_flag IS NULL OR report_flag != 1)", nativeQuery = true)
    List<SettlementReport> fetchNodalReportUnflagged(String fromDate, String todate);

    @Query(value = "SELECT * FROM settlement_report WHERE nodel_account in ('YES') and axis_callback in ('NO') and axis_file in ('NO') and (generated_date>=?1 and generated_date<=?2) and (report_flag IS NULL OR report_flag != 1)", nativeQuery = true)
    List<SettlementReport> fetchAxisCallbackReportUnflagged(String fromDate, String todate);

    @Query(value = "SELECT * FROM settlement_report WHERE nodel_account in ('YES') and (generated_date>=?1 and generated_date<=?2) and (report_flag IS NULL OR report_flag != 1)", nativeQuery = true)
    List<SettlementReport> fetchAxisFileReportUnflagged(String fromDateTwo, String toDateTwo);

    // Update report_flag for given IDs
    @Modifying
    @Transactional
    @Query(value = "UPDATE settlement_report SET report_flag = 1 WHERE id IN (?1)", nativeQuery = true)
    void updateReportFlag(List<Long> ids);

    SettlementReport findByRrn(String rrn);

    @Query("SELECT s FROM SettlementReport s WHERE s.axisFile = 'YES'")
    List<SettlementReport> findAxisFileComparison();


    @Query("SELECT s.rrn FROM SettlementReport s WHERE rrn is not null and creditVpa is null")
    List<String> emptyEntry();

    @Query(value = "SELECT * FROM settlement_report WHERE rrn=?1", nativeQuery = true)
    SettlementReport fetchRrn(String rrn);


    @Query(value = "SELECT id FROM settlement_report WHERE generated_date>=?1 and generated_date<=?2", nativeQuery = true)
    List<Long> dayBoforeYesterDay(String yesterDayFrom,String yesterDayTo);

    @Query(value = "DELETE FROM settlement_report WHERE id in (?1)", nativeQuery = true)
    void delete(List<Long> id);


    @Query(value = "select * from settlement_report where credit_vpa=?1", nativeQuery = true)
    List<SettlementReport> fetchByVpa(String vpa);


    @Query(value = "select * from settlement_report where rrn=?1", nativeQuery = true)
    SettlementReport findByRrnAndAmount(String rrnNo);

    @Query(value = "SELECT * FROM settlement_report WHERE rrn IN (?1)", nativeQuery = true)
    List<SettlementReport> findByRrnIn(Collection<String> rrns);

    @Query(value = "SELECT * FROM settlement_report WHERE axis_file = 'YES' AND generated_date >= ?1 AND generated_date <= ?2",nativeQuery = true)
    List<SettlementReport> findAxisFileComparison( String fromDate, String toDate);


    @Modifying
    @Transactional
    @Query(value = "INSERT INTO back_up_settelment_report SELECT * FROM settlement_report WHERE generated_date >= ?1 AND generated_date <= ?2", nativeQuery = true)
    void insertReport(String yesterDayFrom, String yesterDayTo);
}
