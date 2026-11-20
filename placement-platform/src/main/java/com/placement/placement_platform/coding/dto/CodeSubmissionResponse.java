package com.placement.placement_platform.coding.dto;

import java.util.List;

public class CodeSubmissionResponse {

    private String status;
    private String output;
    private String message;

    private int totalTestCases;
    private int passedTestCases;
    private int failedTestCases;

    private List<TestCaseResult> testCaseResults;

    public CodeSubmissionResponse() {
    }

    public CodeSubmissionResponse(
            String status,
            String output,
            String message) {

        this.status = status;
        this.output = output;
        this.message = message;
    }

    public CodeSubmissionResponse(
            String status,
            String output,
            String message,
            int totalTestCases,
            int passedTestCases,
            int failedTestCases,
            List<TestCaseResult> testCaseResults) {

        this.status = status;
        this.output = output;
        this.message = message;
        this.totalTestCases = totalTestCases;
        this.passedTestCases = passedTestCases;
        this.failedTestCases = failedTestCases;
        this.testCaseResults = testCaseResults;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotalTestCases() {
        return totalTestCases;
    }

    public void setTotalTestCases(int totalTestCases) {
        this.totalTestCases = totalTestCases;
    }

    public int getPassedTestCases() {
        return passedTestCases;
    }

    public void setPassedTestCases(int passedTestCases) {
        this.passedTestCases = passedTestCases;
    }

    public int getFailedTestCases() {
        return failedTestCases;
    }

    public void setFailedTestCases(int failedTestCases) {
        this.failedTestCases = failedTestCases;
    }

    public List<TestCaseResult> getTestCaseResults() {
        return testCaseResults;
    }

    public void setTestCaseResults(List<TestCaseResult> testCaseResults) {
        this.testCaseResults = testCaseResults;
    }

    public static class TestCaseResult {

        private int testCaseNumber;
        private String status;
        private String expectedOutput;
        private String actualOutput;

        public TestCaseResult() {
        }

        public TestCaseResult(
                int testCaseNumber,
                String status,
                String expectedOutput,
                String actualOutput) {

            this.testCaseNumber = testCaseNumber;
            this.status = status;
            this.expectedOutput = expectedOutput;
            this.actualOutput = actualOutput;
        }

        public int getTestCaseNumber() {
            return testCaseNumber;
        }

        public void setTestCaseNumber(int testCaseNumber) {
            this.testCaseNumber = testCaseNumber;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getExpectedOutput() {
            return expectedOutput;
        }

        public void setExpectedOutput(String expectedOutput) {
            this.expectedOutput = expectedOutput;
        }

        public String getActualOutput() {
            return actualOutput;
        }

        public void setActualOutput(String actualOutput) {
            this.actualOutput = actualOutput;
        }
    }
}