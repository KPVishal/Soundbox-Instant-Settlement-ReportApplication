package com.bijlipay.Dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE)
public class DataDto {

    private String token_type;
    private String access_token;
    private String expires_in;
    private String message;

    @JsonProperty("AccountStatementReportRequestBody")
    private AccountStatementReportRequestBody AccountStatementReportRequestBody;

    @JsonProperty("AccountStatementReportResponseBody")
    private AccountStatementReportResponseBody accountStatementReportResponseBody;

    public com.bijlipay.Dto.AccountStatementReportRequestBody getAccountStatementReportRequestBody() {
        return AccountStatementReportRequestBody;
    }

    public void setAccountStatementReportRequestBody(com.bijlipay.Dto.AccountStatementReportRequestBody accountStatementReportRequestBody) {
        AccountStatementReportRequestBody = accountStatementReportRequestBody;
    }


    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AccountStatementReportResponseBody getAccountStatementReportResponseBody() {
        return accountStatementReportResponseBody;
    }

    public void setAccountStatementReportResponseBody(AccountStatementReportResponseBody accountStatementReportResponseBody) {
        this.accountStatementReportResponseBody = accountStatementReportResponseBody;
    }
}
