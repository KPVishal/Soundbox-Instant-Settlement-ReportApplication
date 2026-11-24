package com.bijlipay.Repository;

import com.bijlipay.Model.FileEntityData;
import com.bijlipay.Model.SettlementReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FileEntityDataRepository extends JpaRepository<FileEntityData, Long> {
    @Modifying
    @Transactional
    @Query(value="truncate table uploaded_files_data",nativeQuery = true)
    void truncate();

}

