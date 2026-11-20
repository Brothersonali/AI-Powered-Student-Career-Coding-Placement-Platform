const jobContainer = document.getElementById("jobContainer");
const searchInput = document.getElementById("searchInput");
const searchBtn = document.getElementById("searchBtn");
const resetBtn = document.getElementById("resetBtn");
const resultCount = document.getElementById("resultCount");

let allJobs = [];

// 1. LOAD JOBS
async function loadJobs() {
    if (!jobContainer || !resultCount) return;

    jobContainer.innerHTML = '<p>Loading jobs...</p>';
    resultCount.textContent = "Loading jobs...";

    try {
        const response = await fetch("/jobs");

        if (!response.ok) {
            throw new Error("HTTP " + response.status);
        }

        const data = await response.json();

        if (Array.isArray(data)) {
            allJobs = data;
        } else if (data && Array.isArray(data.content)) {
            allJobs = data.content;
        } else {
            throw new Error("Invalid jobs response");
        }

        displayJobs(allJobs);

    } catch (error) {
        console.error("Job loading error:", error);
        resultCount.textContent = "Unable to load jobs";
        jobContainer.textContent = "Jobs could not be loaded.";
    }
}

// 2. DISPLAY JOBS
function displayJobs(jobs) {
    if (!jobContainer || !resultCount) return;

    jobContainer.innerHTML = "";

    resultCount.textContent = "Showing " + jobs.length + " jobs";

    if (jobs.length === 0) {
        jobContainer.textContent = "No jobs available.";
        return;
    }

    jobs.forEach(function(job) {
        const card = document.createElement("article");
        card.className = "job-card";

        const title = document.createElement("h3");
        title.textContent = job.title || "Untitled Job";

        const company = document.createElement("p");
        company.textContent = "Company: " + (job.companyName || "N/A");

        const location = document.createElement("p");
        location.textContent = "Location: " + (job.location || "N/A");

        const skills = document.createElement("p");
        skills.textContent = "Skills: " + (job.skills || "N/A");

        const salary = document.createElement("p");
        salary.textContent = "Salary: " + (job.salary ?? "N/A");

        const button = document.createElement("button");
        button.type = "button";
        button.className = "btn";
        button.textContent = "Apply Now";

        button.addEventListener("click", function() {
            openApplicationForm(job);
        });

        card.append(title, company, location, skills, salary, button);
        jobContainer.appendChild(card);
    });
}

// 3. SEARCH JOBS
function searchJobs() {
    if (!searchInput) return;

    const text = searchInput.value.trim().toLowerCase();

    const filteredJobs = allJobs.filter(function(job) {
        return String(job.title || "")
            .toLowerCase()
            .includes(text);
    });

    displayJobs(filteredJobs);
}

if (searchBtn) {
    searchBtn.addEventListener("click", searchJobs);
}

if (searchInput) {
    searchInput.addEventListener("keydown", function(event) {
        if (event.key === "Enter") {
            searchJobs();
        }
    });
}

// 4. RESET SEARCH
if (resetBtn) {
    resetBtn.addEventListener("click", function() {
        if (searchInput) searchInput.value = "";
        displayJobs(allJobs);
    });
}

// 5. ADD JOB - ADMIN
const addJobForm = document.getElementById("addJobForm");
const formMessage = document.getElementById("formMessage");

if (addJobForm) {
    addJobForm.addEventListener("submit", async function(event) {
        event.preventDefault();

        const job = {
            title: document.getElementById("jobTitle").value.trim(),
            companyName: document.getElementById("companyName").value.trim(),
            location: document.getElementById("jobLocation").value.trim(),
            salary: Number(document.getElementById("jobSalary").value),
            skills: document.getElementById("jobSkills").value.trim(),
            description: document.getElementById("jobDescription").value.trim()
        };

        try {
            if (formMessage) {
                formMessage.textContent = "Saving job...";
            }

            const response = await fetch("/jobs", {
                method: "POST",
                credentials: "same-origin",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(job)
            });

            if (!response.ok) {
                throw new Error("HTTP " + response.status);
            }

            if (formMessage) {
                formMessage.textContent = "Job added successfully!";
            }

            addJobForm.reset();
            await loadJobs();

        } catch (error) {
            console.error("Add job error:", error);

            if (formMessage) {
                formMessage.textContent = "Unable to add job.";
            }
        }
    });
}

// 6. OPEN APPLICATION FORM
function openApplicationForm(job) {
    const studentName = localStorage.getItem("studentName");
    const email = localStorage.getItem("studentEmail");

    if (!studentName || !email) {
        alert("Please login before applying.");
        window.location.href = "/login.html";
        return;
    }

    const phone = prompt("Enter your 10-digit phone number:");

    if (!phone || !/^[0-9]{10}$/.test(phone.trim())) {
        alert("Enter a valid 10-digit phone number.");
        return;
    }

    const resumeLink = prompt("Enter your resume link:");

    if (!resumeLink || !resumeLink.trim()) {
        alert("Resume link is required.");
        return;
    }

    submitApplication({
        studentName: studentName,
        email: email,
        phone: phone.trim(),
        resumeLink: resumeLink.trim(),
        jobId: job.id
    });
}

// 7. SUBMIT APPLICATION
async function submitApplication(application) {
    try {
        const response = await fetch("/applications", {
            method: "POST",
            credentials: "same-origin",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(application)
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error("HTTP " + response.status + ": " + errorText);
        }

        alert("Application submitted successfully!");

    } catch (error) {
        console.error("Application error:", error);
        alert("Application failed: " + error.message);
    }
}

// 8. INITIALIZE JOB LISTING
if (jobContainer && resultCount) {
    loadJobs();
}