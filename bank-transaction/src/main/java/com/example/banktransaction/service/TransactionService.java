package com.example.banktransaction.service;

import com.example.banktransaction.dto.DefaultApiResponse;
import com.example.banktransaction.entity.Response;
import com.example.banktransaction.entity.Transaction;
import com.example.banktransaction.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class TransactionService {

    @LoadBalanced
    private final RestTemplate restTemplate;

    @Autowired
    private TemplateEngine templateEngine;

    private final String walletServiceBaseUrl = "http://BANK-WALLET:8082/api/v1/wallet";

    @Autowired
    private TransactionRepository transactionRepository;

    public TransactionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    // CRUD methods
    public DefaultApiResponse create(Transaction transaction){
        DefaultApiResponse apiResponse = new DefaultApiResponse();
        try {
            transactionRepository.save(transaction);
            apiResponse.setStatus("00");
            apiResponse.setMessage("Successfully saved Transaction");
            apiResponse.setData(transaction);
        } catch(Exception e) {
            System.out.println("Error: "+ e);
            apiResponse.setStatus("99");
            apiResponse.setMessage("Failed to save Transaction");
        }
        return apiResponse;
    }

//    public boolean doesWalletExist(Long walletId) {
//        Optional<Wallet> wallet = Optional.ofNullable(restTemplate.getForObject(walletServiceBaseUrl + "/" + walletId, Wallet.class));
//        if (wallet.isPresent()){
//            return true;
//        }
//        return false;
//    }
    public DefaultApiResponse readOne(Long transactionId, String walletId){
        DefaultApiResponse apiResponse = new DefaultApiResponse();
        Long walletIdToLong = Long.parseLong(walletId);
        Transaction transaction = transactionRepository.findByIdAndWalletId(transactionId, walletIdToLong);
        if (transaction != null) {
            apiResponse.setStatus("00");
            apiResponse.setMessage("Successfully retrieved Transactions");
            apiResponse.setData(transaction);
            return apiResponse;
        }
        apiResponse.setStatus("99");
        apiResponse.setMessage("ID cannot be found : Check if ID is correct");
        return apiResponse;
    }

    public  DefaultApiResponse readOneUserTransactions(String walletId){
        DefaultApiResponse apiResponse = new DefaultApiResponse();
        try {
            Long walletIdToLong = Long.parseLong(walletId);
            List<Transaction> transactions = transactionRepository.findByWalletId(walletIdToLong);
            apiResponse.setStatus("00");
            apiResponse.setMessage("Successfully retrieved Transactions");
            apiResponse.setData(transactions);
            return apiResponse;
//            return transactions;
        } catch(Exception e) {
            System.out.println("Error: "+ e);
            apiResponse.setStatus("99");
            apiResponse.setMessage("Failed to retrieve Transactions");
            return apiResponse;
        }
    }

    public DefaultApiResponse readAll(String role){
        DefaultApiResponse apiResponse = new DefaultApiResponse();
        if(role.equalsIgnoreCase("admin")){
            List<Transaction> transactionRes = transactionRepository.findAll();
            apiResponse.setStatus("00");
            apiResponse.setMessage("Successfully retrieved all Transactions");
            apiResponse.setData(transactionRes);
            return apiResponse;
        }
        apiResponse.setStatus("99");
        apiResponse.setMessage("cannot access Resource");
        return apiResponse;

    }
    public DefaultApiResponse update(String walletId, Long transactionId, Transaction updater){
        Long walletIdToLong = Long.parseLong(walletId);
        DefaultApiResponse apiResponse = new DefaultApiResponse();
        if(transactionRepository.existsByIdAndWalletId(transactionId, walletIdToLong)){
            updater.setId(transactionId);
            Transaction transaction = transactionRepository.save(updater);
            apiResponse.setStatus("00");
            apiResponse.setMessage("Successfully Updated Transactions");
            apiResponse.setData(transaction);
            return apiResponse;

        }
        apiResponse.setStatus("99");
        apiResponse.setMessage("failed updating transaction successfully");
        return apiResponse;
    }
    public DefaultApiResponse delete(String walletId, Long transactionId){
        DefaultApiResponse apiResponse = new DefaultApiResponse();
        Long walletIdToLong = Long.parseLong(walletId);
        if(transactionRepository.existsByIdAndWalletId(transactionId, walletIdToLong)) {
            transactionRepository.deleteById(transactionId);
            apiResponse.setStatus("00");
            apiResponse.setMessage("Success");
        } else {
            apiResponse.setStatus("00");
            apiResponse.setMessage("failed deleting transaction");
        }
        return apiResponse;
    }

    public DefaultApiResponse walletReadOneUserTransactions(Long walletId) {
        DefaultApiResponse apiResponse = new DefaultApiResponse();
        List<Transaction> transactionRes = transactionRepository.findByWalletId(walletId);
        apiResponse.setStatus("00");
        apiResponse.setMessage("Success");
        apiResponse.setData(transactionRes);
        return apiResponse;
    }

    public DefaultApiResponse monthlyStatement(String walletId, int year, int month) throws IOException {
        Long walletIdToLong = Long.parseLong(walletId);
//        LocalDate startDate = LocalDate.now();

        // Set the start date as January 1st of the specified year
        LocalDate startDate = LocalDate.of(year, month, 1);

        // Set the end date as December 31st of the specified year
//        LocalDate endDate = LocalDate.of(year, month, 31);

        // Locate the current month
        LocalDate firstDayOfMonth = startDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth());
//        System.out.println(startDate);
//        System.out.println(firstDayOfMonth);
//        System.out.println(lastDayOfMonth);

        List<Transaction> transactionList = transactionRepository.findByWalletIdAndDateBetween(walletIdToLong ,firstDayOfMonth, lastDayOfMonth);
//        System.out.println(transactionList);


        // Render HTML template with Thymeleaf
//        Context context = new Context();
//        context.setVariable("transactions", transactionList);
//        String template = templateEngine.process("monthly_statement_template", context);

        // Convert HTML to PDF
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        ITextRenderer renderer = new ITextRenderer();
//        renderer.setDocumentFromString(template);
//        renderer.layout();
//        renderer.createPDF(outputStream);
//        outputStream.close();
//        byte[] bytes = outputStream.toByteArray();

        // Create a temporary file to store PDF content
//        Path tempFile = Files.createTempFile("monthly_statement", ".pdf");
//        Files.write(tempFile, bytes);

        // Set up response headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "monthly_statement.pdf");

        // Serve the PDF file as a response
//        Resource resource = new FileSystemResource(tempFile.toFile());
//        ResponseEntity<Resource> responseEntity =
//        return ResponseEntity.ok()
//                .headers(headers)
//                .contentLength(resource.contentLength())
//                .body(resource);
        DefaultApiResponse apiResponse = new DefaultApiResponse();
        apiResponse.setStatus("00");
        apiResponse.setMessage("Monthly Transactions");
        apiResponse.setData(transactionList);
        return apiResponse;
    }

    public DefaultApiResponse yearlyStatement(String walletId, int year){
        Long walletIdToLong = Long.parseLong(walletId);

        // Set the start date as January 1st of the specified year
        LocalDate startDate = LocalDate.of(year, 1, 1);

        // Set the end date as December 31st of the specified year
        LocalDate endDate = LocalDate.of(year, 12, 31);

        // Retrieve transactions for the specified year
        List<Transaction> transactionList = transactionRepository.findByWalletIdAndDateBetween(walletIdToLong ,startDate, endDate);

        // Print transactionList for debugging
        System.out.println(transactionList);

       // Process transactionList and generate response (your implementation here)
        DefaultApiResponse apiResponse = new DefaultApiResponse();
        apiResponse.setStatus("00");
        apiResponse.setMessage("Yearly Transactions");
        apiResponse.setData(transactionList);
        return apiResponse;

//        return null; // Modify this line to return the appropriate Response
    }
    public DefaultApiResponse statementForDates(String walletId, LocalDate startDate, LocalDate endDate) {
        Long walletIdToLong = Long.parseLong(walletId);
//        List<Transaction> transactions = transactionRepository.findByWalletId(walletIdToLong);
//        List<Transaction> transactionList = transactionRepository.findByDateBetween(startDate, endDate);

        // Retrieve transactions between the given dates for the user's wallet
        List<Transaction> transactionList = transactionRepository.findByWalletIdAndDateBetween(walletIdToLong, startDate, endDate);

        DefaultApiResponse apiResponse = new DefaultApiResponse();
        apiResponse.setStatus("00");
        String message = String.format("Transactions from %s to %s", startDate, endDate);
        apiResponse.setMessage(message);
        apiResponse.setData(transactionList);
        return apiResponse;
    }
}
