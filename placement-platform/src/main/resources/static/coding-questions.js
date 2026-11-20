// CODING QUESTIONS PAGE

const API_URL = "http://localhost:8080/api/coding/questions";
const SOLVED_API_URL = "http://localhost:8080/api/coding/solved";

const questionList = document.getElementById("questionList");
const searchInput = document.getElementById("searchInput");
const difficultyFilter = document.getElementById("difficultyFilter");

let allQuestions = [];
let selectedQuestionId = null;
let solvedQuestionIds = new Set();


// FETCH SOLVED QUESTIONS
async function fetchSolvedQuestions() {
    try {
        const response = await fetch(SOLVED_API_URL, {
            method: "GET",
            credentials: "include"
        });

        if (!response.ok) {
            solvedQuestionIds = new Set();
            return;
        }

        const ids = await response.json();

        solvedQuestionIds = new Set(
            ids.map(id => Number(id))
        );

    } catch (error) {
        solvedQuestionIds = new Set();
        console.error("Unable to load solved questions:", error);
    }
}


// FETCH QUESTIONS
async function fetchQuestions() {
    try {
        const response = await fetch(API_URL);

        if (!response.ok) {
            throw new Error("Failed to fetch questions");
        }

        allQuestions = await response.json();

        await fetchSolvedQuestions();

        displayQuestions(allQuestions);

    } catch (error) {
        questionList.innerHTML =
            "<p>Unable to load questions. Please start the backend.</p>";

        console.error(error);
    }
}


// DISPLAY QUESTIONS
function displayQuestions(questions) {
    questionList.innerHTML = "";

    if (questions.length === 0) {
        questionList.innerHTML = "<p>No questions found.</p>";
        return;
    }

    questions.forEach(question => {
        const card = document.createElement("div");

        card.className = "question-card";

        const difficulty =
            (question.difficulty || "Easy").toLowerCase();

        const isSolved =
            solvedQuestionIds.has(Number(question.id));

        card.innerHTML = `
            <h3>${question.title}</h3>

            <p>${question.description || "No description available"}</p>

            <div class="question-meta">

                <span class="question-badge difficulty-${difficulty}">
                    ${question.difficulty || "Not specified"}
                </span>

                <span class="question-badge">
                    ${question.topic || "General"}
                </span>

                <span class="question-badge ${isSolved ? "solved-status" : "unsolved-status"}">
                    ${isSolved ? "✓ Solved" : "Unsolved"}
                </span>

            </div>

            <button class="view-details-btn">
                View Details
            </button>

            <div class="question-details" style="display:none;">

                <h4>Problem Description</h4>
                <p>${question.description || "Not available"}</p>

                <h4>Sample Input</h4>
                <pre>${question.sampleInput || "Not available"}</pre>

                <h4>Sample Output</h4>
                <pre>${question.sampleOutput || "Not available"}</pre>

            </div>
        `;

        const details = card.querySelector(".question-details");
        const button = card.querySelector(".view-details-btn");

        button.addEventListener("click", function () {

            if (details.style.display === "none") {

                details.style.display = "block";
                button.textContent = "Hide Details";

                selectedQuestionId = question.id;

                document.getElementById("solveSection").style.display =
                    "block";

            } else {

                details.style.display = "none";
                button.textContent = "View Details";

                document.getElementById("solveSection").style.display =
                    "none";
            }
        });

        questionList.appendChild(card);
    });
}


// SEARCH AND FILTER
function filterQuestions() {

    const searchText = searchInput.value.toLowerCase();
    const difficulty = difficultyFilter.value;

    const filteredQuestions = allQuestions.filter(question => {

        const matchesSearch =
            (question.title || "").toLowerCase().includes(searchText);

        const matchesDifficulty =
            difficulty === "" || question.difficulty === difficulty;

        return matchesSearch && matchesDifficulty;
    });

    displayQuestions(filteredQuestions);
}

searchInput.addEventListener("input", filterQuestions);
difficultyFilter.addEventListener("change", filterQuestions);


// LOAD QUESTIONS
fetchQuestions();


// CODING LANGUAGE STARTER CODE

const languageSelect = document.getElementById("language");
const codeEditor = document.getElementById("codeEditor");

const starterCodes = {

    java: `public class Main {
    public static void main(String[] args) {

        // Write your code here

    }
}`,

    python: `def main():

    # Write your code here
    pass


if __name__ == "__main__":
    main()`,

    cpp: `#include <iostream>
using namespace std;

int main() {

    // Write your code here

    return 0;
}`
};


// SET STARTER CODE
function setStarterCode() {

    const selectedLanguage = languageSelect.value;

    codeEditor.value = starterCodes[selectedLanguage];
}

languageSelect.addEventListener("change", setStarterCode);

setStarterCode();


// SUBMIT CODE TO BACKEND

const submitButton = document.getElementById("submitCode");
const stdinInput = document.getElementById("stdinInput");
const submitMessage = document.getElementById("submitMessage");

submitButton.addEventListener("click", async function () {

    const language = languageSelect.value;
    const code = codeEditor.value;
    const stdin = stdinInput.value;

    submitMessage.textContent = "Executing code...";

    try {

        const response = await fetch(
            "http://localhost:8080/api/coding/execute",
            {
                method: "POST",
                credentials: "include",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    language: language,
                    code: code,
                    stdin: stdin,
                    questionId: selectedQuestionId
                })
            }
        );

        const responseText = await response.text();

        console.log("HTTP Status:", response.status);
        console.log("Server Response:", responseText);

        let result = {};

        if (responseText.trim()) {

            try {
                result = JSON.parse(responseText);

            } catch {
                submitMessage.textContent =
                    "Invalid response from server.";
                return;
            }
        }

        if (!response.ok) {

            if (response.status === 401 || response.status === 403) {

                submitMessage.textContent =
                    "Please login as a student before submitting your code.";

            } else {

                submitMessage.textContent =
                    "Error " + response.status + ": " +
                    (result.message || responseText || "Request failed");
            }

            return;
        }


        // DISPLAY EXECUTION RESULT

        let resultText =
            "Status: " + result.status + "\n";

        if (result.totalTestCases > 0) {

            resultText +=
                "Total Test Cases: " + result.totalTestCases + "\n" +
                "Passed: " + result.passedTestCases + "\n" +
                "Failed: " + result.failedTestCases + "\n\n";

            result.testCaseResults.forEach(testCase => {

                resultText +=
                    "Test Case " + testCase.testCaseNumber +
                    ": " + testCase.status + "\n";

                resultText +=
                    "Expected Output: " + testCase.expectedOutput + "\n";

                resultText +=
                    "Actual Output: " + testCase.actualOutput + "\n\n";
            });

        } else {

            resultText += "Output: " + result.output + "\n";
        }

        resultText += "\nMessage: " + result.message;

        submitMessage.textContent = resultText;


        // REFRESH SOLVED STATUS AFTER ACCEPTED

        if (
            result.status === "Accepted" &&
            selectedQuestionId !== null
        ) {

            await fetchSolvedQuestions();

            filterQuestions();
        }

    } catch (error) {

        submitMessage.textContent =
            "Unable to execute code. Please check the backend.";

        console.error(error);
    }
});