package com.ramon.guardai.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class SerialData {
    @JsonProperty("data")
    String date;
    @JsonProperty("valor")
    BigDecimal value;

    public SerialData() {}

    public String getDate() { return this.date; }
    public BigDecimal getValue() { return this.value; }
}
