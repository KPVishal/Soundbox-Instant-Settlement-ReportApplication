package com.bijlipay.Repository;

import com.bijlipay.Model.NodalAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodalAccountRepository extends JpaRepository<NodalAccount,Long> {

    @Query(value = "SELECT * FROM nodal_account WHERE created_at >= ?1 AND created_at <= ?2 AND debit_credit_identifier='C'", nativeQuery = true)
    List<NodalAccount> fetchNodalData(String startDate, String endDate);


}
