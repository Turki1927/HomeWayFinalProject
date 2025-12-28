package com.example.homeway.AIService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiKey;
    public AIService(@Value("${openai.api.key}") String apiKey) {
        this.apiKey = apiKey;
    }

    private String askChat(String prompt) {
        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4o-mini");

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", prompt);
        messages.add(userMsg);

        body.put("messages", messages);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, requestEntity, Map.class);

        Map responseBody = responseEntity.getBody();
        if (responseBody == null) {
            return "AI did not return a response.";
        }

        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
        if (choices == null || choices.isEmpty()) {
            return "AI returned no choices.";
        }

        Map<String, Object> firstChoice = choices.get(0);
        Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
        if (message == null) {
            return "AI returned an empty message.";
        }

        Object content = message.get("content");
        return content != null ? content.toString() : "AI returned no content.";
    }

    //template
    public String methodName(String input) {
        String prompt = """
                You are an ... assistant.
                Explain in clear, simple steps how to ...  and list some simple resources, be short 5-8 sentences:

                input: %s

                Mention:
                - 1
                - 2
                - 3
                - 4
                
                """.formatted(input);

        return askChat(prompt);
    }

    //Osama
    public String CustomerInspectionCostEstimation(String input) {
        String prompt = """
                You are an ... assistant.
                Explain in clear, simple steps how to ...  and list some simple resources, be short 5-8 sentences:

                input: %s

                Mention:
                - 1
                - 2
                - 3
                - 4
                
                """.formatted(input);

        return askChat(prompt);
    }

    public String CustomerReportSummary(String input) {
        String prompt = """
                You are an ... assistant.
                Explain in clear, simple steps how to ...  and list some simple resources, be short 5-8 sentences:

                input: %s

                Mention:
                - 1
                - 2
                - 3
                - 4
                
                """.formatted(input);

        return askChat(prompt);
    }

    public String WorkerIssueDiagnosis(String input) {
        String prompt = """
                You are an ... assistant.
                Explain in clear, simple steps how to ...  and list some simple resources, be short 5-8 sentences:

                input: %s

                Mention:
                - 1
                - 2
                - 3
                - 4
                
                """.formatted(input);

        return askChat(prompt);
    }

    public String MovingCompanyMovingEstimation(String input) {
        String prompt = """
                You are an ... assistant.
                Explain in clear, simple steps how to ...  and list some simple resources, be short 5-8 sentences:

                input: %s

                Mention:
                - 1
                - 2
                - 3
                - 4
                
                """.formatted(input);

        return askChat(prompt);
    }

    public String WorkerJobTimeEstimation(String input) {
        String prompt = """
                You are an ... assistant.
                Explain in clear, simple steps how to ...  and list some simple resources, be short 5-8 sentences:

                input: %s

                Mention:
                - 1
                - 2
                - 3
                - 4
                
                """.formatted(input);

        return askChat(prompt);
    }

    public String MaintenanceCompanyMaintenancePlan(String input) {
        String prompt = """
                You are an ... assistant.
                Explain in clear, simple steps how to ...  and list some simple resources, be short 5-8 sentences:

                input: %s

                Mention:
                - 1
                - 2
                - 3
                - 4
                
                """.formatted(input);

        return askChat(prompt);
    }

    public String RedesignCompanyRedesignScope(String input) {
        String prompt = """
                You are an ... assistant.
                Explain in clear, simple steps how to ...  and list some simple resources, be short 5-8 sentences:

                input: %s

                Mention:
                - 1
                - 2
                - 3
                - 4
                
                """.formatted(input);

        return askChat(prompt);
    }

    //turki
    public String CustomerServicesTimeEstimation(String input) {
        String prompt = """
                You are an ... assistant.
                Explain in clear, simple steps how to ...  and list some simple resources, be short 5-8 sentences:

                input: %s

                Mention:
                - 1
                - 2
                - 3
                - 4
                
                """.formatted(input);

        return askChat(prompt);
    }

    public String CustomerReviewWritingAssist(String input) {
        String prompt = """
                You are an ... assistant.
                Explain in clear, simple steps how to ...  and list some simple resources, be short 5-8 sentences:

                input: %s

                Mention:
                - 1
                - 2
                - 3
                - 4
                
                """.formatted(input);

        return askChat(prompt);
    }


    public String WorkerRepairChecklist(String input) {
        String prompt = """
                You are an ... assistant.
                Explain in clear, simple steps how to ...  and list some simple resources, be short 5-8 sentences:

                input: %s

                Mention:
                - 1
                - 2
                - 3
                - 4
                
                """.formatted(input);

        return askChat(prompt);
    }

    public String WorkerSafetyRequirements(String input) {
        String prompt = """
                You are an ... assistant.
                Explain in clear, simple steps how to ...  and list some simple resources, be short 5-8 sentences:

                input: %s

                Mention:
                - 1
                - 2
                - 3
                - 4
                
                """.formatted(input);

        return askChat(prompt);
    }

    public String CompanyServiceEstimationCost(String input) {
        String prompt = """
                You are an ... assistant.
                Explain in clear, simple steps how to ...  and list some simple resources, be short 5-8 sentences:

                input: %s

                Mention:
                - 1
                - 2
                - 3
                - 4
                
                """.formatted(input);

        return askChat(prompt);
    }

    public String MaintenanceCompanySparePartsCosts(String input) {
        String prompt = """
                You are an ... assistant.
                Explain in clear, simple steps how to ...  and list some simple resources, be short 5-8 sentences:

                input: %s

                Mention:
                - 1
                - 2
                - 3
                - 4
                
                """.formatted(input);

        return askChat(prompt);
    }

    //leen
    public String CustomerAskAIWhatServiceDoesTheIssueFits(String input) {
        String prompt = """
                You are an ... assistant.
                Explain in clear, simple steps how to ...  and list some simple resources, be short 5-8 sentences:

                input: %s

                Mention:
                - 1
                - 2
                - 3
                - 4
                
                """.formatted(input);

        return askChat(prompt);
    }

    public String CustomerIsFixOrDesignCheaper(String input) {
        String prompt = """
                You are an ... assistant.
                Explain in clear, simple steps how to ...  and list some simple resources, be short 5-8 sentences:

                input: %s

                Mention:
                - 1
                - 2
                - 3
                - 4
                
                """.formatted(input);

        return askChat(prompt);
    }

    public String WorkerReportCreationAssistent(String input) {
        String prompt = """
                You are an ... assistant.
                Explain in clear, simple steps how to ...  and list some simple resources, be short 5-8 sentences:

                input: %s

                Mention:
                - 1
                - 2
                - 3
                - 4
                
                """.formatted(input);

        return askChat(prompt);
    }

    public String CompanyInspectionPlanningAssistent(String input) {
        String prompt = """
                You are an ... assistant.
                Explain in clear, simple steps how to ...  and list some simple resources, be short 5-8 sentences:

                input: %s

                Mention:
                - 1
                - 2
                - 3
                - 4
                
                """.formatted(input);

        return askChat(prompt);
    }


    public String MovingCompanyTimeAdvice(String CityAndTime) {
        String prompt = """
                You are an ... assistant.
                Explain in clear, simple steps how to ...  and list some simple resources, be short 5-8 sentences:

                input: %s

                Mention:
                - 1
                - 2
                - 3
                - 4
                
                """.formatted(CityAndTime);

        return askChat(prompt);
    }

    public String MaintenanceFixOrReplace(String input) {
        String prompt = """
                You are an ... assistant.
                Explain in clear, simple steps how to ...  and list some simple resources, be short 5-8 sentences:

                input: %s

                Mention:
                - 1
                - 2
                - 3
                - 4
                
                """.formatted(input);

        return askChat(prompt);
    }
}
