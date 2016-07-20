package br.com.afirmanet.core.manager;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.primefaces.component.datatable.DataTable;

import br.com.afirmanet.core.enumeration.CrudActionEnum;
import br.com.afirmanet.core.enumeration.SearchRestrictionEnum;
import br.com.afirmanet.core.exception.SystemException;
import br.com.afirmanet.core.faces.primefaces.model.LazyEntityModel;
import br.com.afirmanet.core.interceptor.Message;
import br.com.afirmanet.core.persistence.GenericDAO;
import br.com.afirmanet.core.producer.ApplicationManaged;
import br.com.afirmanet.core.util.ApplicationPropertiesUtils;

@Slf4j
@Getter
public class GenericSearch<T, ID extends Serializable, D extends GenericDAO<?, ?>> extends AbstractManager {

	@Inject
	@ApplicationManaged
	protected EntityManager entityManager;

	protected Class<T> entityClass;
	@Setter
	protected T entity;
	protected T searchParam;
	protected T[] selectedEntities;
	protected LazyEntityModel<T, ID> entityList;
	protected Class<D> genericDAOClass;
	protected GenericDAO<T, ID> genericDAO;
	protected Map<ID, T> expandedEntities = new HashMap<>();

	// Configurações
	protected CrudActionEnum currentAction; // A ação corrente da página, ou seja, qual dos formulário está sendo exibido para o usuário.
	protected SearchRestrictionEnum defaultRestriction = SearchRestrictionEnum.valueOf(ApplicationPropertiesUtils.getValue("defaultRestriction", SearchRestrictionEnum.ILIKE.toString()));
	protected boolean showSeparator = ApplicationPropertiesUtils.getValueAsBoolean("showSeparator", "true");
	protected boolean showDetailButton = ApplicationPropertiesUtils.getValueAsBoolean("showDetailButton", "true");
	protected boolean showSearchButton = ApplicationPropertiesUtils.getValueAsBoolean("showSearchButton", "true");
	protected boolean showResetButton = ApplicationPropertiesUtils.getValueAsBoolean("showResetButton", "true");
	protected boolean showActionColumn = ApplicationPropertiesUtils.getValueAsBoolean("showActionColumn", "true");
	protected boolean showFormSearchFilter = ApplicationPropertiesUtils.getValueAsBoolean("showFormSearchFilter", "true");
	protected boolean showTopDataScroller = ApplicationPropertiesUtils.getValueAsBoolean("showTopDataScroller", "false");
	protected boolean showSelectionColumn = ApplicationPropertiesUtils.getValueAsBoolean("showSelectionColumn", "false");
	protected boolean showRefreshButton = ApplicationPropertiesUtils.getValueAsBoolean("showRefreshButton", "false");
	protected boolean showSubTable = ApplicationPropertiesUtils.getValueAsBoolean("showSubTable", "false");
	protected boolean singleSelection = ApplicationPropertiesUtils.getValueAsBoolean("isSingleSelection", "true");
	@Setter
	protected Integer refreshInterval = ApplicationPropertiesUtils.getValueAsInteger("refreshInterval", "60");

	@SuppressWarnings("unchecked")
	public GenericSearch() { //NOPMD
		Type type = getClass().getGenericSuperclass();

		if (type instanceof ParameterizedType) {
			entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
			genericDAOClass = (Class<D>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[2];
		} else {
			Type genericType = ((Class<?>) type).getGenericSuperclass();
			entityClass = (Class<T>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
			genericDAOClass = (Class<D>) ((ParameterizedType) genericType).getActualTypeArguments()[2];
		}

		createSearchParam(); //NOPMD
	}

	/**
	 * <p>
	 * método executado logo após a chamada do construtor. Este método é muito utilizado caso deseja-se executar alguma
	 * ação após a injeção de dependência, pois a injeção de dependência só é aceitável após a instância da classe em
	 * questão.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void postConstruct() {
		try {
			genericDAO = (GenericDAO<T, ID>) genericDAOClass.newInstance();
			genericDAO.setEntityManager(entityManager);
			entity = entityClass.newInstance();
			currentAction = CrudActionEnum.SEARCH;

			init();

		} catch (InstantiationException | IllegalAccessException e) {
			throw new SystemException(e);
		}
	}

	/**
	 * <p>
	 * método chamado após o <code>public void postConstruct()</code>, sendo utilizado para extender as funcionalidades
	 * já implementadas pelo método citado.
	 * </p>
	 */
	public void init() {
		// Basta implementar o que vc precisa
	}

	/*------------------------------------------------------------------------
	 * SEARCH
	 *-----------------------------------------------------------------------*/

	/**
	 * <p>
	 * Executa a consulta de acordo com os Parâmetros informados no objeto instanciado pelo método
	 * <code>public void createSearchParam()</code>, populando o objeto <code>
	 * entityList</code> utilizado para popular um componente <code><datatable /></code>.
	 * </p>
	 * <p>
	 * <b>NOTA:</b> Dê uma olhada nos seguintes métodos: protected void validateSearch(Object) e public void
	 * beforeSearch()
	 * </p>
	 *
	 */
	@Message
	public void search() {
		if (validateSearchParam()) {
			beforeSearch();
			executeSearch();
			currentAction = CrudActionEnum.SEARCH;
			afterSearch();
		}
	}

	/**
	 * <p>
	 * Valida os Parâmetros de pesquisa representado pelo objeto passado por Parâmetro.
	 * </p>
	 *
	 * @param searchParam
	 *        Objeto que representa os Parâmetros de pesquisa.
	 * @return True para validação realizada com sucesso, caso contrário retorna false.
	 */
	protected boolean validateSearchParam() {
		return true;
	}

	/**
	 * <p>
	 * método executado antes do método <code>public void search()</code>, permitindo executar alguma implementação
	 * antes de executar a pesquisa no banco de dados.
	 * </p>
	 *
	 * @see GenericSearch#validateSearchParam(Object)
	 * @see GenericSearch#afterSearch()
	 */
	protected void beforeSearch() {
		// Basta implementar o que vc precisa
	}

	/**
	 * <p>
	 * método executado após o método <code>public void search()</code>, permitindo executar alguma implementação após
	 * executar a pesquisa no banco de dados.
	 * </p>
	 *
	 * @see GenericSearch#validateSearchParam(Object)
	 * @see GenericSearch#beforeSearch()
	 */
	protected void afterSearch() {
		// Basta implementar o que vc precisa
	}

	protected void executeSearch() {
		genericDAO.setSearchRestriction(getDefaultRestriction());
		entityList = new LazyEntityModel<>(genericDAO, searchParam);
	}

	/**
	 * <p>
	 * Cria a instância do objeto responsável pelo mapeamento dos parâmetros de consulta.
	 * </p>
	 */
	protected void createSearchParam() {
		try {
			searchParam = entityClass.newInstance();

		} catch (InstantiationException e) {
			log.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * <p>
	 * Verifica se a ação corrente da página é de pesquisa, ou seja, se o formulário exibido na página é o de pesquisa.
	 * </p>
	 *
	 * @return True caso o formulário exibido na página é o de pesquisa, caso contrário retorna false.
	 */
	public boolean isSearchAction() {
		return currentAction == null || currentAction == CrudActionEnum.SEARCH;
	}

	public void setSelectedEntities(T[] selectedEntities) {
		if (selectedEntities != null) {
			this.selectedEntities = selectedEntities;
		}
	}

	public void expandEntity(T entity) {
		if (isEntityExpanded(entity)) {
			expandedEntities.remove(genericDAO.getId(entity));
		} else {
			subSearch(entity);
			expandedEntities.put(genericDAO.getId(entity), entity);
		}
	}

	public boolean isEntityExpanded(T entity) {
		return expandedEntities.containsKey(genericDAO.getId(entity));
	}

	/*------------------------------------------------------------------------
	 * SUB-SEARCH
	 *-----------------------------------------------------------------------*/

	@Message
	public void subSearch(T entity) {
		// Basta implementar o que vc precisa
	}

	/*------------------------------------------------------------------------
	 * DETAIL
	 *-----------------------------------------------------------------------*/

	/**
	 * <p>
	 * Exibe os detalhes da entidade passada por Parâmetro.
	 * </p>
	 *
	 * @param entity
	 *        Entidade a ser detalhada.
	 */
	@Message
	public void detail(T entity) {
		this.entity = entity;

		beforeDetail();
		executeDetail();
		currentAction = CrudActionEnum.DETAIL;
		afterDetail();
	}

	/**
	 * <p>
	 * método executado antes do método <code>public void detail(T entity)</code>, permitindo executar alguma validação
	 * ou implementação antes do detalhamento da entidade.
	 * </p>
	 *
	 * @param entity
	 *        Entidade selecionada para detalhar
	 */
	protected void beforeDetail() {
		// Basta implementar o que vc precisa
	}

	/**
	 * <p>
	 * método executado após o método <code>public void detail(T entity)</code>, permitindo executar alguma
	 * implementação após o detalhamento da entidade.
	 * </p>
	 *
	 * @param entity
	 *        Entidade selecionada para detalhar
	 */
	protected void afterDetail() {
		// Basta implementar o que vc precisa
	}

	protected void executeDetail() {
		// Basta implementar o que vc precisa
	}

	/**
	 * <p>
	 * Verifica se a ação corrente da página é de detalhamento da entidade, ou seja, se o formulário exibido na página é
	 * o de detalhamento.
	 * </p>
	 *
	 * @return True caso o formulário exibido na página é o de detalhamento, caso contrário retorna false.
	 */
	public boolean isDetailAction() {
		return currentAction == CrudActionEnum.DETAIL;
	}

	/*------------------------------------------------------------------------
	 * RESET
	 *-----------------------------------------------------------------------*/

	/**
	 * <p>
	 * Limpa os campos do formulário.
	 * </p>
	 */
	@Message
	public void reset() {
		executeReset();
		currentAction = CrudActionEnum.SEARCH;
	}

	protected void executeReset() {
		// Master
		entity = (T) null;
		entityList = null;
		selectedEntities = null;
		expandedEntities = new HashMap<>();
		createSearchParam();

		refreshInterval = ApplicationPropertiesUtils.getValueAsInteger("refreshInterval", "60");

		DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:searchResult");
		if (dataTable != null) {
			dataTable.reset();
		}
	}

	/*------------------------------------------------------------------------
	 * DONE
	 *-----------------------------------------------------------------------*/

	/**
	 * <p>
	 * Volta para página de pesquisa.
	 * </p>
	 */
	public void done() {
		entity = (T) null;
		currentAction = CrudActionEnum.SEARCH;
	}

}
