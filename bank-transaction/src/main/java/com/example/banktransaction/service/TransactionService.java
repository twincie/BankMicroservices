package com.example.banktransaction.service;

import com.example.banktransaction.entity.Response;
import com.example.banktransaction.entity.Transaction;
import com.example.banktransaction.repository.TransactionRepository;
import com.lowagie.text.DocumentException;
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
import org.xhtmlrenderer.pdf.ITextRenderer;

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
    public Transaction create(Transaction transaction){
        return transactionRepository.save(transaction);
    }

//    public boolean doesWalletExist(Long walletId) {
//        Optional<Wallet> wallet = Optional.ofNullable(restTemplate.getForObject(walletServiceBaseUrl + "/" + walletId, Wallet.class));
//        if (wallet.isPresent()){
//            return true;
//        }
//        return false;
//    }
    public Response readOne(Long transactionId, String walletId){
        Response response = new Response();
        Long walletIdToLong = Long.parseLong(walletId);
        Transaction transaction = transactionRepository.findByIdAndWalletId(transactionId, walletIdToLong);
        if (transaction != null) {
            response.setStatus(HttpStatus.OK);
            response.setMessage("Successfully retrieved Transactions");
            response.setBody(transaction);
            return response;
        }
        response.setStatus(HttpStatus.FORBIDDEN);
        response.setMessage("ID cannot be found : Check if ID is correct");
        return response;
    }

    public  Response readOneUserTransactions(String walletId){
        try {
            Response response = new Response();
            Long walletIdToLong = Long.parseLong(walletId);
            List<Transaction> transactions = transactionRepository.findByWalletId(walletIdToLong);
            response.setStatus(HttpStatus.OK);
            response.setMessage("Successfully retrieved Transactions");
            response.setBody(transactions);
            return response;
//            return transactions;
        } catch(Exception e) {
            Response response = new Response();
            System.out.println("Error: "+ e);
            response.setStatus(HttpStatus.OK);
            response.setMessage("Successfully retrieved Transactions");
            return response;
        }
    }

    public Response readAll(String role){
        Response response = new Response();
        if(role.equalsIgnoreCase("admin")){
            List<Transaction> transactionRes = transactionRepository.findAll();
            response.setStatus(HttpStatus.OK);
            response.setMessage("Successfully retrieved all Transactions");
            response.setBody(transactionRes);
            return response;
        }
        response.setStatus(HttpStatus.FORBIDDEN);
        response.setMessage("cannot access Resource");
        return response;

    }
    public Response update(String walletId, Long transactionId, Transaction updater){
        Long walletIdToLong = Long.parseLong(walletId);
        Response response = new Response();
        if(transactionRepository.existsByIdAndWalletId(transactionId, walletIdToLong)){
            updater.setId(transactionId);
            Transaction transaction = transactionRepository.save(updater);
            response.setStatus(HttpStatus.OK);
            response.setMessage("Successfully Updated Transactions");
            response.setBody(transaction);
            return response;

        }
        response.setStatus(HttpStatus.OK);
        response.setMessage("Didnt update transaction successfully");
        return response;
    }
    public void delete(String walletId, Long transactionId){
        Long walletIdToLong = Long.parseLong(walletId);
        if(transactionRepository.existsByIdAndWalletId(transactionId, walletIdToLong)) {
            transactionRepository.deleteById(transactionId);
        }
    }

    public List<Transaction> walletReadOneUserTransactions(Long walletId) {
        return  transactionRepository.findByWalletId(walletId);
    }

    public ResponseEntity<Resource> monthlyStatement(String walletId, int year, int month) throws DocumentException, IOException {
        Long walletIdToLong = Long.parseLong(walletId);
//        LocalDate startDate = LocalDate.now();

        // Set the start date as January 1st of the specified year
        LocalDate startDate = LocalDate.of(year, month, 1);

        // Set the end date as December 31st of the specified year
//        LocalDate endDate = LocalDate.of(year, month, 31);

        // Locate the current month
        LocalDate firstDayOfMonth = startDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth());
        System.out.println(startDate);
        System.out.println(firstDayOfMonth);
        System.out.println(lastDayOfMonth);

        List<Transaction> transactionList = transactionRepository.findByWalletIdAndDateBetween(walletIdToLong ,firstDayOfMonth, lastDayOfMonth);
//        System.out.println(transactionList);


        // Render HTML template with Thymeleaf
        Context context = new Context();
        context.setVariable("transactions", transactionList);
        String template = templateEngine.process("monthly_statement_template", context);

        // Convert HTML to PDF
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(template);
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();
        byte[] bytes = outputStream.toByteArray();

        // Create a temporary file to store PDF content
        Path tempFile = Files.createTempFile("monthly_statement", ".pdf");
        Files.write(tempFile, bytes);

        // Set up response headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "monthly_statement.pdf");

        // Serve the PDF file as a response
        Resource resource = new FileSystemResource(tempFile.toFile());
//        ResponseEntity<Resource> responseEntity =
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .body(resource);
//        Response response = new Response();
//        response.setStatus(HttpStatus.OK);
//        response.setMessage("Monthly Transactions");
//        response.setBody(responseEntity);
//        return response;
    }

    public Response yearlyStatement(String walletId, int year){
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
        Response response = new Response();
        response.setStatus(HttpStatus.OK);
        response.setMessage("Yearly Transactions");
        response.setBody(transactionList);
        return response;

//        return null; // Modify this line to return the appropriate Response
    }
    public Response statementForDates(String walletId, LocalDate startDate, LocalDate endDate) {
        Long walletIdToLong = Long.parseLong(walletId);
//        List<Transaction> transactions = transactionRepository.findByWalletId(walletIdToLong);
//        List<Transaction> transactionList = transactionRepository.findByDateBetween(startDate, endDate);

        // Retrieve transactions between the given dates for the user's wallet
        List<Transaction> transactionList = transactionRepository.findByWalletIdAndDateBetween(walletIdToLong, startDate, endDate);

        Response response = new Response();
        response.setStatus(HttpStatus.OK);
        String message = String.format("Transactions from %s to %s", startDate, endDate);
        response.setMessage(message);
        response.setBody(transactionList);
        return response;
    }
}
