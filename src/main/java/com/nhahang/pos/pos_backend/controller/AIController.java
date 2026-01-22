package com.nhahang.pos.pos_backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:5173")
public class AIController {

    // Lấy Key từ file application.properties
    @Value("${gemini.api.key}")
    private String apiKey;

    @GetMapping("/suggest-recipe")
    public ResponseEntity<?> suggestRecipe(@RequestParam String dishName) {
        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key="
                    + apiKey;

            String prompt = "Hãy liệt kê các nguyên liệu để nấu món '" + dishName + "'. "
                    + "Trả về kết quả chỉ là một JSON Array thuần túy, không có markdown, không có ```json. "
                    + "Cấu trúc: [{\"name\": \"tên nguyên liệu\", \"quantity\": số lượng (số thực), \"unit\": \"đơn vị (kg, g, lít, quả...)\"}]. "
                    + "Ví dụ: [{\"name\": \"Thịt bò\", \"quantity\": 0.5, \"unit\": \"kg\"}]";

            String jsonBody = "{ \"contents\": [{ \"parts\": [{ \"text\": \"" + prompt + "\" }] }] }";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            String text = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

            text = text.replace("```json", "").replace("```", "").trim();
            int startIndex = text.indexOf("[");
            int endIndex = text.lastIndexOf("]");
            if (startIndex != -1 && endIndex != -1) {
                text = text.substring(startIndex, endIndex + 1);
            }

            List<Map<String, Object>> ingredients = mapper.readValue(text,
                    new TypeReference<List<Map<String, Object>>>() {
                    });
            return ResponseEntity.ok(ingredients);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Lỗi AI: " + e.getMessage()));
        }
    }

    @GetMapping("/chat")
    public ResponseEntity<?> chatWithAI(@RequestParam String message) {
        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key="
                    + apiKey;

            String systemInstruction = "Bạn là nhân viên phục vụ ảo của nhà hàng 'Food Paradise'. "
                    + "Phong cách: Thân thiện, hài hước, dùng nhiều emoji. "
                    + "Thông tin quán: Mở cửa 8h-22h. Địa chỉ: 123 Quận 1, TP.HCM. "
                    + "Menu nổi bật: Cơm tấm, Phở bò, Trà sữa. "
                    + "Nhiệm vụ: Trả lời câu hỏi của khách hàng ngắn gọn (dưới 100 từ). "
                    + "Câu hỏi của khách: " + message;

            String jsonBody = "{ \"contents\": [{ \"parts\": [{ \"text\": \"" + systemInstruction + "\" }] }] }";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            String text = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

            return ResponseEntity.ok(text);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("AI đang bận: " + e.getMessage());
        }
    }
}