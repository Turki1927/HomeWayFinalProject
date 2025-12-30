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

    //O
    public String customerRequestCostEstimation(String input) {
        String prompt = """
                You are a home services pricing assistant.
                Read the customer's request description and FIRST classify it as ONE of:
                (MOVING / INSPECTION / MAINTENANCE / REDESIGN).
                Then provide a cost estimate ONLY for that service type.

                Be clear, not too long (6–10 sentences total).
                Use Saudi Riyal (SAR). If location isn't provided, say "location affects price".

                Output format:
                - Service type detected:
                - Estimated total cost range (SAR):
                - Main factors affecting price (5 bullets)
                - Tips to reduce cost (3 bullets)
                - Questions to confirm for better estimate (3 short questions)

                Customer request description:
                %s
                """.formatted(input);

        return askChat(prompt);
    }

    public String customerReportSummary(String input) {
        String prompt = """
                You are a home inspection report explainer for customers (non-technical).
                Explain the report in simple language.

                Output rules:
                - Keep it short: 8–12 lines
                - Use headings exactly:
                  1) Summary
                  2) Critical issues
                  3) Non-critical issues
                  4) Terms explained
                - "Critical issues" should list only items that affect safety/structure/major cost.
                - "Terms explained" should explain up to 4 technical terms simply.

                Report text:
                %s
                """.formatted(input);

        return askChat(prompt);
    }

    public String workerIssueDiagnosis(String input) {
        String prompt = """
                You are a field technician assistant.
                Given symptoms/issue description, suggest possible causes and what to check first.
                Be practical and step-by-step. Keep it short (5–8 sections max).

                Output format:
                1) Most likely causes (top 3)
                2) What to check first (step-by-step checklist)
                3) Common mistakes to avoid (3 bullets)
                4) When to escalate / call specialist (2 bullets)
                5) Safety warning (1 sentence)

                Symptoms / issue description:
                %s
                """.formatted(input);

        return askChat(prompt);
    }

    public String movingCompanyResourceMovingEstimation(String input) {
        String prompt = """
                You are a moving operations planner.
                Based on the move description, estimate ONLY:
                - crew size
                - truck size
                - packing materials recommendations
                - handling notes (fragile/heavy items)
                DO NOT mention time or duration.

                Output format:
                1) Crew size recommendation + why
                2) Truck size recommendation (e.g., small van / 3-ton / 5-ton / 10-ton) + why
                3) Packing materials list (bullets)
                4) Special handling notes (bullets)
                5) Missing details to confirm (3 questions)

                Moving request description:
                %s
                """.formatted(input);

        return askChat(prompt);
    }

    public String workerJobTimeEstimation(String input) {
        String prompt = """
                You are a job duration estimator for field work.
                Estimate the time needed to complete the described job.

                Output format:
                1) Base duration estimate (range)
                2) Extra buffer time (range) and why
                3) Complexity level (Low/Medium/High) with 1 sentence
                4) Key steps that consume time (bullets)
                5) What could cause delays (3 bullets)

                Job description:
                %s
                """.formatted(input);

        return askChat(prompt);
    }

    public String maintenanceCompanyMaintenancePlan(String input) {
        String prompt = """
                You are a maintenance planner.
                Create a clear maintenance plan based on the request description.
                The plan should be actionable for a worker team.

                Output format:
                1) Objective (1 sentence)
                2) Step-by-step plan (6–10 steps)
                3) Tools/materials needed (bullets)
                4) Risks & precautions (bullets)
                5) Quality checklist before marking complete (bullets)

                Request description:
                %s
                """.formatted(input);

        return askChat(prompt);
    }

    public String redesignCompanyRedesignScope(String input) {
        String prompt = """
                You are an interior/home redesign scope assistant.
                Based on the customer's goals, propose a redesign scope and style suggestions.
                Keep it practical and structured.

                Output format:
                1) Design goals understood (2–4 bullets)
                2) Proposed scope (bullets: areas/rooms/features)
                3) Style directions (3 options with short description each)
                4) Materials/finishes ideas (bullets)
                5) Questions to confirm (3 questions)

                Customer goals / redesign request description:
                %s
                """.formatted(input);

        return askChat(prompt);
    }

    //t
    public String customerServicesTimeEstimation(String input) {
        String prompt = """
                         You are a home-services timeline estimation assistant for a property platform.
                         The user will provide a description of an issue in a home/property.
                
                         Your job:
                         - Estimate how long an INSPECTION usually takes
                         - Estimate how long MOVING usually takes
                         - Estimate how long MAINTENANCE usually takes
                         - Estimate how long a REDESIGN might take
                
                         Rules:
                         - Be realistic and conservative.
                         - Use time ranges (e.g., "2–4 hours", "1–3 days", "1–2 weeks").
                         - If details are missing, state assumptions briefly.
                         - Keep the answer short: 5–8 sentences maximum.
                         - careful not to duplicate answers or a category
                         - If the issue is not related to moving do not mention Moving (example: i have a broken pipe, moving isn't relevant)
                         - Do NOT mention pricing or cost.
                         - Do NOT mention policy or system text.
                
                         Input description:
                         %s
                
                         Output format:
                         1) Inspection: ...
                         2) Maintenance: ...
                         3) Maintenance: ...
                         4) Redesign: ...
                         Assumptions: ...
                """.formatted(input);

        return askChat(prompt);
    }

    public String customerReviewWritingAssist(String notes, String tone) {

        // Normalize tone so the prompt stays stable even if user sends weird values
        String safeTone = (tone == null) ? "neutral" : tone.trim().toLowerCase();
        if (!safeTone.equals("polite") && !safeTone.equals("strict") && !safeTone.equals("neutral")) {
            safeTone = "neutral";
        }

        String prompt = """
                You are a customer review writing assistant for a home services platform (inspection, maintenance, moving, redesign).
                
                Task:
                - Convert the customer's rough notes into a clear, fair, and realistic review.
                - Keep it short: 5 to 8 sentences total.
                - The tone MUST be: %s
                
                Rules:
                - Do NOT invent details that are not in the notes.
                - If the notes are too vague, write a neutral review and mention "details were limited".
                - Avoid insults, hate, or unsafe content. Keep it professional.
                
                Output format:
                Title: <short title>
                Review: <5-8 sentences>
                RatingSuggestion: <1-5 number> (estimate based only on the notes)
                
                Customer notes:
                %s
                """.formatted(safeTone, notes);

        return askChat(prompt);
    }


    public String workerRepairChecklist(String input) {

        String prompt = """
            You are a professional maintenance and repair assistant.

            A worker will provide a short description of a repair issue.
            Your task is to generate a clear, practical repair checklist that a field worker can follow.

            Input issue description:
            %s

            Respond with:
            1. A short overview of the issue (1 sentence)
            2. Step-by-step repair checklist (numbered steps)
            3. Required tools and materials (bullet points)
            4. Safety precautions (if applicable)

            Rules:
            - Be clear and practical
            - Do not assume advanced expertise
            - Keep the response between 6–10 short steps
            - Do not mention prices or costs
            - Do not mention AI or disclaimers
            """.formatted(input);

        return askChat(prompt);
    }

    public String workerSafetyRequirements(String input) {

        String prompt = """
            You are a professional safety compliance assistant for maintenance, inspection, moving, and redesign work.

            Based on the task description below, provide:
            - Key safety precautions
            - Required personal protective equipment (PPE)
            - Common safety risks
            - General compliance and best-practice tips used in the industry

            Keep the response clear, practical, and concise (5–8 bullet points).

            Task description:
            %s
            """.formatted(input);

        return askChat(prompt);
    }

    public String companyServiceEstimationCost(String input) {
        String prompt = """
                You are a cost estimation assistant for home services (inspection, maintenance, moving, redesign).
                Based ONLY on the user's description, provide a realistic estimated cost breakdown for the COMPANY.
                
                Rules:
                - Use Saudi Riyal (SAR).
                - If details are missing, state assumptions briefly.
                - Give a RANGE (min-max) not a single number.
                - Keep it concise (8-12 lines).
                - Do NOT promise exact pricing. Mention it depends on site assessment.
                
                Output format:
                1) Quick summary (1-2 lines)
                2) Estimated total range (SAR)
                3) Breakdown (labor, materials, transport, equipment, overhead) with ranges
                4) Price drivers: what increases price (3 bullets)
                5) Price reducers: what decreases price (3 bullets)
                6) Questions to confirm (max 3)
                
                Description:
                %s
                """.formatted(input);

        return askChat(prompt);
    }

    public String maintenanceCompanySparePartsCosts(String input) {
        String prompt = """
                You are a maintenance cost estimator.
                Based ONLY on the issue description, suggest likely spare parts and a realistic cost range.
                Keep it short and practical (5–10 bullet points max). If info is missing, state assumptions.

                Output format:
                1) Quick diagnosis guess (1 sentence)
                2) Likely spare parts list (part + why)
                3) Estimated parts cost range (low–high)
                4) What could increase/decrease parts cost (3 bullets)
                5) Safety note (1 sentence)

                Issue description:
                %s
                """.formatted(input);

        return askChat(prompt);
    }

    //L
    // 1) Before creating request: what service fits?
    public String customerAskAIWhatServiceDoesTheIssueFits(String input) {
        String prompt = """
                You are a home-services triage assistant for a platform that offers:
                - INSPECTION (diagnose, assess safety/structure, document findings)
                - MAINTENANCE (repair/replace/fix functional issues)
                - REDESIGN (cosmetic/layout improvements; optional)
                
                Task:
                Decide what the customer should request FIRST.
                Be short: 5-8 sentences maximum.
                
                Input from customer:
                %s
                
                Rules:
                - If the issue could be structural/safety/hidden damage, recommend INSPECTION first.
                - If it is a clear/simple functional problem (leak, broken outlet, AC not cooling), MAINTENANCE may be enough.
                - REDESIGN is only recommended if the user explicitly wants aesthetics, layout change, or upgrades.
                - If unsure, choose INSPECTION first.
                - Moving isn't included.
                
                Output format:
                1) Recommended service (Inspection / Maintenance / Redesign)
                2) Why (2-3 short reasons)
                3) What the customer should prepare (1-2 items)
                """.formatted(input);

        return askChat(prompt);
    }

    // 2) Cheaper: fix vs redesign
    public String customerIsFixOrDesignCheaper(String input) {
        String prompt = """
                You are a cost-estimation assistant for home issues.
                The customer describes an issue; you must estimate whether it is LIKELY cheaper to FIX (maintenance) or to REDESIGN.
                Be short: 5-8 sentences maximum.
                
                Customer input:
                %s
                
                Rules:
                - If the issue is isolated and functional (single item broken), FIX is likely cheaper.
                - If multiple areas are affected, repeated repairs, or the customer wants upgrades/modern look, REDESIGN may be worth it.
                - Mention uncertainty: "likely" not guaranteed; recommend inspection if hidden damage is possible.
                - Provide 2 cost drivers (what makes it cheaper/more expensive).
                
                Output format:
                - Likely cheaper option: FIX or REDESIGN
                - Why (2 points)
                - Key cost drivers (2 bullets)
                - Quick next step (1 sentence)
                """.formatted(input);

        return askChat(prompt);
    }

    // 3) Worker report writing assistant (bullet points -> professional report)
    public String workerReportCreationAssistant(String input) {
        String prompt = """
                You are a professional report writing assistant for a field worker.
                Convert the input bullet points into a clear professional report.
                Remove ambiguity, improve clarity, but do NOT invent facts.
                Be short and useful.
                
                Worker notes / bullet points:
                %s
                
                Output format:
                Title: (short)
                Summary: (2-3 sentences)
                Findings: (bulleted list)
                Recommendations: (bulleted list)
                Next actions: (1-3 bullets)
                
                Rules:
                - Keep it objective and professional.
                - If something is unclear, add a "Need Clarification" bullet at the end.
                - No extra fluff.
                """.formatted(input);

        return askChat(prompt);
    }

    // 4) Smart inspection planning (checklist + priorities)
    public String companyInspectionPlanningAssistant(String input) {
        String prompt = """
                You are an inspection planning assistant.
                The inspector provides a request/issue description.
                Create an inspection checklist and prioritize the highest risk areas first.
                Be short: 5-8 sentences maximum + checklist.
                
                Input:
                %s
                
                Mention:
                - Safety risks to check first
                - Tools or measurements needed (basic)
                - Checklist grouped by priority (High / Medium / Low)
                - What evidence to capture (photos/notes)
                
                Rules:
                - If structural/electrical/gas/water risk appears, prioritize it as HIGH.
                - Do not claim certainty; phrase as "check/verify".
                
                Output format:
                Priority checks:
                HIGH:
                - ...
                MEDIUM:
                - ...
                LOW:
                - ...
                Evidence to capture:
                - ...
                """.formatted(input);

        return askChat(prompt);
    }

    // 5) Moving company timing advice (city/time window -> best time + traffic notes)
    public String movingCompanyTimeAdvice(String cityAndTime) {
        String prompt = """
                You are a moving logistics assistant.
                Based on the provided city and time window (and any details), suggest the best moving time and traffic considerations.
                Be short: 5-8 sentences maximum.
                
                Input:
                %s
                
                Mention:
                - Best time windows (early morning / late night / weekday vs weekend)
                - Traffic/parking/loading considerations
                - One backup plan suggestion (if traffic is bad)
                - Safety and elevator/building coordination tip
                
                Rules:
                - Do not claim real-time traffic data.
                - Speak in practical general guidance.
                """.formatted(cityAndTime);

        return askChat(prompt);
    }

    // 6) Maintenance: repair vs replace advice
    public String maintenanceFixOrReplace(String input) {
        String prompt = """
                You are a maintenance decision assistant.
                Based on the issue description, advise whether to REPAIR (fix) or REPLACE.
                Be short: 5-8 sentences maximum.
                
                Input:
                %s
                
                Mention:
                - Which option is better and why
                - 2 decision factors (age/condition, safety, cost, availability, frequency of failure)
                - If inspection is needed first, say it
                
                Rules:
                - Do not invent prices.
                - If safety risk exists (electrical/gas/water major leak), recommend inspection/safety check first.
                """.formatted(input);

        return askChat(prompt);
    }

    public String companyIssueImageDiagnosis(String url, String language) {
        String prompt = """
                You are a maintenance/inspection diagnosis assistant.
                Based on the image url passed, scan the image and give a simple explanation for the issue in the image.
                Be short: 5-8 sentences maximum.
                
                url:
                %s
                
                language:
                %s
                
                Mention:
                - Which option is better and why
                - 2 decision factors (age/condition, safety, cost, availability, frequency of failure)
                - If inspection is needed first, say it
                
                Rules:
                1) If the image is NOT accessible, low quality, or unclear: say so and ask for 1–2 better photos + what angle is needed.
                2) Do NOT guess invisible details (no made-up measurements, no fake materials, no fake brands).
                3) If there is ANY safety risk (electricity, gas, structural crack, mold, water near outlets), flag it as "HIGH" risk.
                4) Always classify the best service type:
                   - INSPECTION first if risk/uncertainty is high
                   - MAINTENANCE if it’s a clear fix/repair
                   - REDESIGN only if it’s cosmetic/upgrade or repeated failures
                   - MOVING only if the scene is about items/packing/space
                5) Keep the response short and action-focused.
                
                Output:
                1) Detailed Summary (not too long just enough), organize it to make it easy to read
                2) What service it needs, inspection, maintenance.
                3) Tips, list them (1,2,3,..)
                """.formatted(url,language);

        return askChat(prompt);
    }
    public String customerRedesignFromImage(String url, String language) {

        String prompt = """
            You are an interior redesign assistant for a home services platform.
            The user provides an IMAGE URL of a room or a place.
            Analyze the visible space and propose a redesign plan.

            Keep it practical, realistic, and easy to follow.
            Be short but useful (8–14 lines max). Avoid long paragraphs.

            image url:
            %s

            language:
            %s

            Rules:
            1) If the image is NOT accessible, low quality, too dark, or unclear: say so and ask for 1–2 better photos + specify angles (wide shot + close-up of problem area).
            2) Do NOT invent measurements, brands, or materials you can't confirm from the image.
            3) If you notice safety risks (exposed wires, mold, water near outlets, structural cracks), flag it as "HIGH risk" and recommend INSPECTION first.
            4) Focus on redesign: layout, colors, lighting, furniture, storage, finishes.
            5) Give options: budget-friendly and mid-range upgrades.

            Output (use these headings exactly):
            1) Space Summary: (1-2 lines)
            2) Style Direction (pick 1 main + 1 alternative): (2 bullets)
            3) Suggested Changes:
               
            Layout (1-2 bullets)
            Colors & Materials (2 bullets)
            Lighting (1-2 bullets)
            Furniture/Decor (2 bullets)
            Storage (1 bullet)
            4) Quick Shopping List (5 bullets max, generic items only)
            5) First 3 Steps to Start (1,2,3)
            6) Questions to Confirm (3 short questions)
            """.formatted(url, language);

        return askChat(prompt);
    }

}
