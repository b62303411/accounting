UPDATE public.exploitation_expense ee
SET expense_type = 'FraisBancaires'
FROM (
  SELECT e.id AS id
  FROM public.expense e
  JOIN public.transaction t ON e.transaction_id = t.id
  WHERE t.description LIKE '%FRAIS MENS PLAN SERV%'
) AS matching_expenses
WHERE ee.id = matching_expenses.id;


UPDATE public.exploitation_expense ee
SET expense_type = 'HonoraireProfessionel'
FROM (
  SELECT e.id AS id
  FROM public.expense e
  JOIN public.transaction t ON e.transaction_id = t.id
  WHERE t.description LIKE '%88921927%'
) AS matching_expenses
WHERE ee.id = matching_expenses.id;

UPDATE public.exploitation_expense ee
SET expense_type = 'Fournitures'
FROM (
  SELECT e.id AS id
  FROM public.expense e
  JOIN public.transaction t ON e.transaction_id = t.id
  WHERE t.description LIKE '%BUREAUENGROS%'
) AS matching_expenses
WHERE ee.id = matching_expenses.id;


UPDATE public.exploitation_expense ee
SET expense_type = 'FraisDeplacement'
FROM (
  SELECT e.id AS id
  FROM public.expense e
  JOIN public.transaction t ON e.transaction_id = t.id
  WHERE t.description LIKE '%VINCIPARKTOURALTITUMONTREAL%'
) AS matching_expenses
WHERE ee.id = matching_expenses.id;
