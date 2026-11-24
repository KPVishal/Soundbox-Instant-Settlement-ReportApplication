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
public class AxisTokenDto {
    @JsonProperty("Data")
    private DataDto Data;
    @JsonProperty("Risk")
    private Object Risk;
    @JsonProperty("Links")
    private Object Links;
    @JsonProperty("Meta")
    private Object Meta;

    public com.bijlipay.Dto.DataDto getData() {
        return Data;
    }

    public void setData(com.bijlipay.Dto.DataDto data) {
        Data = data;
    }

    public Object getRisk() {
        return Risk;
    }

    public void setRisk(Object risk) {
        Risk = risk;
    }

    public Object getLinks() {
        return Links;
    }

    public void setLinks(Object links) {
        Links = links;
    }

    public Object getMeta() {
        return Meta;
    }

    public void setMeta(Object meta) {
        Meta = meta;
    }
}
