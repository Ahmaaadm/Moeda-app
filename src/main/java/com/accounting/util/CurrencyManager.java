package com.accounting.util;

import com.accounting.database.DatabaseManager;

import java.text.DecimalFormat;

public class CurrencyManager {
    private static CurrencyManager instance;
    private String currentCurrency;
    private double exchangeRate;
    private DatabaseManager dbManager;
    private DecimalFormat formatter;
    
    private CurrencyManager() {
        dbManager = DatabaseManager.getInstance();
        formatter = new DecimalFormat("#,##0.00");
    }
    
    public static CurrencyManager getInstance() {
        if (instance == null) {
            instance = new CurrencyManager();
        }
        return instance;
    }
    
    public void loadSettings() {
        currentCurrency = dbManager.getSetting("currency");
        if (currentCurrency == null) {
            currentCurrency = "USD";
        }
        
        String rateStr = dbManager.getSetting("exchange_rate");
        if (rateStr != null) {
            try {
                exchangeRate = Double.parseDouble(rateStr);
            } catch (NumberFormatException e) {
                exchangeRate = 89500.0; // Default LBP rate
            }
        } else {
            exchangeRate = 89500.0;
        }
    }
    
    public void updateSettings(String currency, double rate) {
        this.currentCurrency = currency;
        this.exchangeRate = rate;
        
        dbManager.updateSetting("currency", currency);
        dbManager.updateSetting("exchange_rate", String.valueOf(rate));
    }
    
    public String getCurrentCurrency() {
        return currentCurrency;
    }
    
    public double getExchangeRate() {
        return exchangeRate;
    }
    
    public String formatAmount(double amount) {
        if ("USD".equals(currentCurrency)) {
            return "$" + formatter.format(amount);
        } else {
            return formatter.format(amount * exchangeRate) + " ل.ل";
        }
    }
    
    public String getCurrencySymbol() {
        return "USD".equals(currentCurrency) ? "$" : "ل.ل";
    }
}