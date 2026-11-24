package com.bijlipay.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "settlement_report")
public class SettlementReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "txn_id",nullable = true)
    private String txnId;

    @Column(name = "amount",nullable = true)
    private String amount;

    @Column(name = "vpa",nullable = true)
    private String vpa;

    @Column(name = "merchant_name",nullable = true)
    private String merchantName;

    @Column(name = "generated_date",nullable = true)
    private String generatedDate;

    @Column(name = "axis_callback",columnDefinition = "VARCHAR(10) DEFAULT 'NO'")
    private String axisCallBack="NO";

    @Column(name = "nodel_account",columnDefinition = "VARCHAR(10) DEFAULT 'NO'")
    private String nodelAccount="NO";

    @Column(name = "axis_file",columnDefinition = "VARCHAR(10) DEFAULT 'NO'")
    private String axisFile="NO";

    @Column(name = "account_no",nullable = true)
    private String acountNumber;

    @Column(name = "rrn",unique = true,nullable = false)
    private String rrn;

    @Column(name = "order_id",nullable = true)
    private String orderId;

    @Column(name = "mobile_no",nullable = true)
    private String mobileNo;

    @Column(name = "bank_name",nullable = true)
    private String bankName;

    @Column(name = "masked_account_number")
    private String maskedAccountNumber;

    @Column(name = "ifsc")
    private String ifsc;

    @Column(name = "account_cust_name")
    private String accountCustName;

    @Column(name = "resp_code")
    private String respCode;

    @Column(name = "credit_vpa")
    private String creditVpa;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "surcharge", precision = 15, scale = 2)
    private Integer surcharge;

    @Column(name = "tax", precision = 15, scale = 2)
    private Integer tax;

    @Column(name = "debit_amount", precision = 15, scale = 2)
    private Integer debitAmount;

    @Column(name = "mdr_tax", precision = 15, scale = 2)
    private Integer mdrTax;

    @Column(name = "merchant_id")
    private String merchantId;

    @Column(name = "unq_cust_id")
    private String unqCustId;

    @Column(name = "batch_no")
    private String batchNo;

    @Column(name = "settlement_status")
    private String settlementStatus;

    @Column(name = "init_mode")
    private String initMode;

    @Column(name = "purpose_code")
    private String purposeCode;

    @Column(name = "payer_account_type")
    private String payerAccountType;

    @Column(name = "bin")
    private String bin;

    @Column(name = "response")
    private String response;

    @Column(name = "report_flag", columnDefinition = "INT DEFAULT 0")
    private Integer reportFlag = 0;

    // [SQR_DQR_FLAG] NEW COLUMN - 0 = Static QR (static_qr_txn), 1 = Dynamic QR (upi_axis_callback)
    @Column(name = "sqr_dqr_flag", columnDefinition = "INT DEFAULT 0")
    private Integer sqrDqrFlag = 0;


    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Integer getReportFlag() {
        return reportFlag;
    }

    public void setReportFlag(Integer reportFlag) {
        this.reportFlag = reportFlag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getVpa() {
        return vpa;
    }

    public void setVpa(String vpa) {
        this.vpa = vpa;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(String generatedDate) {
        this.generatedDate = generatedDate;
    }

    public String getAxisCallBack() {
        return axisCallBack;
    }

    public void setAxisCallBack(String axisCallBack) {
        this.axisCallBack = axisCallBack;
    }

    public String getNodelAccount() {
        return nodelAccount;
    }

    public void setNodelAccount(String nodelAccount) {
        this.nodelAccount = nodelAccount;
    }

    public String getAxisFile() {
        return axisFile;
    }

    public void setAxisFile(String axisFile) {
        this.axisFile = axisFile;
    }

    public String getAcountNumber() {
        return acountNumber;
    }

    public void setAcountNumber(String acountNumber) {
        this.acountNumber = acountNumber;
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getMaskedAccountNumber() {
        return maskedAccountNumber;
    }

    public void setMaskedAccountNumber(String maskedAccountNumber) {
        this.maskedAccountNumber = maskedAccountNumber;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public String getAccountCustName() {
        return accountCustName;
    }

    public void setAccountCustName(String accountCustName) {
        this.accountCustName = accountCustName;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getCreditVpa() {
        return creditVpa;
    }

    public void setCreditVpa(String creditVpa) {
        this.creditVpa = creditVpa;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(Integer surcharge) {
        this.surcharge = surcharge;
    }

    public Integer getTax() {
        return tax;
    }

    public void setTax(Integer tax) {
        this.tax = tax;
    }

    public Integer getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(Integer debitAmount) {
        this.debitAmount = debitAmount;
    }

    public Integer getMdrTax() {
        return mdrTax;
    }

    public void setMdrTax(Integer mdrTax) {
        this.mdrTax = mdrTax;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getUnqCustId() {
        return unqCustId;
    }

    public void setUnqCustId(String unqCustId) {
        this.unqCustId = unqCustId;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(String settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public String getInitMode() {
        return initMode;
    }

    public void setInitMode(String initMode) {
        this.initMode = initMode;
    }

    public String getPurposeCode() {
        return purposeCode;
    }

    public void setPurposeCode(String purposeCode) {
        this.purposeCode = purposeCode;
    }

    public String getPayerAccountType() {
        return payerAccountType;
    }

    public void setPayerAccountType(String payerAccountType) {
        this.payerAccountType = payerAccountType;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    // [SQR_DQR_FLAG] getter / setter for the new sqr_dqr_flag column
    public Integer getSqrDqrFlag() {
        return sqrDqrFlag;
    }

    public void setSqrDqrFlag(Integer sqrDqrFlag) {
        this.sqrDqrFlag = sqrDqrFlag;
    }
}
