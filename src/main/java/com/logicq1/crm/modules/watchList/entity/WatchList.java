package com.logicq1.crm.modules.watchList.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "watch_list")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WatchList {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String email;

    private String phoneNumber;

}
