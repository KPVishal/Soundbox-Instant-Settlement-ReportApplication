package com.bijlipay.Model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "axis_settelment")
public class AxisSettelment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "txn_id",nullable = true)
    private String txnId;

    @Column(name = "amount",nullable = true)
    private String amount;

    @Column(name = "vpa",nullable = true)
    private String vpa;

    @Column(name = "merchant_name",nullable = true)
    private String merchantName;

    @Column(name = "generated_date",nullable = true)
    private Date generatedDate;

    @Column(name = "axis_callback",nullable = true)
    private String axisCallBack;

    @Column(name = "nodel_account",nullable = true)
    private String nodelAccount;

    @Column(name = "axis_file",nullable = true)
    private String axisFile;


    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public Date getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(Date generatedDate) {
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


}
