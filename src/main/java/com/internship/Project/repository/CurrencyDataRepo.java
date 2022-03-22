package com.internship.Project.repository;

import com.internship.Project.entity.CurrencyData;
import org.springframework.data.repository.CrudRepository;

public interface CurrencyDataRepo extends CrudRepository<CurrencyData, Long> {

    CurrencyData findByCurrencyName(String currencyName);

}
