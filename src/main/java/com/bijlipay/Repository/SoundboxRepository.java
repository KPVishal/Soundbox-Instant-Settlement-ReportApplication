package com.bijlipay.Repository;

import com.bijlipay.Model.AxisSettelment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SoundboxRepository  extends JpaRepository<AxisSettelment,Long> {

}
