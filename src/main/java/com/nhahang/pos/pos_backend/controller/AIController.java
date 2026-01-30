package com.nhahang.pos.pos_backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*") // Cho phép mọi nguồn gọi vào để tránh lỗi CORS
public class AIController {

    @Value("${gemini.api.key}")
    private String apiKey;

    @GetMapping("/chat")
    public ResponseEntity<?> chatWithAI(@RequestParam String message) {
        try {
            // 1. Kiểm tra xem Key đã được nạp chưa (Xem ở Console Java)
            System.out.println("--- ĐANG GỌI AI ---");
            System.out.println("API Key: " + (apiKey != null ? "Đã nạp" : "CHƯA CÓ KEY!"));
            System.out.println("Tin nhắn khách: " + message);

            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key="
                    + apiKey;

            // 2. Tạo cấu trúc dữ liệu chuẩn (Tương thích Java 8 trở lên)
            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", "Bạn là nhân viên nhà hàng Food Paradise. Trả lời ngắn gọn câu này: " + message);

            Map<String, Object> parts = new HashMap<>();
            parts.put("parts", Arrays.asList(textPart));

            Map<String, Object> body = new HashMap<>();
            body.put("contents", Arrays.asList(parts));

            // 3. Gửi yêu cầu
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(url, entity, JsonNode.class);

            // 4. Trích xuất kết quả
            String aiText = response.getBody()
                    .path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            System.out.println("AI trả lời: " + aiText);
            return ResponseEntity.ok(aiText);

        } catch (Exception e) {
            // In lỗi chi tiết ra màn hình Console của IntelliJ/VS Code
            System.err.println("LỖI BACKEND: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi hệ thống: " + e.getMessage());
        }
    }
}