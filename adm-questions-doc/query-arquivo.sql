Select 	'"' || replace(p.descricao, ',', '') || '",' || replace(r.titulo, ',', '') || ''''
from 	perguntas p
	inner join respostas r on r.id_resposta = p.id_resposta
Order by r.titulo asc;

		