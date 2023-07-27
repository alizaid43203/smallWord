package com.smallworld;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smallworld.data.Transaction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class TransactionDataFetcher {
    private final List<Transaction> transactions;
    public TransactionDataFetcher(String filePath) throws IOException {
        String jsonContent = Files.readString(Path.of(filePath));
        ObjectMapper objectMapper = new ObjectMapper();
        transactions = objectMapper.readValue(jsonContent, new TypeReference<List<Transaction>>() {});
    }
    /**
     * Returns the sum of the amounts of all transactions
     * using streams features mapping the values to double stream to calculate the total sum of transaction
     */
    public double getTotalTransactionAmount() {
        return transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    /**
     * Returns the sum of the amounts of all transactions sent by the specified client
     * bellow method first search out that client whose full name is passed and then sum od its total transaction amount will be returned.
     */
    public double getTotalTransactionAmountSentBy(String senderFullName) {
        return transactions.stream()
                .filter(transaction -> transaction.getSenderFullName().equals(senderFullName))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    /**
     * Returns the highest transaction amount
     * below method will return the max transaction amount among all transactions
     */
    public double getMaxTransactionAmount() {
        return transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .max()
                .orElse(0.0);
    }

    /**
     * Counts the number of unique clients that sent or received a transaction
     * below method will return the unique clients count all the translation data which means that translation happened
     */
    public long countUniqueClients() {
        Set<String> uniqueClients = new HashSet<>();
        transactions.forEach(transaction -> {
            uniqueClients.add(transaction.getSenderFullName());
            uniqueClients.add(transaction.getBeneficiaryFullName());
        });
        return uniqueClients.size();
    }

    /**
     * Returns whether a client (sender or beneficiary) has at least one transaction with a compliance
     * issue that has not been solved
     * in bellow method the stream logic works as that it will check with sender and beneficiary name and with that if
     * there is no any issueId and transaction is not resolved then it will consider it as there is no open compliance issue
     *
     */
    public boolean hasOpenComplianceIssues(String clientFullName) {
        return transactions.stream()
                .anyMatch(transaction ->
                        (transaction.getSenderFullName().equals(clientFullName) ||
                                transaction.getBeneficiaryFullName().equals(clientFullName))
                                && transaction.getIssueId() != null
                                && !transaction.isIssueSolved());
    }

    /**
     * Returns all transactions indexed by beneficiary name
     * here the list of translations of ByBeneficiaryName will be return method Collectors.groupingBy will group all the transactions
     * beneficiary name and make a list of it
     */
    public Map<String, List<Transaction>> getTransactionsByBeneficiaryName() {// updated the parameter Transaction=>List<Transaction> according to requirement, as
        return transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getBeneficiaryFullName));
    }

    /**
     * Returns the identifiers of all open compliance issues
     * the below method will first filter out the transaction whose isIssueSolved is false and IssueId is not null then result will be those
     * transaction which are not resolved and after the map will get he issues ids and make the set of it and return
     */
    public Set<Integer> getUnsolvedIssueIds() {
        return transactions.stream()
                .filter(transaction -> transaction.getIssueId() != null && !transaction.isIssueSolved())
                .map(Transaction::getIssueId)
                .collect(Collectors.toSet());
    }

    /**
     * Returns a list of all solved issue messages
     * belo method first will filter out the data of solved transaction then will get its mesages and the return list
     */
    public List<String> getAllSolvedIssueMessages() {

        return transactions.stream()
                .filter(transaction -> transaction.getIssueId() != null && transaction.isIssueSolved())
                .map(Transaction::getIssueMessage)
                .collect(Collectors.toList());
    }

    /**
     * Returns the 3 transactions with the highest amount sorted by amount descending
     * below method first will compare the amounts and then make them into reverse order in descending order and will take the top 3 transactions
     * on the bases of their amount
     */
    public List<Transaction> getTop3TransactionsByAmount() {
        return transactions.stream()
                .sorted(Comparator.comparingDouble(Transaction::getAmount).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

    /**
     * Returns the senderFullName of the sender with the most total sent amount
     *Below method first will calculate the every sender all transaction amounts by grouping sender name after that from that map
     *will compare amounts and take the max amount and then get its name in last
     */
    public Optional<String> getTopSender() {
        Map<String, Double> senderToTotalSentAmount = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getSenderFullName,
                        Collectors.summingDouble(Transaction::getAmount)));
        return senderToTotalSentAmount.entrySet().stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .map(Map.Entry::getKey);
    }

}
