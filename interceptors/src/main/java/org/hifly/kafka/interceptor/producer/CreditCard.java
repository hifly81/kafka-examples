package org.hifly.kafka.interceptor.producer;

import java.io.Serializable;

public class CreditCard implements Serializable {

    private static final long serialVersionUID = 1L;

    private String creditCard;

    public CreditCard() {}

    public String getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    public String toString() {
        return creditCard;
    }


}