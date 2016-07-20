package br.com.afirmanet.core.manager;

import java.io.Serializable;

import javax.transaction.Transactional;

import lombok.Getter;
import br.com.afirmanet.core.enumeration.CrudActionEnum;
import br.com.afirmanet.core.exception.SystemException;
import br.com.afirmanet.core.interceptor.Message;
import br.com.afirmanet.core.persistence.GenericDAO;
import br.com.afirmanet.core.util.ApplicationPropertiesUtils;
import br.com.afirmanet.questions.enums.AcaoEnum;

@Getter
public class GenericCRUD<T, ID extends Serializable, D extends GenericDAO<?, ?>> extends GenericSearch<T, ID, D> {

	protected boolean showInsertButton = ApplicationPropertiesUtils.getValueAsBoolean("showInsertButton", "true");
	protected boolean showEditButton = ApplicationPropertiesUtils.getValueAsBoolean("showEditButton", "true");
	protected boolean showDeleteButton = ApplicationPropertiesUtils.getValueAsBoolean("showDeleteButton", "true");

	/*------------------------------------------------------------------------
	 * INSERT / SAVE
	 *-----------------------------------------------------------------------*/

	protected void beforePrepareInsert() {
		// Basta implementar o que vc precisa
	}

	@Message
	public void prepareInsert() {
		try {
			entity = entityClass.newInstance();
			beforePrepareInsert();
			currentAction = CrudActionEnum.EDIT;
			afterPrepareInsert();

		} catch (InstantiationException | IllegalAccessException e) {
			throw new SystemException(e);
		}
	}

	protected void afterPrepareInsert() {
		// Basta implementar o que vc precisa
	}

	@Message
	@Transactional
	public void save() {
		if (validateSave()) {
			entityManager.joinTransaction();

			beforeSave();
			gravarHistorico(AcaoEnum.INCLUIR);
			executeSave();
			afterSave();

			addInfoMessageFromResourceBundle("insert.success");
			currentAction = CrudActionEnum.DETAIL;
		}
	}

	public void executeSave() {
		genericDAO.save(entity);
	}

	protected boolean validateSave() {
		return true;
	}

	protected void beforeSave() {
		// Basta implementar o que vc precisa
	}

	protected void afterSave() {
		// Basta implementar o que vc precisa
	}

	/*------------------------------------------------------------------------
	 * EDIT / UPDATE
	 *-----------------------------------------------------------------------*/

	@Message
	public void prepareEdit(T entity) {
		this.entity = entity;
		beforePrepareEdit();
		currentAction = CrudActionEnum.EDIT;
		afterPrepareEdit();
	}

	protected void beforePrepareEdit() {
		// Basta implementar o que vc precisa
	}

	protected void afterPrepareEdit() {
		// Basta implementar o que vc precisa
	}

	@Message
	@Transactional
	public void update() {
		if (validateUpdate()) {
			entityManager.joinTransaction();

			beforeUpdate();
			gravarHistorico(AcaoEnum.ALTERAR);
			executeUpdate();
			afterUpdate();

			addInfoMessageFromResourceBundle("update.success");
			currentAction = CrudActionEnum.DETAIL;
		}
	}

	public void executeUpdate() {
		genericDAO.update(entity);
	}

	protected boolean validateUpdate() {
		return true;
	}

	protected void beforeUpdate() {
		// Basta implementar o que vc precisa
	}

	protected void afterUpdate() {
		// Basta implementar o que vc precisa
	}

	/*------------------------------------------------------------------------
	 * DELETE
	 *-----------------------------------------------------------------------*/

	@Message
	@Transactional
	public void delete() {
		entityManager.joinTransaction();

		beforeDelete();
		gravarHistorico(AcaoEnum.EXCLUIR);
		executeDelete();
		afterDelete();

		addInfoMessageFromResourceBundle("delete.success");
		currentAction = CrudActionEnum.SEARCH;
	}

	public void executeDelete() {
		genericDAO.deleteById(genericDAO.getId(entity));
	}

	@Message
	@Transactional
	public void delete(T entity) {
		this.entity = entity;
		delete();
	}

	protected void beforeDelete() {
		// Basta implementar o que vc precisa
	}

	protected void afterDelete() {
		// Basta implementar o que vc precisa
	}

	protected void gravarHistorico(AcaoEnum acao) {
		// Basta implementar o que vc precisa
	}
	/*------------------------------------------------------------------------
	 * OTHERS
	 *-----------------------------------------------------------------------*/

	public void cancel() {
		done();
	}

	public boolean isManaged() {
		return genericDAO.getId(entity) != null;
	}

	public boolean isInsertEditAction() {
		return currentAction == CrudActionEnum.EDIT;
	}

	@Override
	public boolean isShowActionColumn() {
		return showInsertButton || showEditButton || showDeleteButton;
	}

}
