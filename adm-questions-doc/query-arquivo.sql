Select 	'"' || replace(p.descricao, ',', '') || '",' || replace(r.descricao, ',', '') 
from 	perguntas p
	inner join respostas r on r.id_resposta = p.id_resposta
Order by r.descricao asc;

		