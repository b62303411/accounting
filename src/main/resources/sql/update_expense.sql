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
  WHERE t.description LIKE '%0148275476%'
) AS matching_expenses
WHERE ee.id = matching_expenses.id;

UPDATE public.exploitation_expense ee
SET expense_type = 'Fournitures'
FROM (
  SELECT e.id AS id
  FROM public.expense e
  JOIN public.transaction t ON e.transaction_id = t.id
  WHERE t.description LIKE '%THEHOMEDEPOT%'
) AS matching_expenses
WHERE ee.id = matching_expenses.id;


UPDATE public.exploitation_expense ee
SET expense_type = 'FraisDeplacement'
FROM (
  SELECT e.id AS id
  FROM public.expense e
  JOIN public.transaction t ON e.transaction_id = t.id
  WHERE t.description LIKE '%IVANHOECAMBRIDGEINC%'
) AS matching_expenses
WHERE ee.id = matching_expenses.id;
