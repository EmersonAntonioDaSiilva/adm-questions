package br.com.afirmanet.questions.manager.application.additional;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import br.com.afirmanet.core.manager.AbstractManager;
import br.com.afirmanet.core.producer.ApplicationManaged;
import br.com.afirmanet.questions.constante.RelatorioClassificacaoEnum;
import br.com.afirmanet.questions.dao.ClassificacaoDAO;
import br.com.afirmanet.questions.dao.ClienteDAO;
import br.com.afirmanet.questions.dao.RespostaDAO;
import br.com.afirmanet.questions.dao.TopicoDAO;
import br.com.afirmanet.questions.entity.Classificacao;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.Resposta;
import br.com.afirmanet.questions.entity.Topico;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class RelatorioClassificacaoManager extends AbstractManager implements Serializable {

	private static final long serialVersionUID = -331556378253936963L;

	@Getter
	@Setter
	private Cliente cliente;

	@Getter
	@Setter
	private Topico topico;

	@Getter
	@Setter
	private Resposta resposta;

	@Getter
	private List<Cliente> lstCliente;

	@Getter
	private List<Topico> lstTopico;

	@Getter
	private List<Classificacao> lstClassificaoChart;

	@Getter
	private List<Resposta> lstResposta;

	@Getter
	@Setter
	private LocalDate dataInicioClassificacao;

	@Getter
	private BarChartModel barModel;

	private int valorMaximoEixoY;

	@Inject
	@ApplicationManaged
	private EntityManager entityManager;

	@PostConstruct
	public void inicializar() {
		ClienteDAO clienteDAO = new ClienteDAO(entityManager);
		lstCliente = clienteDAO.findAll();
	}

	private void createBarModel() {
		this.valorMaximoEixoY = 0;
		barModel = initBarModel();

		barModel.setTitle("Classificaçao de Sentimentos");
		barModel.setLegendPosition("ne");

		Axis xAxis = barModel.getAxis(AxisType.X);
		xAxis.setLabel("Datas");

		Axis yAxis = barModel.getAxis(AxisType.Y);
		yAxis.setLabel("Quantidade de Sentimentos");
		yAxis.setMin(0);
		
		Integer max = this.valorMaximoEixoY;
		if(this.valorMaximoEixoY > 0)
			max =  (int) (this.valorMaximoEixoY + (this.valorMaximoEixoY * 0.10));
		
		yAxis.setMax(max);
	}

	private BarChartModel initBarModel() {
		BarChartModel model = new BarChartModel();

		LocalDate dtFim = LocalDate.of(dataInicioClassificacao.getYear(), dataInicioClassificacao.getMonth(), dataInicioClassificacao.getDayOfMonth()).plusMonths(1);
		dtFim = LocalDate.of(dtFim.getYear(), dtFim.getMonth(), 1).minusDays(1);

		
		ChartSeries positivos = countPorData(dataInicioClassificacao, dtFim,
				RelatorioClassificacaoEnum.SENTIMENTO_POSITIVO);
		ChartSeries negativos = countPorData(dataInicioClassificacao, dtFim,
				RelatorioClassificacaoEnum.SENTIMENTO_NEGATIVO);
		ChartSeries imparciais = countPorData(dataInicioClassificacao, dtFim,
				RelatorioClassificacaoEnum.SENTIMENTO_IMPARCIAL);

		model.addSeries(positivos);
		model.addSeries(negativos);
		model.addSeries(imparciais);

		return model;
	}

	private ChartSeries countPorData(LocalDate dtIni, LocalDate dtFim,
			RelatorioClassificacaoEnum sentimento) {
		ChartSeries retorno = new ChartSeries();

		ClassificacaoDAO classificacaoDAO = new ClassificacaoDAO(entityManager);
		retorno.setLabel(sentimento.getChartLabel());
		
		List<Classificacao> classificacao = classificacaoDAO.findByDataCadastroESentimento(cliente, topico,
				dtIni, dtFim, sentimento.getCodigo());

		if (classificacao != null && !classificacao.isEmpty()){
			Map<String, Integer> collect = classificacao.stream().collect(Collectors.groupingBy(
					Classificacao::getDataCadastroFormatMesAno, Collectors.summingInt(Classificacao::getRegistro)));
			
			collect.forEach((Data, size) -> retorno.set(Data, size));

			if(classificacao.size() > this.valorMaximoEixoY) {
				this.valorMaximoEixoY = classificacao.size();
			}
		}else{
			DateTimeFormatter pattern = DateTimeFormatter.ofPattern("MM/yyyy");
			retorno.set(dtIni.format(pattern), 0);
		}

		return retorno;
	}

	public void selecionarCliente() {
		TopicoDAO topicoDAO = new TopicoDAO(entityManager);
		this.lstTopico = topicoDAO.findbyCliente(this.cliente);

	}

	public void buscarClassificacao() {
		RespostaDAO respostaDAO = new RespostaDAO(entityManager);
		this.lstResposta = respostaDAO.findByClienteAndTopico(this.cliente, this.topico);

		createBarModel();
	}

	@SuppressWarnings("unchecked")
	public void chartItemSelect(ItemSelectEvent event) {
		BarChartModel cModel = (BarChartModel) ((org.primefaces.component.chart.Chart) event.getSource()).getModel();
		ChartSeries mySeries = cModel.getSeries().get(event.getSeriesIndex());

		Set<Entry<Object, Number>> mapValues = mySeries.getData().entrySet();

		Entry<Object, Number>[] test = new Entry[mapValues.size()];
		mapValues.toArray(test);

		String dataCadastroTexto = (String) test[event.getItemIndex()].getKey();

		Integer mes = Integer.parseInt(dataCadastroTexto.split("/")[0]);
		Integer ano = Integer.parseInt(dataCadastroTexto.split("/")[1]);
		Integer dia = 1;

		LocalDate dtInicio = LocalDate.of(ano, mes, dia);
		LocalDate dtFim = LocalDate.of(ano, mes, dia).plusMonths(1);
		dtFim = LocalDate.of(dtFim.getYear(), dtFim.getMonth(), 1).minusDays(1);

		Integer sentimento = 0;

		if (RelatorioClassificacaoEnum.SENTIMENTO_POSITIVO.getChartLabel().equals(mySeries.getLabel())) {
			sentimento = RelatorioClassificacaoEnum.SENTIMENTO_POSITIVO.getCodigo();
		}

		if (RelatorioClassificacaoEnum.SENTIMENTO_NEGATIVO.getChartLabel().equals(mySeries.getLabel())) {
			sentimento = RelatorioClassificacaoEnum.SENTIMENTO_NEGATIVO.getCodigo();
		}

		ClassificacaoDAO classificacaoDAO = new ClassificacaoDAO(entityManager);
		this.lstClassificaoChart = classificacaoDAO.findByDataCadastroESentimento(cliente, topico, dtInicio, dtFim,
				sentimento);

	}
}
