package com.bijlipay.Dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE)
public class TransactionRecordDto {
    private int serialNumber;
    private String transactionDate;
    private String pstdDate;
    private String transactionParticulars;
    private String chqNumber;
    private String valueDate;

    private BigDecimal amount;
    private String drcr;
    private BigDecimal balance;

    private String paymentMode;
    private String utrNumber;
    private String internalReferenceNumber;

    private String remittingBranch;
    private String remittingBankName;
    private String remittingAccountNumber;
    private String remittingAccountName;
    private String remittingIFSC;

    private String benficiaryBranch;
    private String benficiaryName;
    private String benficiaryAccountNumber;
    private String benficiaryIFSC;

    private String channel;
    private String timeStamp;
    private String remarks;

    private String transactionCurrencyCode;
    private String internalReferenceId;
    private String transactionParticular2;
    private String customerReferenceNumber;
    private String virtualAccountNumber;
    private String corporateCode;

    private String additionalField1;
    private String additionalField2;
    private String additionalField3;

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getPstdDate() {
        return pstdDate;
    }

    public void setPstdDate(String pstdDate) {
        this.pstdDate = pstdDate;
    }

    public String getTransactionParticulars() {
        return transactionParticulars;
    }

    public void setTransactionParticulars(String transactionParticulars) {
        this.transactionParticulars = transactionParticulars;
    }

    public String getChqNumber() {
        return chqNumber;
    }

    public void setChqNumber(String chqNumber) {
        this.chqNumber = chqNumber;
    }

    public String getValueDate() {
        return valueDate;
    }

    public void setValueDate(String valueDate) {
        this.valueDate = valueDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDrcr() {
        return drcr;
    }

    public void setDrcr(String drcr) {
        this.drcr = drcr;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getUtrNumber() {
        return utrNumber;
    }

    public void setUtrNumber(String utrNumber) {
        this.utrNumber = utrNumber;
    }

    public String getInternalReferenceNumber() {
        return internalReferenceNumber;
    }

    public void setInternalReferenceNumber(String internalReferenceNumber) {
        this.internalReferenceNumber = internalReferenceNumber;
    }

    public String getRemittingBranch() {
        return remittingBranch;
    }

    public void setRemittingBranch(String remittingBranch) {
        this.remittingBranch = remittingBranch;
    }

    public String getRemittingBankName() {
        return remittingBankName;
    }

    public void setRemittingBankName(String remittingBankName) {
        this.remittingBankName = remittingBankName;
    }

    public String getRemittingAccountNumber() {
        return remittingAccountNumber;
    }

    public void setRemittingAccountNumber(String remittingAccountNumber) {
        this.remittingAccountNumber = remittingAccountNumber;
    }

    public String getRemittingAccountName() {
        return remittingAccountName;
    }

    public void setRemittingAccountName(String remittingAccountName) {
        this.remittingAccountName = remittingAccountName;
    }

    public String getRemittingIFSC() {
        return remittingIFSC;
    }

    public void setRemittingIFSC(String remittingIFSC) {
        this.remittingIFSC = remittingIFSC;
    }

    public String getBenficiaryBranch() {
        return benficiaryBranch;
    }

    public void setBenficiaryBranch(String benficiaryBranch) {
        this.benficiaryBranch = benficiaryBranch;
    }

    public String getBenficiaryName() {
        return benficiaryName;
    }

    public void setBenficiaryName(String benficiaryName) {
        this.benficiaryName = benficiaryName;
    }

    public String getBenficiaryAccountNumber() {
        return benficiaryAccountNumber;
    }

    public void setBenficiaryAccountNumber(String benficiaryAccountNumber) {
        this.benficiaryAccountNumber = benficiaryAccountNumber;
    }

    public String getBenficiaryIFSC() {
        return benficiaryIFSC;
    }

    public void setBenficiaryIFSC(String benficiaryIFSC) {
        this.benficiaryIFSC = benficiaryIFSC;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getTransactionCurrencyCode() {
        return transactionCurrencyCode;
    }

    public void setTransactionCurrencyCode(String transactionCurrencyCode) {
        this.transactionCurrencyCode = transactionCurrencyCode;
    }

    public String getInternalReferenceId() {
        return internalReferenceId;
    }

    public void setInternalReferenceId(String internalReferenceId) {
        this.internalReferenceId = internalReferenceId;
    }

    public String getTransactionParticular2() {
        return transactionParticular2;
    }

    public void setTransactionParticular2(String transactionParticular2) {
        this.transactionParticular2 = transactionParticular2;
    }

    public String getCustomerReferenceNumber() {
        return customerReferenceNumber;
    }

    public void setCustomerReferenceNumber(String customerReferenceNumber) {
        this.customerReferenceNumber = customerReferenceNumber;
    }

    public String getVirtualAccountNumber() {
        return virtualAccountNumber;
    }

    public void setVirtualAccountNumber(String virtualAccountNumber) {
        this.virtualAccountNumber = virtualAccountNumber;
    }

    public String getCorporateCode() {
        return corporateCode;
    }

    public void setCorporateCode(String corporateCode) {
        this.corporateCode = corporateCode;
    }

    public String getAdditionalField1() {
        return additionalField1;
    }

    public void setAdditionalField1(String additionalField1) {
        this.additionalField1 = additionalField1;
    }

    public String getAdditionalField2() {
        return additionalField2;
    }

    public void setAdditionalField2(String additionalField2) {
        this.additionalField2 = additionalField2;
    }

    public String getAdditionalField3() {
        return additionalField3;
    }

    public void setAdditionalField3(String additionalField3) {
        this.additionalField3 = additionalField3;
    }
}
