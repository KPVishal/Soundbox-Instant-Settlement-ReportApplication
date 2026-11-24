package com.bijlipay.Dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE)
public class AccountStatementReportResponseBody {
    private String corpCode;
    private String accountNumber;
    private String userPrinciple;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss",timezone = "Asia/Kolkata")
    private Date fromDate;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss",timezone = "Asia/Kolkata")
    private Date toDate;
    private String minimumAmount;
    private String maximumAmount;
    private String transactionAccounting;


    private int statusCode;
    private String message;
    private String requestTimeEpoch;
    private String requestId;

    private int rowsCount;
    private double openingBalance;
    private double closingBalance;

    private String currency;

    @JsonProperty("paging")
    private PagingDto paging;
    @JsonProperty("data")
    private List<TransactionRecordDto> data;

    public String getCorpCode() {
        return corpCode;
    }

    public void setCorpCode(String corpCode) {
        this.corpCode = corpCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getUserPrinciple() {
        return userPrinciple;
    }

    public void setUserPrinciple(String userPrinciple) {
        this.userPrinciple = userPrinciple;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(String minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public String getMaximumAmount() {
        return maximumAmount;
    }

    public void setMaximumAmount(String maximumAmount) {
        this.maximumAmount = maximumAmount;
    }

    public String getTransactionAccounting() {
        return transactionAccounting;
    }

    public void setTransactionAccounting(String transactionAccounting) {
        this.transactionAccounting = transactionAccounting;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequestTimeEpoch() {
        return requestTimeEpoch;
    }

    public void setRequestTimeEpoch(String requestTimeEpoch) {
        this.requestTimeEpoch = requestTimeEpoch;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getRowsCount() {
        return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
        this.rowsCount = rowsCount;
    }

    public double getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(double openingBalance) {
        this.openingBalance = openingBalance;
    }

    public double getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(double closingBalance) {
        this.closingBalance = closingBalance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PagingDto getPaging() {
        return paging;
    }

    public void setPaging(PagingDto paging) {
        this.paging = paging;
    }

    public List<TransactionRecordDto> getData() {
        return data;
    }

    public void setData(List<TransactionRecordDto> data) {
        this.data = data;
    }
}
