package com.smallworld;

import com.smallworld.data.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionDataFetcherTest {
    private TransactionDataFetcher transactionDataFetcher;

    @BeforeEach
    void setUp() throws IOException {
        transactionDataFetcher = new TransactionDataFetcher("transactions.json");
    }

    @Test
    void getTotalTransactionAmount() {
        double totalAmount = transactionDataFetcher.getTotalTransactionAmount();
        assertEquals(4371.37, totalAmount, 0.01);
    }

    @Test
    void getTotalTransactionAmountSentBy_existingSender_returnsTotalAmount() {
        double totalAmount = transactionDataFetcher.getTotalTransactionAmountSentBy("Tom Shelby");
        assertEquals(828.26, totalAmount, 0.001);
    }

    @Test
    void getTotalTransactionAmountSentBy_nonExistingSender_returnsZero() {
        double totalAmount = transactionDataFetcher.getTotalTransactionAmountSentBy("John Doe");
        assertEquals(0.0, totalAmount, 0.001);
    }

    @Test
    void getMaxTransactionAmount() {
        double maxAmount = transactionDataFetcher.getMaxTransactionAmount();
        assertEquals(985.0, maxAmount, 0.001);
    }

    @Test
    void countUniqueClients() {
        assertEquals(14, transactionDataFetcher.countUniqueClients());
    }

    @Test
    void hasOpenComplianceIssues_clientWithOpenIssues_returnsTrue() {
        assertTrue(transactionDataFetcher.hasOpenComplianceIssues("Tom Shelby"));
    }

    @Test
    void hasOpenComplianceIssues_clientWithoutOpenIssues_returnsFalse() throws IOException {
        assertFalse(transactionDataFetcher.hasOpenComplianceIssues("Aunt Polly"));

    }

    @Test
    void getTransactionsByBeneficiaryName() throws IOException {
        Map<String, List<Transaction>> transactionsByBeneficiaryName = transactionDataFetcher.getTransactionsByBeneficiaryName();
        assertEquals(10, transactionsByBeneficiaryName.size());
        assertTrue(transactionsByBeneficiaryName.containsKey("Alfie Solomons"));
        assertEquals(1, transactionsByBeneficiaryName.get("Alfie Solomons").size());
        assertTrue(transactionsByBeneficiaryName.containsKey("Arthur Shelby"));
        assertEquals(2, transactionsByBeneficiaryName.get("Arthur Shelby").size());
    }

    @Test
    void getUnsolvedIssueIds() {
        Set<Integer> unsolvedIssueIds = transactionDataFetcher.getUnsolvedIssueIds();
        assertEquals(Set.of(1, 3, 15, 54, 99), unsolvedIssueIds);
    }

    @Test
    void getAllSolvedIssueMessages() {
        List<String> solvedIssueMessages = transactionDataFetcher.getAllSolvedIssueMessages();
        assertEquals(List.of("Never gonna give you up", "Never gonna let you down", "Never gonna run around and desert you"), solvedIssueMessages);
    }

    @Test
    void getTop3TransactionsByAmount() {
        List<Transaction> top3Transactions = transactionDataFetcher.getTop3TransactionsByAmount();
        assertEquals(3, top3Transactions.size());
        assertEquals(985.0, top3Transactions.get(0).getAmount(), 0.001);
        assertEquals(666.0, top3Transactions.get(1).getAmount(), 0.001);
        assertEquals(666.0, top3Transactions.get(2).getAmount(), 0.001);
    }

    @Test
    void getTopSender() {
        Optional<String> topSender = transactionDataFetcher.getTopSender();
        assertTrue(topSender.isPresent());
        assertEquals("Grace Burgess", topSender.get());
    }
}
