package com.logicq1.crm.MetadataEngine.ModuleManagement.repository;

import com.logicq1.crm.MetadataEngine.ModuleManagement.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ModuleRepository extends JpaRepository<Module, UUID> {

}