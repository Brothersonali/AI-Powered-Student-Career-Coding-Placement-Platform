package com.placement.placement_platform.coding.service;

import com.placement.placement_platform.coding.dto.CodeSubmissionRequest;
import com.placement.placement_platform.coding.dto.CodeSubmissionResponse;
import com.placement.placement_platform.coding.entity.CodingTestCase;
import com.placement.placement_platform.coding.repository.CodingTestCaseRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class CodeExecutionService {

    private final RestClient restClient;
    private final CodingTestCaseRepository testCaseRepository;
    private final StudentSolvedQuestionService solvedService;

    public CodeExecutionService(
            @Value("${judge0.api.url}") String apiUrl,
            CodingTestCaseRepository testCaseRepository,
            StudentSolvedQuestionService solvedService) {

        this.restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .build();

        this.testCaseRepository = testCaseRepository;
        this.solvedService = solvedService;
    }

    public CodeSubmissionResponse executeCode(
            CodeSubmissionRequest request,
            Long studentId) {

        try {
            if (request.getQuestionId() == null) {
                return executeCustomInput(request);
            }

            List<CodingTestCase> testCases =
                    testCaseRepository.findByQuestionId(
                            request.getQuestionId()
                    );

            if (testCases.isEmpty()) {
                return executeCustomInput(request);
            }

            int passed = 0;
            int failed = 0;

            List<CodeSubmissionResponse.TestCaseResult> results =
                    new ArrayList<>();

            String lastOutput = "";
            String executionFailureStatus = "";

            for (int i = 0; i < testCases.size(); i++) {

                CodingTestCase testCase = testCases.get(i);

                // Hide expected output for hidden test cases
                String expectedOutput = testCase.isHidden()
                        ? null
                        : testCase.getExpectedOutput();


                ExecutionResult execution = runCode(
                        request.getLanguage(),
                        request.getCode(),
                        testCase.getInput()
                );
                String actualOutput = testCase.isHidden()
                        ? null
                        : execution.output;

                if (!testCase.isHidden()) {
                    lastOutput = execution.output;
                }

                if (!"Accepted".equalsIgnoreCase(execution.status)) {
                    executionFailureStatus = execution.status;
                }

                boolean isPassed =
                        "Accepted".equalsIgnoreCase(execution.status)
                                && normalizeOutput(execution.output)
                                .equals(normalizeOutput(
                                        testCase.getExpectedOutput()
                                ));
                if (isPassed) {
                    passed++;

                    results.add(
                            new CodeSubmissionResponse.TestCaseResult(
                                    i + 1,
                                    "Passed",
                                    expectedOutput,
                                    actualOutput
                            )
                    );
                }

                else {
                    failed++;

                    results.add(
                            new CodeSubmissionResponse.TestCaseResult(
                                    i + 1,
                                    "Failed",
                                    expectedOutput,
                                    actualOutput
                            )
                    );
                }
            }

            String finalStatus;

            if (failed == 0) {
                finalStatus = "Accepted";
            } else if (!executionFailureStatus.isEmpty()) {
                finalStatus = executionFailureStatus;
            } else {
                finalStatus = "Wrong Answer";
            }

            // Save question when all test cases pass
            if ("Accepted".equalsIgnoreCase(finalStatus)) {
                solvedService.markAsSolved(
                        studentId,
                        request.getQuestionId()
                );
            }

            return new CodeSubmissionResponse(
                    finalStatus,
                    lastOutput,
                    "Test case evaluation completed",
                    testCases.size(),
                    passed,
                    failed,
                    results
            );

        } catch (Exception e) {

            return new CodeSubmissionResponse(
                    "Error",
                    "",
                    "Code execution failed: " + e.getMessage()
            );
        }
    }

    private CodeSubmissionResponse executeCustomInput(
            CodeSubmissionRequest request) {

        ExecutionResult result = runCode(
                request.getLanguage(),
                request.getCode(),
                request.getStdin()
        );

        return new CodeSubmissionResponse(
                result.status,
                result.output,
                "Execution completed"
        );
    }

    private ExecutionResult runCode(
            String language,
            String code,
            String input) {

        int languageId = getLanguageId(language);

        Map<String, Object> payload = new HashMap<>();

        payload.put("source_code", code);
        payload.put("language_id", languageId);
        payload.put("stdin", input == null ? "" : input);

        Map<String, Object> result = restClient.post()
                .uri("/submissions?base64_encoded=false&wait=true")
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (result == null) {
            throw new IllegalStateException(
                    "Empty response from Judge0"
            );
        }

        Map<?, ?> status = (Map<?, ?>) result.get("status");

        String statusText = status == null
                ? "Unknown"
                : String.valueOf(status.get("description"));

        Object output = result.get("stdout");

        if (output == null) {
            output = result.get("stderr");
        }

        if (output == null) {
            output = result.get("compile_output");
        }

        String outputText = output == null
                ? ""
                : String.valueOf(output);

        return new ExecutionResult(statusText, outputText);
    }

    private String normalizeOutput(String output) {

        if (output == null) {
            return "";
        }

        return output.replace("\r\n", "\n").trim();
    }

    private int getLanguageId(String language) {

        if (language == null) {
            throw new IllegalArgumentException(
                    "Language is required"
            );
        }

        return switch (language.toLowerCase()) {
            case "java" -> 62;
            case "python" -> 71;
            case "cpp" -> 54;
            default -> throw new IllegalArgumentException(
                    "Unsupported programming language"
            );
        };
    }

    private static class ExecutionResult {

        private final String status;
        private final String output;

        private ExecutionResult(String status, String output) {
            this.status = status;
            this.output = output;
        }
    }
}