package com.bijlipay.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bijlipay.Model.UploadedVpa;

@Repository
public interface UploadedVpaRepository extends JpaRepository<UploadedVpa, Long> {

    @Query(value = "SELECT vpa FROM uploaded_vpa", nativeQuery = true)
    List<String> findAllVpas();

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE uploaded_vpa", nativeQuery = true)
    void truncate();
}
