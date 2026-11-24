package com.bijlipay.Repository;

import com.bijlipay.Model.SecondBackUpReport;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SecondBackUpReportRepository extends JpaRepository<SecondBackUpReport,Long> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO second_back_up_report (SELECT * FROM back_up_settelment_report WHERE generated_date>=?1 and generated_date<=?2)", nativeQuery = true)
    void insertReport(String yesterDayFrom,String yesterDayTo);

}
