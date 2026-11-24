package com.bijlipay.Model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

@Entity
@Table(name = "nodal_account")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE)
public class NodalAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonProperty("UNIQUE_ID")
    @Column(name = "unique_id")
    private String uniqueId;
    @JsonProperty("FLAG")
    @Column(name = "flag")
    private String flag;
    @JsonProperty("LCHG_TIME")
    @Column(name = "lchg_time")
    private String lchgTime;
    @JsonProperty("ACID")
    @Column(name = "acid")
    private String acid;
    @JsonProperty("REFERENCENO")
    @Column(name = "reference_no")
    private String referenceNo;
    @JsonProperty("VALUE_DATE")
    @Column(name = "value_date")
    private String valueDate;
    @JsonProperty("BANK_NARRATION")
    @Column(name = "bank_narration", length = 500)
    private String bankNarration;
    @JsonProperty("DEBIT_CREDIT_IDENTIFIER")
    @Column(name = "debit_credit_identifier")
    private String debitCreditIdentifier;
    @JsonProperty("TRANSACTION_ID")
    @Column(name = "transaction_id")
    private String transactionId;
    @JsonProperty("TRANSACTION_MODE")
    @Column(name = "transaction_mode")
    private String transactionMode;
    @JsonProperty("TRANSACTION_AMOUNT")
    @Column(name = "transaction_amount")
    private String transactionAmount;
    @JsonProperty("CHEQUE_NUMBER")
    @Column(name = "cheque_number")
    private String chequeNumber;
    @JsonProperty("BENEFICIARY_ACCOUNT_NUMBER")
    @Column(name = "beneficiary_account_number")
    private String beneficiaryAccountNumber;
    @JsonProperty("BENEFICIARY_NAME")
    @Column(name = "beneficiary_name")
    private String beneficiaryName;
    @JsonProperty("BENEFICIARY_BANK_NAME")
    @Column(name = "beneficiary_bank_name")
    private String beneficiaryBankName;
    @JsonProperty("RUNNING_BALANCE")
    @Column(name = "running_balance")
    private String runningBalance;
    @JsonProperty("BRANCH_LOCATION")
    @Column(name = "branch_location")
    private String branchLocation;
    @JsonProperty("VIRTUAL_ACCOUNT")
    @Column(name = "virtual_account")
    private String virtualAccount;
    @JsonProperty("MMID")
    @Column(name = "mmid")
    private String mmid;
    @JsonProperty("TRANSFER_MODE")
    @Column(name = "transfer_mode")
    private String transferMode;
    @JsonProperty("SOURCE_VAL")
    @Column(name = "source_val")
    private String sourceVal;
    @JsonProperty("FUND")
    @Column(name = "fund")
    private String fund;
    @JsonProperty("TRANSACTION_CODE")
    @Column(name = "transaction_code")
    private String transactionCode;
    @JsonProperty("REMITTER_IFSC_CODE")
    @Column(name = "remitter_ifsc_code")
    private String remitterIfscCode;
    @JsonProperty("REMITTER_BANK_NAME")
    @Column(name = "remitter_bank_name")
    private String remitterBankName;
    @JsonProperty("REMITTER_ACCOUNT_TYPE")
    @Column(name = "remitter_account_type")
    private String remitterAccountType;
    @JsonProperty("REMITTER_NAME")
    @Column(name = "remitter_name")
    private String remitterName;
    @JsonProperty("REMITTER_ACCOUNT_NUMBER")
    @Column(name = "remitter_account_number")
    private String remitterAccountNumber;
    @JsonProperty("ETL_INSERT_DATE_DTD")
    @Column(name = "etl_insert_date_dtd")
    private Date etlInsertDateDtd;
    @Column(name = "created_at")
    private String CreatedAt;

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    // Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUniqueId() { return uniqueId; }
    public void setUniqueId(String uniqueId) { this.uniqueId = uniqueId; }

    public String getFlag() { return flag; }
    public void setFlag(String flag) { this.flag = flag; }

    public String getLchgTime() { return lchgTime; }
    public void setLchgTime(String lchgTime) { this.lchgTime = lchgTime; }

    public String getAcid() { return acid; }
    public void setAcid(String acid) { this.acid = acid; }

    public String getReferenceNo() { return referenceNo; }
    public void setReferenceNo(String referenceNo) { this.referenceNo = referenceNo; }

    public String getValueDate() { return valueDate; }
    public void setValueDate(String valueDate) { this.valueDate = valueDate; }

    public String getBankNarration() { return bankNarration; }
    public void setBankNarration(String bankNarration) { this.bankNarration = bankNarration; }

    public String getDebitCreditIdentifier() { return debitCreditIdentifier; }
    public void setDebitCreditIdentifier(String debitCreditIdentifier) { this.debitCreditIdentifier = debitCreditIdentifier; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getTransactionMode() { return transactionMode; }
    public void setTransactionMode(String transactionMode) { this.transactionMode = transactionMode; }

    public String getTransactionAmount() { return transactionAmount; }
    public void setTransactionAmount(String transactionAmount) { this.transactionAmount = transactionAmount; }

    public String getChequeNumber() { return chequeNumber; }
    public void setChequeNumber(String chequeNumber) { this.chequeNumber = chequeNumber; }

    public String getBeneficiaryAccountNumber() { return beneficiaryAccountNumber; }
    public void setBeneficiaryAccountNumber(String beneficiaryAccountNumber) { this.beneficiaryAccountNumber = beneficiaryAccountNumber; }

    public String getBeneficiaryName() { return beneficiaryName; }
    public void setBeneficiaryName(String beneficiaryName) { this.beneficiaryName = beneficiaryName; }

    public String getBeneficiaryBankName() { return beneficiaryBankName; }
    public void setBeneficiaryBankName(String beneficiaryBankName) { this.beneficiaryBankName = beneficiaryBankName; }

    public String getRunningBalance() { return runningBalance; }
    public void setRunningBalance(String runningBalance) { this.runningBalance = runningBalance; }

    public String getBranchLocation() { return branchLocation; }
    public void setBranchLocation(String branchLocation) { this.branchLocation = branchLocation; }

    public String getVirtualAccount() { return virtualAccount; }
    public void setVirtualAccount(String virtualAccount) { this.virtualAccount = virtualAccount; }

    public String getMmid() { return mmid; }
    public void setMmid(String mmid) { this.mmid = mmid; }

    public String getTransferMode() { return transferMode; }
    public void setTransferMode(String transferMode) { this.transferMode = transferMode; }

    public String getSourceVal() { return sourceVal; }
    public void setSourceVal(String sourceVal) { this.sourceVal = sourceVal; }

    public String getFund() { return fund; }
    public void setFund(String fund) { this.fund = fund; }

    public String getTransactionCode() { return transactionCode; }
    public void setTransactionCode(String transactionCode) { this.transactionCode = transactionCode; }

    public String getRemitterIfscCode() { return remitterIfscCode; }
    public void setRemitterIfscCode(String remitterIfscCode) { this.remitterIfscCode = remitterIfscCode; }

    public String getRemitterBankName() { return remitterBankName; }
    public void setRemitterBankName(String remitterBankName) { this.remitterBankName = remitterBankName; }

    public String getRemitterAccountType() { return remitterAccountType; }
    public void setRemitterAccountType(String remitterAccountType) { this.remitterAccountType = remitterAccountType; }

    public String getRemitterName() { return remitterName; }
    public void setRemitterName(String remitterName) { this.remitterName = remitterName; }

    public String getRemitterAccountNumber() { return remitterAccountNumber; }
    public void setRemitterAccountNumber(String remitterAccountNumber) { this.remitterAccountNumber = remitterAccountNumber; }

    public Date getEtlInsertDateDtd() { return etlInsertDateDtd; }
    public void setEtlInsertDateDtd(Date etlInsertDateDtd) { this.etlInsertDateDtd = etlInsertDateDtd; }

}
