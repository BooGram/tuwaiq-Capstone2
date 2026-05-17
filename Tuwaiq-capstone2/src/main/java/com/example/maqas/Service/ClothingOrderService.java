package com.example.maqas.Service;

import com.example.maqas.Api.ApiException;
import com.example.maqas.Model.ClothingOrder;
import com.example.maqas.Model.Customer;
import com.example.maqas.Model.TailorShop;
import com.example.maqas.Repository.ClothingOrderRepository;
import com.example.maqas.Repository.CustomerRepository;
import com.example.maqas.Repository.TailorShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClothingOrderService {

    private final ClothingOrderRepository clothingOrderRepository;
    private final CustomerRepository customerRepository;
    private final TailorShopRepository tailorShopRepository;
    private final ObjectProvider<JavaMailSender> javaMailSenderProvider;

    @Value("${notification.email.enabled:false}")
    private Boolean emailEnabled;

    @Value("${spring.mail.username:}")
    private String emailFrom;

    @Value("${notification.whatsapp.enabled:false}")
    private Boolean whatsappEnabled;

    @Value("${ultramsg.instance-id:}")
    private String ultramsgInstanceId;

    @Value("${ultramsg.token:}")
    private String ultramsgToken;

    @Value("${openai.chatgpt.enabled:false}")
    private Boolean openAiChatGptEnabled;

    @Value("${openai.api-key:}")
    private String openAiApiKey;

    @Value("${openai.model:gpt-4.1-mini}")
    private String openAiModel;

    public List<ClothingOrder> getClothingOrders() {
        return clothingOrderRepository.findAll();
    }

    public void createClothingOrder(ClothingOrder clothingOrder) {
        Customer customer = customerRepository.getCustomerById(clothingOrder.getCustomerId());
        TailorShop tailorShop = tailorShopRepository.getTailorShopById(clothingOrder.getTailorShopId());

        if (customer == null) {
            throw new ApiException("Customer not found");
        }
        if (tailorShop == null) {
            throw new ApiException("Tailor shop not found");
        }
        if (!tailorShop.getSpecialty().equals("ALL") && !tailorShop.getSpecialty().equals(clothingOrder.getCategory())) {
            throw new ApiException("Tailor shop does not support this order category");
        }
        if (clothingOrder.getDeliveryDate().isBefore(clothingOrder.getOrderDate())) {
            throw new ApiException("Delivery date must be after order date");
        }
        if (!clothingOrder.getStatus().equals("PENDING")) {
            throw new ApiException("New order status must be PENDING");
        }
        if (clothingOrder.getPrice() != null) {
            throw new ApiException("Customer cannot set the price when creating an order");
        }

        clothingOrderRepository.save(clothingOrder);
        sendOrderCreatedNotification(customer, clothingOrder);
    }

    public void updateClothingOrder(Integer id, ClothingOrder clothingOrder) {
        ClothingOrder oldClothingOrder = clothingOrderRepository.getClothingOrderById(id);

        if (oldClothingOrder == null) {
            throw new ApiException("Clothing order not found");
        }
        if (customerRepository.getCustomerById(clothingOrder.getCustomerId()) == null) {
            throw new ApiException("Customer not found");
        }

        TailorShop tailorShop = tailorShopRepository.getTailorShopById(clothingOrder.getTailorShopId());

        if (tailorShop == null) {
            throw new ApiException("Tailor shop not found");
        }
        if (!tailorShop.getSpecialty().equals("ALL") && !tailorShop.getSpecialty().equals(clothingOrder.getCategory())) {
            throw new ApiException("Tailor shop does not support this order category");
        }
        if (clothingOrder.getDeliveryDate().isBefore(clothingOrder.getOrderDate())) {
            throw new ApiException("Delivery date must be after order date");
        }
        if (clothingOrder.getPrice() != null && clothingOrder.getPrice() <= 0) {
            throw new ApiException("Price must be positive");
        }

        oldClothingOrder.setCustomerId(clothingOrder.getCustomerId());
        oldClothingOrder.setTailorShopId(clothingOrder.getTailorShopId());
        oldClothingOrder.setCategory(clothingOrder.getCategory());
        oldClothingOrder.setFabricType(clothingOrder.getFabricType());
        oldClothingOrder.setColor(clothingOrder.getColor());
        oldClothingOrder.setPrice(clothingOrder.getPrice());
        oldClothingOrder.setStatus(clothingOrder.getStatus());
        oldClothingOrder.setOrderDate(clothingOrder.getOrderDate());
        oldClothingOrder.setDeliveryDate(clothingOrder.getDeliveryDate());

        clothingOrderRepository.save(oldClothingOrder);
    }

    public void deleteClothingOrder(Integer id) {
        ClothingOrder selectedClothingOrder = clothingOrderRepository.getClothingOrderById(id);

        if (selectedClothingOrder == null) {
            throw new ApiException("Clothing order not found");
        }

        clothingOrderRepository.delete(selectedClothingOrder);
    }

    public void changeOrderStatus(Integer orderId, String status) {
        ClothingOrder clothingOrder = clothingOrderRepository.getClothingOrderById(orderId);

        if (clothingOrder == null) {
            throw new ApiException("Clothing order not found");
        }
        if (!status.matches("^(PENDING|QUOTED|ACCEPTED|IN_PROGRESS|READY|DELIVERED|CANCELLED|REJECTED)$")) {
            throw new ApiException("Status must be PENDING, QUOTED, ACCEPTED, IN_PROGRESS, READY, DELIVERED, CANCELLED, or REJECTED");
        }
        if (status.equals("QUOTED")) {
            throw new ApiException("Use the quote endpoint to send a price quote");
        }
        if (status.equals("ACCEPTED")) {
            throw new ApiException("Customer must accept the quote using the accept quote endpoint");
        }
        if (status.equals("REJECTED")) {
            throw new ApiException("Customer must reject the quote using the reject quote endpoint");
        }
        if ((status.equals("IN_PROGRESS") || status.equals("READY") || status.equals("DELIVERED")) &&
                !(clothingOrder.getStatus().equals("ACCEPTED") || clothingOrder.getStatus().equals("IN_PROGRESS") || clothingOrder.getStatus().equals("READY"))) {
            throw new ApiException("Customer must accept the price quote before the order moves forward");
        }

        clothingOrder.setStatus(status);
        clothingOrderRepository.save(clothingOrder);

        Customer customer = customerRepository.getCustomerById(clothingOrder.getCustomerId());
        sendOrderStatusChangedNotification(customer, clothingOrder);
    }

    public void setOrderPrice(Integer orderId, Double price) {
        ClothingOrder clothingOrder = clothingOrderRepository.getClothingOrderById(orderId);

        if (clothingOrder == null) {
            throw new ApiException("Clothing order not found");
        }
        if (price == null || price <= 0) {
            throw new ApiException("Price must be positive");
        }
        if (!clothingOrder.getStatus().equals("PENDING")) {
            throw new ApiException("Only pending orders can receive a price quote");
        }

        clothingOrder.setPrice(price);
        clothingOrder.setStatus("QUOTED");
        clothingOrderRepository.save(clothingOrder);

        Customer customer = customerRepository.getCustomerById(clothingOrder.getCustomerId());
        sendPriceQuoteNotification(customer, clothingOrder);
    }

    public void acceptPriceQuote(Integer orderId) {
        ClothingOrder clothingOrder = clothingOrderRepository.getClothingOrderById(orderId);

        if (clothingOrder == null) {
            throw new ApiException("Clothing order not found");
        }
        if (!clothingOrder.getStatus().equals("QUOTED")) {
            throw new ApiException("Only quoted orders can be accepted");
        }

        clothingOrder.setStatus("ACCEPTED");
        clothingOrderRepository.save(clothingOrder);

        Customer customer = customerRepository.getCustomerById(clothingOrder.getCustomerId());
        sendOrderStatusChangedNotification(customer, clothingOrder);
    }

    public void rejectPriceQuote(Integer orderId) {
        ClothingOrder clothingOrder = clothingOrderRepository.getClothingOrderById(orderId);

        if (clothingOrder == null) {
            throw new ApiException("Clothing order not found");
        }
        if (!clothingOrder.getStatus().equals("QUOTED")) {
            throw new ApiException("Only quoted orders can be rejected");
        }

        clothingOrder.setStatus("REJECTED");
        clothingOrderRepository.save(clothingOrder);

        Customer customer = customerRepository.getCustomerById(clothingOrder.getCustomerId());
        sendOrderStatusChangedNotification(customer, clothingOrder);
    }

    public List<ClothingOrder> getOrdersByCustomerId(Integer customerId) {
        if (customerRepository.getCustomerById(customerId) == null) {
            throw new ApiException("Customer not found");
        }

        return clothingOrderRepository.findClothingOrdersByCustomerId(customerId);
    }

    public List<ClothingOrder> getOrdersByTailorShopId(Integer tailorShopId) {
        if (tailorShopRepository.getTailorShopById(tailorShopId) == null) {
            throw new ApiException("Tailor shop not found");
        }

        return clothingOrderRepository.findClothingOrdersByTailorShopId(tailorShopId);
    }

    public List<ClothingOrder> getOrdersByCategory(String category) {
        if (!category.matches("^(THOBE|ABAYA|DRESS|UNIFORM)$")) {
            throw new ApiException("Category must be THOBE, ABAYA, DRESS, or UNIFORM");
        }

        return clothingOrderRepository.findClothingOrdersByCategory(category);
    }

    public List<ClothingOrder> getOrdersByStatus(String status) {
        if (!status.matches("^(PENDING|QUOTED|ACCEPTED|IN_PROGRESS|READY|DELIVERED|CANCELLED|REJECTED)$")) {
            throw new ApiException("Status must be PENDING, QUOTED, ACCEPTED, IN_PROGRESS, READY, DELIVERED, CANCELLED, or REJECTED");
        }

        return clothingOrderRepository.findClothingOrdersByStatus(status);
    }

    public String getClothingSuggestions(String category) {
        validateCategory(category);

        List<ClothingOrder> orders = clothingOrderRepository.findAll();

        if (orders.isEmpty()) {
            throw new ApiException("No orders available for suggestions");
        }

        LocalDate now = LocalDate.now();

        List<ClothingOrder> currentMonthOrders = orders.stream()
                .filter(order -> order.getCategory().equals(category))
                .filter(order -> order.getOrderDate().getMonth() == now.getMonth())
                .filter(order -> order.getOrderDate().getYear() == now.getYear())
                .toList();

        List<ClothingOrder> categoryOrders = orders.stream()
                .filter(order -> order.getCategory().equals(category))
                .toList();

        List<ClothingOrder> ordersForSuggestion = currentMonthOrders.isEmpty() ? categoryOrders : currentMonthOrders;

        if (ordersForSuggestion.isEmpty()) {
            throw new ApiException("No previous orders found for this category");
        }

        String localAnalysis = getLocalClothingSuggestions(category, ordersForSuggestion, !currentMonthOrders.isEmpty());

        if (!openAiChatGptEnabled) {
            return localAnalysis;
        }

        if (openAiApiKey == null || openAiApiKey.isEmpty()) {
            return localAnalysis + " ChatGPT integration is not configured.";
        }

        try {
            return askChatGptForClothingSuggestions(category, ordersForSuggestion, !currentMonthOrders.isEmpty());
        } catch (Exception e) {
            return localAnalysis + " ChatGPT request failed, so local suggestions were returned.";
        }
    }

    private void validateCategory(String category) {
        if (!category.matches("^(THOBE|ABAYA|DRESS|UNIFORM)$")) {
            throw new ApiException("Category must be THOBE, ABAYA, DRESS, or UNIFORM");
        }
    }

    private String getLocalClothingSuggestions(String category, List<ClothingOrder> orders, Boolean currentMonthData) {
        String colors = getTopValues(orders.stream()
                .collect(Collectors.groupingBy(ClothingOrder::getColor, Collectors.counting())));

        String fabrics = getTopValues(orders.stream()
                .collect(Collectors.groupingBy(ClothingOrder::getFabricType, Collectors.counting())));

        String combinations = getTopValues(orders.stream()
                .collect(Collectors.groupingBy(order -> order.getColor() + " with " + order.getFabricType(), Collectors.counting())));

        String source = currentMonthData ? "current month orders" : "previous orders";

        return "Based on " + source + ", customers choosing " + category +
                " are trending toward these colors: " + colors +
                ". Popular fabric types are: " + fabrics +
                ". Suggested combinations: " + combinations + ".";
    }

    private String askChatGptForClothingSuggestions(String category, List<ClothingOrder> orders, Boolean currentMonthData) throws Exception {
        String prompt = buildChatGptPrompt(category, orders, currentMonthData);
        String jsonBody = "{"
                + "\"model\":\"" + escapeJson(openAiModel) + "\","
                + "\"max_tokens\":300,"
                + "\"messages\":["
                + "{\"role\":\"system\",\"content\":\"" + escapeJson("You are a helpful clothing style assistant for customers using a tailoring platform.") + "\"},"
                + "{\"role\":\"user\",\"content\":\"" + escapeJson(prompt) + "\"}"
                + "]"
                + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + openAiApiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new ApiException("ChatGPT request failed");
        }

        return extractChatGptText(response.body());
    }

    private String buildChatGptPrompt(String category, List<ClothingOrder> orders, Boolean currentMonthData) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("A customer is unsure what options to select for a custom ")
                .append(category)
                .append(" order in Maqas. ");
        prompt.append("Use these ")
                .append(currentMonthData ? "current month" : "previous")
                .append(" orders to suggest trending colors, fabric types, and popular combinations. ");
        prompt.append("Do not talk like an admin report. Talk directly to the customer. ");
        prompt.append("Keep it friendly, practical, and under 5 sentences. Orders: ");

        for (ClothingOrder order : orders) {
            prompt.append("Order ")
                    .append(order.getId())
                    .append(": category=")
                    .append(order.getCategory())
                    .append(", color=")
                    .append(order.getColor())
                    .append(", fabric=")
                    .append(order.getFabricType())
                    .append(", date=")
                    .append(order.getOrderDate())
                    .append("; ");
        }

        return prompt.toString();
    }

    private String extractChatGptText(String responseBody) {
        String marker = "\"content\"";
        int start = responseBody.indexOf(marker);

        if (start == -1) {
            return responseBody;
        }

        start = responseBody.indexOf(":", start);

        if (start == -1) {
            return responseBody;
        }

        start = responseBody.indexOf("\"", start);

        if (start == -1) {
            return responseBody;
        }

        start++;
        StringBuilder text = new StringBuilder();
        boolean escaping = false;

        for (int i = start; i < responseBody.length(); i++) {
            char current = responseBody.charAt(i);

            if (escaping) {
                if (current == 'n') {
                    text.append('\n');
                } else if (current == 't') {
                    text.append('\t');
                } else {
                    text.append(current);
                }
                escaping = false;
            } else if (current == '\\') {
                escaping = true;
            } else if (current == '"') {
                break;
            } else {
                text.append(current);
            }
        }

        return text.toString();
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String getTopValues(Map<String, Long> values) {
        return values.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(", "));
    }

    private void sendOrderCreatedNotification(Customer customer, ClothingOrder clothingOrder) {
        String message = "Hello " + customer.getName() + ", your Maqas order #" + clothingOrder.getId() + " has been created with status " + clothingOrder.getStatus();
        try {
            sendEmail(customer.getEmail(), message);
        } catch (ApiException e) {
            System.out.println(e.getMessage());
        }
        try {
            sendWhatsApp(customer.getPhoneNumber(), message);
        } catch (ApiException e) {
            System.out.println(e.getMessage());
        }
    }

    private void sendOrderStatusChangedNotification(Customer customer, ClothingOrder clothingOrder) {
        String message = "Hello " + customer.getName() + ", your Maqas order #" + clothingOrder.getId() + " status changed to " + clothingOrder.getStatus();
        try {
            sendEmail(customer.getEmail(), message);
        } catch (ApiException e) {
            System.out.println(e.getMessage());
        }
        try {
            sendWhatsApp(customer.getPhoneNumber(), message);
        } catch (ApiException e) {
            System.out.println(e.getMessage());
        }
    }

    private void sendPriceQuoteNotification(Customer customer, ClothingOrder clothingOrder) {
        String message = "Hello " + customer.getName() + ", your Maqas order #" + clothingOrder.getId() + " has a new price quote: " + clothingOrder.getPrice() + " SAR. Please accept or reject the quote.";
        try {
            sendEmail(customer.getEmail(), message);
        } catch (ApiException e) {
            System.out.println(e.getMessage());
        }
        try {
            sendWhatsApp(customer.getPhoneNumber(), message);
        } catch (ApiException e) {
            System.out.println(e.getMessage());
        }
    }

    private void sendEmail(String email, String message) {
        if (!emailEnabled) {
            System.out.println("Email notification sent to " + email + ": " + message);
            return;
        }

        JavaMailSender javaMailSender = javaMailSenderProvider.getIfAvailable();

        if (javaMailSender == null || emailFrom == null || emailFrom.isEmpty()) {
            throw new ApiException("Email integration is not configured");
        }

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(emailFrom);
            mailMessage.setTo(email);
            mailMessage.setSubject("Maqas order update");
            mailMessage.setText(message);
            javaMailSender.send(mailMessage);
        } catch (Exception e) {
            throw new ApiException("Email notification failed");
        }
    }

    private void sendWhatsApp(String phoneNumber, String message) {
        if (!whatsappEnabled) {
            System.out.println("WhatsApp notification sent to " + phoneNumber + ": " + message);
            return;
        }

        if (ultramsgInstanceId == null || ultramsgInstanceId.isEmpty() ||
                ultramsgToken == null || ultramsgToken.isEmpty()) {
            throw new ApiException("WhatsApp integration is not configured");
        }

        try {
            String body = "token=" + encode(ultramsgToken) +
                    "&to=" + encode(convertSaudiPhoneNumber(phoneNumber)) +
                    "&body=" + encode(message);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.ultramsg.com/" + ultramsgInstanceId + "/messages/chat"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ApiException("WhatsApp notification failed");
            }
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("WhatsApp notification failed");
        }
    }

    private String convertSaudiPhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("05")) {
            return "+966" + phoneNumber.substring(1);
        }

        return phoneNumber;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
