var editButtons = document.getElementsByClassName('edit-button');
	Array.from(editButtons).forEach(function (button) {
		button.addEventListener('click', function (e) {
			var expenseId = e.target.dataset.id;

			$.get("/api/expenses/" + expenseId, function (data) {
				// `data` contains the JSON response from your server. You can now populate the modal with this information.

				// Example: Set the description field in the modal
				$("#transaction.description").val(data.transaction.description);
				$("#transaction\\.date").text(data.transaction.date);
				$("#transaction\\.account").text(data.transaction.account);
				$("#transaction\\.description").text(data.transaction.description);
				$("#transaction\\.amount").text(data.transaction.amount);
				$("#transaction\\.type").text(data.transaction.type);
				$("#transaction\\.isCashFlow").text(data.transaction.isCashFlow ? "Yes" : "No"); // Assuming it's a boolean value
				$("#transaction\\.note").text(data.transaction.note);
				// You could set other fields as well

				// Finally, show the modal (assuming you're using Bootstrap)
				$('#editModal').modal('show');
			})
				.fail(function () {
					alert("An error occurred while fetching the expense details.");
				});
			// Fetch the expense details and populate the form, or populate directly from table data

			$.ajax({
				url: "/api/invoice/findByExpenseId", // Update with your API endpoint
				method: "GET",
				data: {expenseId: expenseId},
				success: function (invoices) {
					// Clear existing options
					$("#invoices").empty();

					// Add a default option
					$("#invoices").append('<option value="">Select an Invoice</option>');

					// Loop through the invoices and append to the dropdown
					invoices.forEach(function (invoice) {
						$("#invoices").append('<option value="' + invoice.id + '">' + invoice.name + '</option>');
					});
				},
				error: function (err) {
					console.error("An error occurred while fetching the invoices:", err);
					// Handle the error as needed
				}
			});
			// Show the modal
			//modal.style.display = "block";
			//var myModal = new bootstrap.Modal(document.getElementById('editModal'), {});
			//myModal.show();
		});
	});