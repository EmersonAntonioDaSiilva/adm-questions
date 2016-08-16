select 	* 
from 	classificacoes 
where 	data_cadastro between  '2016-08-01 00:00:00.000' and '2016-08-31 23:59:59.000' 
and 	data_classificacao is null
and 	sentimento not in(1, 100, 200)
	Order by resposta, confidence;


