package life.genny.gadatron.setupBaseEntitys;

import java.lang.invoke.MethodHandles;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.jboss.logging.Logger;

import life.genny.qwandaq.EEntityStatus;
import life.genny.qwandaq.entity.BaseEntity;
import life.genny.qwandaq.models.UserToken;
import life.genny.qwandaq.utils.BaseEntityUtils;
import life.genny.qwandaq.utils.QwandaUtils;
import life.genny.qwandaq.utils.SearchUtils;

@ApplicationScoped
public class DocumentService {

	static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass());
	static final String entityType = "Document";

	static Jsonb jsonb = JsonbBuilder.create();

	@Inject
	UserToken userToken;

	@Inject
	QwandaUtils qwandaUtils;

	@Inject
	BaseEntityUtils beUtils;

	@Inject
	SearchUtils searchUtils;

	/**
	 * setup the Application with initial data.
	 *
	 * @param code       The targeted Document BaseEntity
	 * @param coreBeCode The associated core BaseEntity
	 * @return true if successful
	 */
	public Boolean setup(String code) {
		log.info("=========================initialise " + entityType + "=========================");
		log.info("Source Code = " + userToken.getUserCode());
		log.info(entityType + " Code = " + code);

		BaseEntity document = beUtils.getBaseEntityOrNull(code);

		if (document == null) {
			log.error("Document not found");
			return false;
		}

		// set core attributes
		document = beUtils.addValue(document, "PRI_CODE", code);
		beUtils.updateBaseEntity(document);

		return true;
	}

	/**
	 * setup the Document with active data.
	 *
	 * @param code The targeted BaseEntity
	 * @return true if successful
	 */
	public Boolean activate(String code) {
		log.info("=========================activate " + entityType + "=========================");
		log.info("Source Code = " + userToken.getUserCode());
		log.info(entityType + " Code = " + code);

		BaseEntity entity = beUtils.getBaseEntityOrNull(code);

		if (entity == null) {
			log.error(entityType + " not found");
			return false;
		}

		BaseEntity coreBe = beUtils.getBaseEntityFromLinkAttribute(entity, "LNK_COREBE");

		if (coreBe == null) {
			log.error("CoreBE not found");
			return false;
		}

		entity.setStatus(EEntityStatus.ACTIVE);

		beUtils.updateBaseEntity(entity);

		return true;
	}

	/**
	 * Archive the Document with active data.
	 *
	 * @param code The targeted BaseEntity
	 * @return true if successful
	 */
	public Boolean archive(String code) {
		log.info("=========================Archive " + entityType + "=========================");
		log.info("Source Code = " + userToken.getUserCode());
		log.info(entityType + " Code = " + code);

		BaseEntity entity = beUtils.getBaseEntityOrNull(code);

		if (entity == null) {
			log.error(entityType + " not found");
			return false;
		}

		entity.setStatus(EEntityStatus.ARCHIVED);
		beUtils.updateBaseEntity(entity);

		return true;
	}

	/**
	 * Cancel the Document with active data.
	 *
	 * @param code The targeted BaseEntity
	 * @return true if successful
	 */
	public Boolean cancel(String code) {
		log.info("=========================Cancel " + entityType + "=========================");
		log.info("Source Code = " + userToken.getUserCode());
		log.info(entityType + " Code = " + code);

		BaseEntity entity = beUtils.getBaseEntityOrNull(code);

		if (entity == null) {
			log.error(entityType + " not found");
			return false;
		}
		entity.setStatus(EEntityStatus.DELETED);
		beUtils.updateBaseEntity(entity);

		return true;
	}
}