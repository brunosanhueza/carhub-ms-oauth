package com.zacky.automotriz.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zacky.automotriz.models.entity.automotrizModel;

public interface automotrizRepository extends JpaRepository<automotrizModel, Integer>{
  

}
