package com.smallworld.data;

import lombok.Data;

@Data
public class Transaction {
    private long mtn;
    private double amount;
    private String senderFullName;
    private int senderAge;
    private String beneficiaryFullName;
    private int beneficiaryAge;
    private Integer issueId;
    private boolean issueSolved;
    private String issueMessage;
}
