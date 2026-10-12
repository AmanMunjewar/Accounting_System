package com.vectorbank.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.vectorbank.exception.BankingException;
import com.vectorbank.model.Account;
import com.vectorbank.model.Transaction;
import com.vectorbank.service.BankingService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

/**
 * Built-in HTTP Web Server using Java 17 standard library (com.sun.net.httpserver).
 * Provides a clean, modern Web UI and REST-like JSON endpoints without any third-party framework dependencies.
 */
public class BankingWebServer {
    private final int port;
    private final BankingService bankingService;
    private HttpServer server;

    public BankingWebServer(int port, BankingService bankingService) {
        this.port = port;
        this.bankingService = bankingService;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // Static Web UI route
        server.createContext("/", new StaticUiHandler());

        // API Endpoints
        server.createContext("/api/create-account", new CreateAccountHandler());
        server.createContext("/api/login", new LoginHandler());
        server.createContext("/api/account", new AccountInfoHandler());
        server.createContext("/api/deposit", new DepositHandler());
        server.createContext("/api/withdraw", new WithdrawHandler());
        server.createContext("/api/passbook", new PassbookHandler());
        server.createContext("/api/top-k", new TopKHandler());

        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());

        server.start();
        System.out.println("🌐 VECTOR Bank Web UI running at http://localhost:" + port);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    // -------------------------------------------------------------
    // HTTP Handlers
    // -------------------------------------------------------------

    private class StaticUiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method Not Allowed", "text/plain");
                return;
            }

            String html = WebUiTemplate.getHtml();
            byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    private class CreateAccountHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}", "application/json");
                return;
            }

            Map<String, String> params = parseBody(exchange.getRequestBody());
            try {
                String name = params.getOrDefault("name", "").trim();
                LocalDate dob = LocalDate.parse(params.getOrDefault("dob", "").trim());
                String pan = params.getOrDefault("pan", "").trim().toUpperCase();
                BigDecimal initialDeposit = new BigDecimal(params.getOrDefault("initialDeposit", "0").trim());
                String password = params.getOrDefault("password", "").trim();

                int accountNo = bankingService.createAccount(name, dob, pan, initialDeposit, password);
                String json = String.format("{\"success\":true,\"accountNo\":%d,\"balance\":\"%.2f\",\"message\":\"Account created successfully\"}",
                        accountNo, initialDeposit);
                sendResponse(exchange, 200, json, "application/json");
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"success\":false,\"error\":\"" + escapeJson(e.getMessage()) + "\"}", "application/json");
            }
        }
    }

    private class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}", "application/json");
                return;
            }

            Map<String, String> params = parseBody(exchange.getRequestBody());
            try {
                int accountNo = Integer.parseInt(params.getOrDefault("accountNo", "0").trim());
                String password = params.getOrDefault("password", "").trim();

                Account acc = bankingService.authenticate(accountNo, password);
                String json = String.format(
                        "{\"success\":true,\"accountNo\":%d,\"name\":\"%s\",\"dob\":\"%s\",\"pan\":\"%s\",\"balance\":\"%.2f\"}",
                        acc.getAccountNo(),
                        escapeJson(acc.getCustomer().getName()),
                        acc.getCustomer().getDob(),
                        escapeJson(acc.getCustomer().getPan()),
                        acc.getBalance()
                );
                sendResponse(exchange, 200, json, "application/json");
            } catch (Exception e) {
                sendResponse(exchange, 401, "{\"success\":false,\"error\":\"" + escapeJson(e.getMessage()) + "\"}", "application/json");
            }
        }
    }

    private class AccountInfoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> query = parseQuery(exchange.getRequestURI().getQuery());
            try {
                int accountNo = Integer.parseInt(query.getOrDefault("accountNo", "0"));
                Account acc = bankingService.getAccountInfo(accountNo);
                String json = String.format(
                        "{\"success\":true,\"accountNo\":%d,\"name\":\"%s\",\"dob\":\"%s\",\"pan\":\"%s\",\"balance\":\"%.2f\"}",
                        acc.getAccountNo(),
                        escapeJson(acc.getCustomer().getName()),
                        acc.getCustomer().getDob(),
                        escapeJson(acc.getCustomer().getPan()),
                        acc.getBalance()
                );
                sendResponse(exchange, 200, json, "application/json");
            } catch (Exception e) {
                sendResponse(exchange, 404, "{\"success\":false,\"error\":\"" + escapeJson(e.getMessage()) + "\"}", "application/json");
            }
        }
    }

    private class DepositHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}", "application/json");
                return;
            }

            Map<String, String> params = parseBody(exchange.getRequestBody());
            try {
                int accountNo = Integer.parseInt(params.getOrDefault("accountNo", "0").trim());
                BigDecimal amount = new BigDecimal(params.getOrDefault("amount", "0").trim());

                BigDecimal newBal = bankingService.deposit(accountNo, amount);
                String json = String.format("{\"success\":true,\"newBalance\":\"%.2f\",\"message\":\"Deposit successful\"}", newBal);
                sendResponse(exchange, 200, json, "application/json");
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"success\":false,\"error\":\"" + escapeJson(e.getMessage()) + "\"}", "application/json");
            }
        }
    }

    private class WithdrawHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}", "application/json");
                return;
            }

            Map<String, String> params = parseBody(exchange.getRequestBody());
            try {
                int accountNo = Integer.parseInt(params.getOrDefault("accountNo", "0").trim());
                BigDecimal amount = new BigDecimal(params.getOrDefault("amount", "0").trim());

                BigDecimal newBal = bankingService.withdraw(accountNo, amount);
                String json = String.format("{\"success\":true,\"newBalance\":\"%.2f\",\"message\":\"Withdrawal successful\"}", newBal);
                sendResponse(exchange, 200, json, "application/json");
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"success\":false,\"error\":\"" + escapeJson(e.getMessage()) + "\"}", "application/json");
            }
        }
    }

    private class PassbookHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> query = parseQuery(exchange.getRequestURI().getQuery());
            try {
                int accountNo = Integer.parseInt(query.getOrDefault("accountNo", "0"));
                List<Transaction> list = bankingService.getPassbook(accountNo);

                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < list.size(); i++) {
                    Transaction t = list.get(i);
                    sb.append(String.format("{\"id\":%d,\"time\":\"%s\",\"deposit\":\"%.2f\",\"withdraw\":\"%.2f\",\"balance\":\"%.2f\"}",
                            t.getTransactionId(),
                            t.getTimestamp().toString().substring(0, 19),
                            t.getDeposit(),
                            t.getWithdraw(),
                            t.getBalanceAfter()));
                    if (i < list.size() - 1) sb.append(",");
                }
                sb.append("]");

                String json = "{\"success\":true,\"transactions\":" + sb + "}";
                sendResponse(exchange, 200, json, "application/json");
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"success\":false,\"error\":\"" + escapeJson(e.getMessage()) + "\"}", "application/json");
            }
        }
    }

    private class TopKHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> query = parseQuery(exchange.getRequestURI().getQuery());
            try {
                int accountNo = Integer.parseInt(query.getOrDefault("accountNo", "0"));
                int k = Integer.parseInt(query.getOrDefault("k", "5"));

                List<Transaction> list = bankingService.getTopKTransactions(accountNo, k);

                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < list.size(); i++) {
                    Transaction t = list.get(i);
                    sb.append(String.format("{\"id\":%d,\"type\":\"%s\",\"amount\":\"%.2f\",\"time\":\"%s\",\"balance\":\"%.2f\"}",
                            t.getTransactionId(),
                            t.isDeposit() ? "DEPOSIT" : "WITHDRAW",
                            t.getAmount(),
                            t.getTimestamp().toString().substring(0, 19),
                            t.getBalanceAfter()));
                    if (i < list.size() - 1) sb.append(",");
                }
                sb.append("]");

                String json = "{\"success\":true,\"k\":" + k + ",\"transactions\":" + sb + "}";
                sendResponse(exchange, 200, json, "application/json");
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"success\":false,\"error\":\"" + escapeJson(e.getMessage()) + "\"}", "application/json");
            }
        }
    }

    // -------------------------------------------------------------
    // Helper Methods
    // -------------------------------------------------------------

    private static void sendResponse(HttpExchange exchange, int statusCode, String body, String contentType) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static Map<String, String> parseBody(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        String body = buffer.toString(StandardCharsets.UTF_8);

        Map<String, String> map = new HashMap<>();
        if (body.isEmpty()) return map;

        // Try application/x-www-form-urlencoded
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                map.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            } else if (kv.length == 1) {
                map.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8), "");
            }
        }
        return map;
    }

    private static Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                map.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            }
        }
        return map;
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
