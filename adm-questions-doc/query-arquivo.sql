﻿Select 	'"' || replace(p.descricao, ',', '') || '",' || replace(r.titulo, ',', '') || ''''
from 	perguntas p
	inner join respostas r on r.id_resposta = p.id_resposta
Where	id_cliente = 1
Group by p.descricao, r.titulo
Order by r.titulo asc;



select * from clientes




Select 	r.titulo
from 	respostas r 
Where	id_cliente = 1
Order by r.titulo asc;
