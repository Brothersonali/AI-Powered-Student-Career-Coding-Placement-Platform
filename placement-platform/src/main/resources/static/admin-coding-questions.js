const API_URL = "http://localhost:8080/api/coding/questions";

// Add multiple Test Case fields
let testCaseCount = 1;

document.getElementById("addTestCaseButton")
    .addEventListener("click", function () {

        testCaseCount++;

        const container =
            document.getElementById("testCasesContainer");

        const testCaseDiv = document.createElement("div");
        testCaseDiv.className = "test-case-form";

        const heading = document.createElement("h4");
        heading.textContent = "Test Case " + testCaseCount;

        const input = document.createElement("textarea");
        input.className = "testInput";
        input.placeholder = "Enter Test Case Input";
        input.required = true;

        const output = document.createElement("textarea");
        output.className = "testOutput";
        output.placeholder = "Enter Expected Output";
        output.required = true;

        const label = document.createElement("label");

        const checkbox = document.createElement("input");
        checkbox.type = "checkbox";
        checkbox.className = "testHidden";

        label.append(
            checkbox,
            document.createTextNode(" Hidden Test Case")
        );

        testCaseDiv.append(
            heading,
            input,
            output,
            label
        );

        container.appendChild(testCaseDiv);
    });


// Add Question with Multiple Test Cases
document.getElementById("addQuestionForm")
    .addEventListener("submit", async function (event) {

        event.preventDefault();

        const message =
            document.getElementById("questionMessage");

        const testInputs =
            document.querySelectorAll(".testInput");

        const testOutputs =
            document.querySelectorAll(".testOutput");

        const testHidden =
            document.querySelectorAll(".testHidden");

        const testCases = [];

        for (let i = 0; i < testInputs.length; i++) {

            if (
                !testInputs[i].value.trim() ||
                !testOutputs[i].value.trim()
            ) {
                message.textContent =
                    "Please fill all Test Case fields.";
                return;
            }

            testCases.push({
                input: testInputs[i].value,
                expectedOutput: testOutputs[i].value,
                hidden: testHidden[i].checked
            });
        }

        const question = {
            title: document.getElementById("title").value,
            description: document.getElementById("description").value,
            difficulty: document.getElementById("difficulty").value,
            topic: document.getElementById("topic").value,
            sampleInput: document.getElementById("sampleInput").value,
            sampleOutput: document.getElementById("sampleOutput").value,
            testCases: testCases
        };

        try {

            const response = await fetch(API_URL, {
                method: "POST",
                credentials: "include",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(question)
            });

            if (!response.ok) {
                throw new Error(
                    "Question could not be added. Status: " +
                    response.status
                );
            }

            message.textContent =
                "Question and Test Cases added successfully!";

            this.reset();

            // Keep only the first Test Case field
            const container =
                document.getElementById("testCasesContainer");

            const firstTestCase =
                container.querySelector(".test-case-form");

            container.innerHTML = "";

            if (firstTestCase) {
                container.appendChild(firstTestCase);
                firstTestCase.querySelector("h4").textContent =
                    "Test Case 1";
                firstTestCase.querySelectorAll("textarea")
                    .forEach(field => field.value = "");
                firstTestCase.querySelector("input[type='checkbox']")
                    .checked = false;
            }

            testCaseCount = 1;

            loadQuestions();

        } catch (error) {

            message.textContent = error.message;
            console.error(error);
        }
    });


// Load All Questions
async function loadQuestions() {

    const container =
        document.getElementById("questionsContainer");

    try {

        const response = await fetch(API_URL, {
            credentials: "include"
        });

        if (!response.ok) {
            throw new Error("Unable to load questions");
        }

        const questions = await response.json();

        container.innerHTML = "";

        if (questions.length === 0) {
            container.textContent =
                "No coding questions available.";
            return;
        }

        questions.forEach(question => {

            const card = document.createElement("div");
            card.className = "job-card";

            const title = document.createElement("h4");
            title.textContent = question.title;

            const description = document.createElement("p");
            description.textContent =
                question.description || "No description";

            const difficulty = document.createElement("p");
            difficulty.textContent =
                "Difficulty: " + question.difficulty;

            const topic = document.createElement("p");
            topic.textContent =
                "Topic: " + (question.topic || "General");

            const deleteButton = document.createElement("button");
            deleteButton.textContent = "Delete Question";

            deleteButton.addEventListener("click", function () {
                deleteQuestion(question.id);
            });

            const testCaseSection = document.createElement("div");

            const testCaseHeading = document.createElement("h4");
            testCaseHeading.textContent = "Manage Test Cases";

            const inputField = document.createElement("textarea");
            inputField.placeholder = "Enter Test Case Input";

            const outputField = document.createElement("textarea");
            outputField.placeholder = "Enter Expected Output";

            const hiddenLabel = document.createElement("label");

            const hiddenCheckbox = document.createElement("input");
            hiddenCheckbox.type = "checkbox";

            hiddenLabel.append(
                hiddenCheckbox,
                document.createTextNode(" Hidden Test Case")
            );

            const addTestCaseButton = document.createElement("button");
            addTestCaseButton.textContent = "Add Test Case";
            addTestCaseButton.type = "button";

            const testCaseMessage = document.createElement("p");

            const testCaseList = document.createElement("div");

            addTestCaseButton.addEventListener(
                "click",
                async function () {

                    if (
                        !inputField.value.trim() ||
                        !outputField.value.trim()
                    ) {
                        testCaseMessage.textContent =
                            "Please enter both input and expected output.";
                        return;
                    }

                    const testCase = {
                        input: inputField.value,
                        expectedOutput: outputField.value,
                        hidden: hiddenCheckbox.checked
                    };

                    try {

                        const response = await fetch(
                            `${API_URL}/${question.id}/test-cases`,
                            {
                                method: "POST",
                                credentials: "include",
                                headers: {
                                    "Content-Type": "application/json"
                                },
                                body: JSON.stringify(testCase)
                            }
                        );

                        if (!response.ok) {
                            throw new Error(
                                "Test case could not be added. Status: " +
                                response.status
                            );
                        }

                        testCaseMessage.textContent =
                            "Test case added successfully!";

                        inputField.value = "";
                        outputField.value = "";
                        hiddenCheckbox.checked = false;

                        loadTestCases(question.id, testCaseList);

                    } catch (error) {

                        testCaseMessage.textContent = error.message;
                        console.error(error);
                    }
                }
            );

            testCaseSection.append(
                testCaseHeading,
                inputField,
                outputField,
                hiddenLabel,
                addTestCaseButton,
                testCaseMessage,
                testCaseList
            );

            card.append(
                title,
                description,
                difficulty,
                topic,
                deleteButton,
                testCaseSection
            );

            container.appendChild(card);

            loadTestCases(question.id, testCaseList);
        });

    } catch (error) {

        container.textContent = error.message;
        console.error(error);
    }
}


// Load Existing Test Cases
async function loadTestCases(questionId, container) {

    try {

        const response = await fetch(
            `${API_URL}/${questionId}/test-cases`,
            {
                credentials: "include"
            }
        );

        if (!response.ok) {
            throw new Error("Unable to load test cases");
        }

        const testCases = await response.json();

        container.innerHTML = "";

        const heading = document.createElement("h5");
        heading.textContent = "Existing Test Cases";

        container.appendChild(heading);

        if (testCases.length === 0) {

            const message = document.createElement("p");
            message.textContent = "No test cases added yet.";

            container.appendChild(message);
            return;
        }

        testCases.forEach(testCase => {

            const item = document.createElement("div");

            const input = document.createElement("p");
            input.textContent = "Input: " + testCase.input;

            const output = document.createElement("p");
            output.textContent =
                "Expected Output: " + testCase.expectedOutput;

            const visibility = document.createElement("p");
            visibility.textContent = testCase.hidden
                ? "Hidden Test Case"
                : "Visible Test Case";

            const deleteButton = document.createElement("button");
            deleteButton.textContent = "Delete Test Case";

            deleteButton.addEventListener("click", function () {
                deleteTestCase(
                    questionId,
                    testCase.id,
                    container
                );
            });

            item.append(
                input,
                output,
                visibility,
                deleteButton
            );

            container.appendChild(item);
        });

    } catch (error) {

        container.textContent = error.message;
        console.error(error);
    }
}


// Delete Question
async function deleteQuestion(id) {

    if (!confirm("Are you sure you want to delete this question?")) {
        return;
    }

    try {

        const response = await fetch(`${API_URL}/${id}`, {
            method: "DELETE",
            credentials: "include"
        });

        if (!response.ok) {
            throw new Error("Question deletion failed");
        }

        alert("Question deleted successfully!");

        loadQuestions();

    } catch (error) {

        alert(error.message);
    }
}


// Delete Test Case
async function deleteTestCase(questionId, testCaseId, container) {

    if (!confirm("Are you sure you want to delete this test case?")) {
        return;
    }

    try {

        const response = await fetch(
            `${API_URL}/${questionId}/test-cases/${testCaseId}`,
            {
                method: "DELETE",
                credentials: "include"
            }
        );

        if (!response.ok) {
            throw new Error("Test case deletion failed");
        }

        alert("Test case deleted successfully!");

        loadTestCases(questionId, container);

    } catch (error) {

        alert(error.message);
    }
}


// Load questions when page opens
loadQuestions();