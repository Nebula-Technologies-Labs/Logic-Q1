package com.logicq1.crm.modules.watchList.repository;

import com.logicq1.crm.modules.watchList.entity.WatchList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WatchListRepository extends JpaRepository<WatchList, UUID> {


}
