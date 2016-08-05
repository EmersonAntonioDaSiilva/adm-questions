package br.com.afirmanet.questions.manager.application.additional;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import br.com.afirmanet.core.manager.AbstractManager;
import br.com.afirmanet.core.producer.ApplicationManaged;
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
        yAxis.setMax(this.valorMaximoEixoY * 2);
    }
    
    private BarChartModel initBarModel() {
        BarChartModel model = new BarChartModel();
        
        List<ClassificacaoChart> listaClassificacaoChart = listarClassificacaoChart();

        ChartSeries positivos = new ChartSeries();
        positivos.setLabel("Positivo");
        
        for (ClassificacaoChart positivo : listaClassificacaoChart) {
        	positivos.set(positivo.dataCadastroFormatada(), positivo.getPositivos().size());
		}
 
        ChartSeries negativos = new ChartSeries();
        negativos.setLabel("Negativo");
        
        for (ClassificacaoChart negativo : listaClassificacaoChart) {
        	negativos.set(negativo.dataCadastroFormatada(), negativo.getNegativos().size());
		}
        
        ChartSeries naoSeAplicam = new ChartSeries();
        naoSeAplicam.setLabel("Não se Aplica");
        
        for (ClassificacaoChart naoSeAplica : listaClassificacaoChart) {
        	naoSeAplicam.set(naoSeAplica.dataCadastroFormatada(), naoSeAplica.getNaoSeAplicam().size());
		}
        
        model.addSeries(positivos);
        model.addSeries(negativos);
        model.addSeries(naoSeAplicam);
         
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
		System.out.println(dataInicioClassificacao);
		System.out.println(dataFimClassificacao);
		
		ClassificacaoDAO classificacaoDAO = new ClassificacaoDAO(entityManager);
		this.lstClassificao = classificacaoDAO.findByRangeOfDataCadastro(
				this.cliente, this.topico, this.dataInicioClassificacao, this.dataFimClassificacao);
		
		if(this.lstClassificao != null && this.lstClassificao.size() > 0) {
			createBarModel();
		}
	}
}
