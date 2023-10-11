<#assign newline = "\n">
\documentclass[12pt, letterpaper]{article} 
\usepackage{booktabs} 
\usepackage{siunitx} 
\usepackage{fancyhdr} 
\title{Financial Statements} 
\date{${fy_end?date}} 
\author{${companyName}} 
\pagestyle{fancy} 
\fancyhf{}  % Clear header and footer 
\fancyhead[L]{${companyName}}  % Left header 
\fancyfoot[C]{${fy_end?date}}  % Center footer  
${newline}
${newline}
\begin{document} 
</br>
</br>
\maketitle
</br>
\newpage
\section*{Income Statement}
\subsection*{Revenues}
\begin{tabular}{|l|r|}
\hline
Description & Amount \\
\hline
<#list revenueAccounts as account>
${account.name} & \num[group-separator={,},group-minimum-digits=4]${account.balance?string["0.00"]} \\
</#list>
\hline
\hline
\textbf{Total Revenue} & \$${totalRevenue?string["0.00"]} \\
\hline
\end{tabular}

\subsection*{Operating Expenses}
\begin{tabular}{|l|r|}
\hline
Description & Amount \\
\hline
<#list operatingExpenseAccounts as account>
${account.name?replace("&", "\\&")} & \$${account.balance?string["0.00"]} \\
</#list>
\hline
\hline
\textbf{Total Operating Expenses} & \$${totalOperatingExpenses?string["0.00"]} \\
\hline
\end{tabular}

\subsection*{Other Expenses}
\begin{tabular}{|l|r|}
\hline
Description & Amount \\
\hline
<#list otherExpenseAccounts as account>
${account.name} & \$${account.balance?string["0.00"]} \\
</#list>
\hline

\end{tabular}

\subsection*{Tax Information}
\begin{tabular}{|l|r|}
\hline
Description & Amount \\
\hline
Income Before Tax & \$${incomeBeforeTax?string["0.00"]} \\
Income Tax & \$${incomeTax?string["0.00"]} \\
Income After Tax & \$${incomeAfterTax?string["0.00"]} \\
\hline
\end{tabular}

\section*{Balance Sheet}
\subsection*{Assets}
\begin{tabular}{|l|r|}
\hline
Description & Amount \\
\hline
<#list assets as account>
${account.accountName?replace("_", "\\_")} & \$${account.balance?string["0.00"]} \\
</#list>
\hline
\hline
\textbf{Total Assets} & 
<#if totalAssets?is_number>
    <#if totalAssets < 0>
        \$\num[group-separator={,},group-minimum-digits=4]{${(totalAssets)?string["0.00"]}}
    <#else>
        \$\num[group-separator={,},group-minimum-digits=4]{${totalAssets?string["0.00"]}}
    </#if>
<#else>
   ${totalAssets} 
</#if> \\
\hline
\end{tabular}

\subsection*{Liabilities}
\begin{tabular}{|l|r|}
\hline
Description & Amount \\
\hline
<#list liabilities as account>
${account.accountName?replace("_", "\\_")} & 
\$${account.balance?string["0.00"]} \\
</#list>
\hline
\hline
\textbf{Total Liabilities} & 
<#if totalLiabilities?is_number>
    <#if totalLiabilities < 0>
        \$\num[group-separator={,},group-minimum-digits=4]{${(totalLiabilities)?string["0.00"]}}
    <#else>
        \$\num[group-separator={,},group-minimum-digits=4]{${totalLiabilities?string["0.00"]}}
    </#if>
<#else>
   ${totalLiabilities} 
</#if> \\
\hline
\end{tabular}

\subsection*{Equity}
\begin{tabular}{|l|r|}
\hline
Description & Amount \\
\hline
<#list equity as account>
${account.accountName} & \$${account.balance?string["0.00"]} \\
</#list>
\hline
\hline
\textbf{Total Equity} & 
<#if totalEquity?is_number>
    <#if totalEquity < 0>
        \$\num[group-separator={,},group-minimum-digits=4]{${(totalEquity)?string["0.00"]}}
    <#else>
        \$\num[group-separator={,},group-minimum-digits=4]{${totalEquity?string["0.00"]}}
    </#if>
<#else>
   ${totalEquity} 
</#if> \\
\hline

\end{tabular}

\end{document}
