package com.logicq1.crm.modules.auth.repositories;

import com.logicq1.crm.modules.auth.entity.ForgotPassword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Integer> {


}
