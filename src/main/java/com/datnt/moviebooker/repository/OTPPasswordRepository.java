package com.datnt.moviebooker.repository;

import com.datnt.moviebooker.entity.OTPPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OTPPasswordRepository extends JpaRepository<OTPPassword, Long> {
    @Query("SELECT o FROM OTPPassword o WHERE o.idRequestResetPassword = :requestId")
    OTPPassword findByRequestId(@Param("requestId") String requestId);
}
