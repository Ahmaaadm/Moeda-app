package com.accounting.util;

import com.accounting.database.DatabaseManager;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CurrencyManager {
    private static CurrencyManager instance;

    private final DatabaseManager dbManager;
    private final DecimalFormat formatter;

    // persisted in settings
    private String currentCurrency;   // "Kz", "USD", "LBP"
    private double exchangeRate;      // USD -> currentCurrency (e.g., 1 USD = 1.0 Kz if you want same)

    private CurrencyManager() {
        dbManager = DatabaseManager.getInstance();

        // Create formatter with English locale to ensure English numerals
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        formatter = new DecimalFormat("#,##0.00", symbols);

        loadSettings();
    }

    public static synchronized CurrencyManager getInstance() {
        if (instance == null) instance = new CurrencyManager();
        return instance;
    }

    // -------- settings --------
    public void loadSettings() {
        String currency = dbManager.getSetting("currency");
        currentCurrency = (currency == null ? "Kz" : currency);  // default Kz

        String rateStr = dbManager.getSetting("exchange_rate");
        if (rateStr == null) {
            exchangeRate = 1.0; // default: 1 USD = 1 Kz
        } else {
            try {
                exchangeRate = Double.parseDouble(rateStr);
                if (exchangeRate <= 0) exchangeRate = 1.0;
            } catch (NumberFormatException e) {
                exchangeRate = 1.0;
            }
        }
    }

    public void updateSettings(String currency, double rate) {
        this.currentCurrency = currency;
        this.exchangeRate = rate > 0 ? rate : this.exchangeRate;

        dbManager.updateSetting("currency", this.currentCurrency);
        dbManager.updateSetting("exchange_rate", String.valueOf(this.exchangeRate));
    }

    public String getCurrentCurrency() { return currentCurrency; }
    public double getExchangeRate() { return exchangeRate; }

    // -------- conversion (DB amounts are in USD) --------
    /** USD -> active currency (for display) */
    public double toActiveCurrency(double amountInUSD) {
        if ("USD".equals(currentCurrency)) return amountInUSD;
        return amountInUSD * exchangeRate;  // LBP or Kz or any other
    }

    /** active currency -> USD (for saving user input) */
    public double fromActiveToUSD(double amountInActiveCurrency) {
        if ("USD".equals(currentCurrency)) return amountInActiveCurrency;
        return amountInActiveCurrency / (exchangeRate == 0 ? 1.0 : exchangeRate);
    }

    // -------- formatting --------
    public String getCurrencySymbol() {
        if ("Kz".equals(currentCurrency)) return "Kz";
        if ("USD".equals(currentCurrency)) return "$";
        if ("LBP".equals(currentCurrency)) return "ل.ل";
        return currentCurrency; // fallback: show whatever is stored
    }

    // format a number + symbol ONLY (NO conversion)
    public String formatAmountSymbolOnly(double amount) {
        return formatter.format(amount) + " " + getCurrencySymbol();
    }

    /** Format a USD amount per active currency (does conversion + symbol) */
    public String formatAmount(double amountInUSD) {
        double display = toActiveCurrency(amountInUSD);
        String sym = getCurrencySymbol();

        // Format the number with proper English numerals and clean decimals
        String formattedNumber = formatter.format(display);

        // Common style: symbol after for Kz and LBP, before for USD
        if ("USD".equals(currentCurrency)) {
            return sym + formattedNumber;      // $1,234.00
        } else {
            return formattedNumber + " " + sym; // 1,234.00 Kz  /  1,234.00 ل.ل
        }
    }

    /**
     * Format amount without decimals if it's a whole number
     * This makes the display cleaner for amounts like 1000.00 -> 1,000 Kz
     */
    public String formatAmountClean(double amountInUSD) {
        double display = toActiveCurrency(amountInUSD);
        String sym = getCurrencySymbol();

        // Check if the number is a whole number
        String formattedNumber;
        if (display == Math.floor(display)) {
            // It's a whole number, format without decimals
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
            DecimalFormat wholeFormatter = new DecimalFormat("#,##0", symbols);
            formattedNumber = wholeFormatter.format(display);
        } else {
            // Has decimals, use regular formatter
            formattedNumber = formatter.format(display);
        }

        // Common style: symbol after for Kz and LBP, before for USD
        if ("USD".equals(currentCurrency)) {
            return sym + formattedNumber;      // $1,234 or $1,234.50
        } else {
            return formattedNumber + " " + sym; // 1,234 Kz or 1,234.50 Kz
        }
    }
}