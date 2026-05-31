package com.example.maqas.Service;

import com.example.maqas.Api.ApiException;
import com.example.maqas.Model.Customer;
import com.example.maqas.Model.MeasurementRequest;
import com.example.maqas.Model.ShopOwner;
import com.example.maqas.Model.TailorShop;
import com.example.maqas.Repository.CustomerRepository;
import com.example.maqas.Repository.MeasurementRequestRepository;
import com.example.maqas.Repository.ShopOwnerRepository;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeasurementRequestService {

    private final MeasurementRequestRepository measurementRequestRepository;
    private final CustomerRepository customerRepository;
    private final TailorShopRepository tailorShopRepository;
    private final ShopOwnerRepository shopOwnerRepository;
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

    public List<MeasurementRequest> getMeasurementRequests() {
        return measurementRequestRepository.findAll();
    }

    public void addMeasurementRequest(Integer customerId, MeasurementRequest measurementRequest) {
        Customer customer = customerRepository.getCustomerById(customerId);
        if (customer == null) {
            throw new ApiException("Customer not found");
        }

        TailorShop tailorShop = tailorShopRepository.getTailorShopById(measurementRequest.getTailorShopId());
        if (tailorShop == null) {
            throw new ApiException("Tailor shop not found");
        }
        if (tailorShop.getOffersHomeMeasurement().equals(false)) {
            throw new ApiException("Tailor shop does not offer home measurement service");
        }

        measurementRequest.setCustomerId(customerId);
        measurementRequest.setStatus("PENDING");
        measurementRequestRepository.save(measurementRequest);
        sendMeasurementRequestNotification(customer, tailorShop, measurementRequest);
    }

    public void updateMeasurementRequest(Integer customerId, Integer id, MeasurementRequest measurementRequest) {
        MeasurementRequest oldMeasurementRequest = measurementRequestRepository.getMeasurementRequestById(id);
        if (oldMeasurementRequest == null) {
            throw new ApiException("Measurement request not found");
        }
        if (customerRepository.getCustomerById(customerId) == null) {
            throw new ApiException("Customer not found");
        }

        TailorShop tailorShop = tailorShopRepository.getTailorShopById(measurementRequest.getTailorShopId());
        if (tailorShop == null) {
            throw new ApiException("Tailor shop not found");
        }
        if (tailorShop.getOffersHomeMeasurement().equals(false)) {
            throw new ApiException("Tailor shop does not offer home measurement service");
        }

        oldMeasurementRequest.setCustomerId(customerId);
        oldMeasurementRequest.setTailorShopId(measurementRequest.getTailorShopId());
        oldMeasurementRequest.setCity(measurementRequest.getCity());
        oldMeasurementRequest.setAddress(measurementRequest.getAddress());
        oldMeasurementRequest.setPreferredDate(measurementRequest.getPreferredDate());

        measurementRequestRepository.save(oldMeasurementRequest);
    }

    public void deleteMeasurementRequest(Integer id) {
        MeasurementRequest selectedMeasurementRequest = measurementRequestRepository.getMeasurementRequestById(id);
        if (selectedMeasurementRequest == null) {
            throw new ApiException("Measurement request not found");
        }
        measurementRequestRepository.delete(selectedMeasurementRequest);
    }

    public void changeMeasurementRequestStatus(Integer id, String status) {
        MeasurementRequest measurementRequest = measurementRequestRepository.getMeasurementRequestById(id);
        if (measurementRequest == null) {
            throw new ApiException("Measurement request not found");
        }
        if (!status.matches("^(PENDING|ACCEPTED|COMPLETED|CANCELLED)$")) {
            throw new ApiException("Status must be PENDING, ACCEPTED, COMPLETED, or CANCELLED");
        }

        measurementRequest.setStatus(status);
        measurementRequestRepository.save(measurementRequest);
    }

    public List<MeasurementRequest> getRequestsByCustomerId(Integer customerId) {
        if (customerRepository.getCustomerById(customerId) == null) {
            throw new ApiException("Customer not found");
        }
        return measurementRequestRepository.findMeasurementRequestsByCustomerId(customerId);
    }

    public List<MeasurementRequest> getRequestsByTailorShopId(Integer tailorShopId) {
        if (tailorShopRepository.getTailorShopById(tailorShopId) == null) {
            throw new ApiException("Tailor shop not found");
        }
        return measurementRequestRepository.findMeasurementRequestsByTailorShopId(tailorShopId);
    }

    public List<MeasurementRequest> getRequestsByStatus(String status) {
        if (!status.matches("^(PENDING|ACCEPTED|COMPLETED|CANCELLED)$")) {
            throw new ApiException("Status must be PENDING, ACCEPTED, COMPLETED, or CANCELLED");
        }
        return measurementRequestRepository.findMeasurementRequestsByStatus(status);
    }

    private void sendMeasurementRequestNotification(Customer customer, TailorShop tailorShop, MeasurementRequest measurementRequest) {
        ShopOwner shopOwner = shopOwnerRepository.getShopOwnerById(tailorShop.getOwnerId());
        if (shopOwner == null) return;

        String message = "New home measurement request from " + customer.getName() +
                " (request #" + measurementRequest.getId() + ")" +
                " for " + measurementRequest.getPreferredDate() +
                " at " + measurementRequest.getAddress() + ", " + measurementRequest.getCity() + ".";

        try { sendEmail(shopOwner.getEmail(), message); } catch (ApiException e) { System.out.println(e.getMessage()); }
        try { sendWhatsApp(shopOwner.getPhoneNumber(), message); } catch (ApiException e) { System.out.println(e.getMessage()); }
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
            mailMessage.setSubject("Maqas measurement request");
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
