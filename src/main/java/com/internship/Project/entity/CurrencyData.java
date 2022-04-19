package com.internship.Project.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CurrencyData {

    // The name of the Currency
    @Id
    private String currencyName;
    // The value of 1 Indian rupee to their currency
    private String ratePerRupee;
    // The value of 1 currency money to Indian rupee
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
