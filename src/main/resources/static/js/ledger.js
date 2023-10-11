function format(timestamp) {
	const i_timestamp = parseInt(timestamp);
	const date = new Date(i_timestamp);
	const options = { year: 'numeric', month: 'long', day: 'numeric' };
	const formattedDate = new Intl.DateTimeFormat('en-US', options).format(date);
	return formattedDate;
}
function updateDataTable() {
	$("#loadingSpinner").show();

	//let table = $('#ledger-table');

	globalTable.clear().draw();  // Clear the existing rows

	const evtSource = new EventSource("/api/ledger/stream");

	evtSource.onmessage = function(event) {

		let entries = JSON.parse(event.data);

		if (entries.endOfStream) {
			// This is the end-of-stream message
			$("#loadingSpinner").hide();  // Hide the spinner
			evtSource.close();  // Close the event source connection
		} else {
			// Prepare an array to hold rows
			let rows = [];

			// Loop through each record in the batch
			for (let entry of entries) {
				rows.push([
					entry.unixTime,
					entry.accountType,
					entry.message,
					entry.glAccountName,
					entry.glAccountNumber,
					entry.vendorOrClient,
					entry.debit,
					entry.credit,
					entry.balence,
					entry.abalence
				]);
			}
			globalTable.rows.add(rows).draw(false); // false: do not redraw the entire table
		};

		evtSource.onerror = function(event) {
			console.error("EventSource failed:", event);
			evtSource.close();
			//$("#loadingSpinner").hide();
		};

		evtSource.onopen = function(event) {
			//$("#loadingSpinner").hide();  // Hide the spinner once the connection is open and data starts flowing
		};
	}
}
let globalTable;

$(document).ready(function() {
	
	globalTable = $('#ledger-table').DataTable({
		processing: false,
		responsive: true,
		columnDefs: [
			{
				targets: 0,  // Target the first column (date column)
				render: function(data, type, row) {
					if (type === 'sort' || type === 'type') {
						return data;
					}
					return format(data);
				}
			},
			{

				targets: [6, 7, 8, 9],  // The indices of the "Debit" and "Credit" columns
				render: function(data, type, row) {
					if (type === 'display' || type === 'filter') {
						if (data === null || data === '') {
							return '-';
						}
						return '$' + parseFloat(data).toFixed(2);
					}
					return data;

				}
			}
		],
		order: [[0, 'asc']]  // Sort by the date column


	});
});