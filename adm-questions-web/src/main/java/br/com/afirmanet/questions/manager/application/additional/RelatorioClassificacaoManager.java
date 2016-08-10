package br.com.afirmanet.questions.manager.application.additional;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

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
import br.com.afirmanet.questions.dao.TopicoDAO;
import br.com.afirmanet.questions.entity.Classificacao;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.Topico;
import br.com.afirmanet.questions.grafico.modelo.ClassificacaoChart;
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
	private List<Cliente> lstCliente;
	
	@Getter
	private List<Topico> lstTopico;
	
	@Getter
	private List<Classificacao> lstClassificao;
	
	@Getter
	private List<Classificacao> lstClassificaoChart;
	
	@Getter
	@Setter
	private LocalDate dataInicioClassificacao;

	@Getter
	@Setter
	private LocalDate dataFimClassificacao;	
	
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
        barModel = initBarModel();
         
        barModel.setTitle("Classificaçao de Sentimentos");
        barModel.setLegendPosition("ne");
         
        Axis xAxis = barModel.getAxis(AxisType.X);
        xAxis.setLabel("Datas");
         
        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("Quantidade de Sentimentos");
        yAxis.setMin(0);
        yAxis.setMax(this.valorMaximoEixoY);
    }
    
    private BarChartModel initBarModel() {
        BarChartModel model = new BarChartModel();
        
        List<ClassificacaoChart> listaClassificacaoChart = listarClassificacaoChart();

        ChartSeries positivos = new ChartSeries();
        positivos.setLabel(RelatorioClassificacaoEnum.SENTIMENTO_POSITIVO.getChartLabel());
        
        listaClassificacaoChart.forEach(positivo -> positivos.set(positivo.dataCadastroFormatada(), positivo.getPositivos().size()));
 
        ChartSeries negativos = new ChartSeries();
        negativos.setLabel(RelatorioClassificacaoEnum.SENTIMENTO_NEGATIVO.getChartLabel());
        
        listaClassificacaoChart.forEach(negativo -> negativos.set(negativo.dataCadastroFormatada(), negativo.getNegativos().size()));
        
        ChartSeries imparciais = new ChartSeries();
        imparciais.setLabel(RelatorioClassificacaoEnum.SENTIMENTO_IMPARCIAL.getChartLabel());
        
        listaClassificacaoChart.forEach(naoSeAplica -> imparciais.set(naoSeAplica.dataCadastroFormatada(), naoSeAplica.getNaoSeAplicam().size()));
        
        model.addSeries(positivos);
        model.addSeries(negativos);
        model.addSeries(imparciais);
         
        return model;
    }

	private List<ClassificacaoChart> listarClassificacaoChart() {
		List<ClassificacaoChart> listaClassificacaoChart = new ArrayList<>();
        ClassificacaoChart classificacaoChart = new ClassificacaoChart();
       
        classificacaoChart.setDataCadastro(this.lstClassificao.get(0).getDataCadastro());
        LocalDateTime dataCadastro = this.lstClassificao.get(0).getDataCadastro();
        
        for (Classificacao classificacao : this.lstClassificao) {  	
        	
        	if(classificacao.getDataCadastro().getDayOfMonth() != dataCadastro.getDayOfMonth()
        			|| classificacao.getDataCadastro().getMonthValue() != dataCadastro.getMonthValue()) {
        		
        		dataCadastro = classificacao.getDataCadastro();
        		listaClassificacaoChart.add(classificacaoChart);
        		
        		atualizarValorMaximoEixoY(classificacaoChart);
        		
        		classificacaoChart = new ClassificacaoChart();
        		classificacaoChart.setDataCadastro(dataCadastro);
        		
        	}
        	
        	if(classificacao.getSentimento().equals(1)) {
        		classificacaoChart.getPositivos().add(classificacao);
    		} else if(classificacao.getSentimento().equals(-1)) {
    			classificacaoChart.getNegativos().add(classificacao);
    		} else {
    			classificacaoChart.getNaoSeAplicam().add(classificacao);
    		}
        	
		}
		return listaClassificacaoChart;
	}

	private void atualizarValorMaximoEixoY(ClassificacaoChart classificacaoChart) {
		if(classificacaoChart.getPositivos().size() > this.valorMaximoEixoY) {
			this.valorMaximoEixoY = classificacaoChart.getPositivos().size();
		} 
		
		if(classificacaoChart.getNegativos().size() > this.valorMaximoEixoY) {
			this.valorMaximoEixoY = classificacaoChart.getNegativos().size();
		}
		
		if(classificacaoChart.getNaoSeAplicam().size() > this.valorMaximoEixoY) {
			this.valorMaximoEixoY = classificacaoChart.getNaoSeAplicam().size();
		}
	}
	
	public void selecionarCliente() {
		this.lstTopico = carregarTopicosPorCliente();
	}
	
	private List<Topico> carregarTopicosPorCliente() {
		List<Topico> lstTopico = new ArrayList<Topico>();
		
		TopicoDAO topicoDAO = new TopicoDAO(entityManager);
		lstTopico = topicoDAO.findbyCliente(this.cliente);
			
		return lstTopico;
	}
	
	public void buscarClassificacao() {
	
		ClassificacaoDAO classificacaoDAO = new ClassificacaoDAO(entityManager);
		this.lstClassificao = classificacaoDAO.findByRangeOfDataCadastro(
				this.cliente, this.topico, this.dataInicioClassificacao, this.dataFimClassificacao);
		
		if(this.lstClassificao != null && this.lstClassificao.size() > 0) {
			createBarModel();
		}
	}
	
	public void chartItemSelect(ItemSelectEvent event) {
		BarChartModel cModel = (BarChartModel) ((org.primefaces.component.chart.Chart) event.getSource()).getModel();
		 ChartSeries mySeries = cModel.getSeries().get(event.getSeriesIndex());
	
		 Set<Entry<Object, Number>> mapValues = mySeries.getData().entrySet();
	
		 Entry<Object,Number>[] test = new Entry[mapValues.size()];
		 mapValues.toArray(test);
		 
		 String dataCadastroTexto = (String) test[event.getItemIndex()].getKey();
		 DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		 
		 LocalDate dataCadastro = LocalDate.parse(dataCadastroTexto, pattern);
		 
		 Integer sentimento = 0;
		 
		 if(RelatorioClassificacaoEnum.SENTIMENTO_POSITIVO.getChartLabel().equals(mySeries.getLabel())) {
			 sentimento = RelatorioClassificacaoEnum.SENTIMENTO_POSITIVO.getCodigo();
		 }
		 
		 if(RelatorioClassificacaoEnum.SENTIMENTO_NEGATIVO.getChartLabel().equals(mySeries.getLabel())) {
			 sentimento = RelatorioClassificacaoEnum.SENTIMENTO_NEGATIVO.getCodigo();
		 }
		 
		 ClassificacaoDAO classificacaoDAO = new ClassificacaoDAO(entityManager);
		 
		 this.lstClassificaoChart = classificacaoDAO.findByDataCadastroESentimento(dataCadastro, sentimento);
    }
}
