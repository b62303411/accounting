function format(timestamp) {
	const i_timestamp = parseInt(timestamp);
	const date = new Date(i_timestamp);
	const options = { year: 'numeric', month: 'long', day: 'numeric' };
	const formattedDate = new Intl.DateTimeFormat('en-US', options).format(date);
	return formattedDate;
}

$(document).ready(function() {
	$('#ledger-table').DataTable({
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