// Delete job
async function deleteJob(id) {
    if (!confirm("Are you sure you want to delete this job?")) {
        return;
    }

    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: "DELETE",
            credentials: "same-origin"
        });

        if (!response.ok) {
            const errorText = await response.text();

            throw new Error(
                errorText || `Job deletion failed (${response.status})`
            );
        }

        alert("Job deleted successfully!");
        await loadJobs();

    } catch (error) {
        alert(error.message);
    }
}