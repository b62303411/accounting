
function parseFile() {
    var fileInput = document.getElementById('file');
    var formData = new FormData();
    formData.append('file', fileInput.files[0]);

    fetch('/api/invoice/parse', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        // Fill the form fields with the extracted information
        document.getElementById('recipient').value = data.recipient;
        document.getElementById('origine').value = data.origine;
        document.getElementById('nofac').value = data.noFacture;
        document.getElementById('description').value = data.description;
        document.getElementById('amount').value = data.amount;
        document.getElementById('tps').value = data.tps;
        document.getElementById('tvq').value = data.tvq;
        var date = new Date(data.date);
        var formattedDate = date.toISOString().split('T')[0];
        
        document.getElementById('date').value = formattedDate;
        // ... other fields ...
    })
    .catch(error => console.error('There was an error!', error));
}
document.getElementById('parseFile').addEventListener('click', parseFile);
