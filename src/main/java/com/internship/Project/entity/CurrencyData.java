package com.internship.Project.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CurrencyData {

    @Id
    private String currencyName;
    private String ratePerRupee;
    private String rupeePerCurrency;

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getRatePerRupee() {
        return ratePerRupee;
    }

    public void setRatePerRupee(String ratePerRupee) {
        this.ratePerRupee = ratePerRupee;
    }

    public String getRupeePerCurrency() {
        return rupeePerCurrency;
    }

    public void setRupeePerCurrency(String rupeePerCurrency) {
        this.rupeePerCurrency = rupeePerCurrency;
    }

}
