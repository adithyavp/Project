package com.internship.project.repository;

import com.internship.project.entity.CurrencyData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyDataRepo extends CrudRepository<CurrencyData, Long> {

    CurrencyData findByCurrencyName(String currencyName);

}
