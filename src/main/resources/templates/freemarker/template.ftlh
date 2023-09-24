\documentclass[12pt, letterpaper]{article}

\title{Income Statement}
\date{${year}}
\author{${companyName}}

\begin{document}

\maketitle

\section*{Revenues}
\begin{tabular}{|l|r|}
\hline
Description & Amount \\
\hline
<#list revenueAccounts as account>
${account.accountName} & \$${account.balance?string["0.00"]} \\
</#list>
\hline
\end{tabular}

\section*{Operating Expenses}
\begin{tabular}{|l|r|}
\hline
Description & Amount \\
\hline
<#list operatingExpenseAccounts as account>
${account.accountName} & \$${account.balance?string["0.00"]} \\
</#list>
\hline
\end{tabular}

\section*{Other Expenses}
\begin{tabular}{|l|r|}
\hline
Description & Amount \\
\hline
<#list otherExpenseAccounts as account>
${account.accountName} & \$${account.balance?string["0.00"]} \\
</#list>
\hline
\end{tabular}

\section*{Tax Information}
\begin{tabular}{|l|r|}
\hline
Description & Amount \\
\hline
Income Before Tax & \$${incomeBeforeTax?string["0.00"]} \\
Income Tax & \$${incomeTax?string["0.00"]} \\
Income After Tax & \$${incomeAfterTax?string["0.00"]} \\
\hline
\end{tabular}

\end{document}
