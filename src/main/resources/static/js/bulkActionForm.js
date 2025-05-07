
function handleBulkActionSubmit(event) {
	event.preventDefault();

	var formData = {
		description: document.getElementById("description").value,
		newType: document.getElementById("newType").value
	};

	fetch("/api/expenses/bulkupdate", {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify(formData)
	})
		.then(response => response.json())
		.then(data => {
			// Handle response here. e.g., show a success message or update the UI
		})
		.catch(error => console.error('There was an error!', error));
}

document.getElementById("bulkActionForm").addEventListener("submit", handleBulkActionSubmit);