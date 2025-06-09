You are a bank statement parser. Given the following raw statement content, extract the data and format it as a valid JSON document.

 
 
 The account type should comes just before te transactions. 
 
 The info of the transaction should be roughly in that format in the pdf,,
 at least logically
 
 
 |Date| Description         |Retraits ($)| Dépôts ($) |Soldes ($)|
 |27  | MAI SOLDE PRECEDENT |            |            |12 338,49 | 
 |30  | MAI PLACEMENT NBI   | 307,70     |            |12 030,79 |

<<RAW_INPUT>>
${rawText}
<<END_RAW_INPUT>>

Format the result in the following JSON structure:
Example:
```json
{
   "AccountInfo": {
      "AccountType": "",
      "AccountNumber": "",
      "StatementPeriod": "DD MMM YY - DD MMM YY", <--- formated like this but with actual date
      "NextStatementDate": "DD MMM YY", <--- formated like this but with actual date
      "CreditBalance": " $",
      "MinMonthlyBalance": "$"
   },
   "Transactions": [
      {
         "Description": "",
         "Retraits": "",
         "Depots": "",
         "Date": "",
         "Solde": ""
      }
   ]
}
```

Only include the JSON content inside the ```json block. Do not include any extra commentary.
