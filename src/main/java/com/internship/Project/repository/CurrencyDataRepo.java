package com.internship.Project.repository;

import com.internship.Project.entity.CurrencyData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyDataRepo extends CrudRepository<CurrencyData, Long> {

    CurrencyData findByCurrencyName(String currencyName);

}
