package life.genny.gadatron.setupBaseEntitys;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

import life.genny.qwandaq.EEntityStatus;
import life.genny.qwandaq.entity.BaseEntity;
import life.genny.qwandaq.entity.SearchEntity;
import life.genny.qwandaq.models.UserToken;
import life.genny.qwandaq.utils.BaseEntityUtils;
import life.genny.qwandaq.utils.CacheUtils;
import life.genny.qwandaq.utils.QwandaUtils;
import life.genny.qwandaq.utils.SearchUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

@ApplicationScoped
public class CompanyService {

	static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass());

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
	 * setup the Company with initial data.
	 *
	 * @param code The targeted Company BaseEntity
	 * @return true if successful
	 */
	public Boolean setup(String code) {
		log.info("=========================initialise Company=========================");
		log.info("Source Code = " + userToken.getUserCode());
		log.info("Company Code = " + code);

		BaseEntity Company = beUtils.getBaseEntityOrNull(code);

		if (Company == null) {
			log.error("Company not found");
			return false;
		}

		// set core attributes
		Company = beUtils.addValue(Company, "PRI_CODE", code);
		Company = beUtils.addValue(Company, "PRI_APPLIED_BY", userToken.getUserCode());

		beUtils.updateBaseEntity(Company);

		return true;
	}

	/**
	 * setup the Company with active data.
	 *
	 * @param code The targeted BaseEntity
	 * @return true if successful
	 */
	public Boolean activate(String code) {
		log.info("=========================activate Company=========================");
		log.info("Source Code = " + userToken.getUserCode());
		log.info("Company Code = " + code);

		BaseEntity entity = beUtils.getBaseEntityOrNull(code);

		if (entity == null) {
			log.error("Company not found");
			return false;
		}
		entity.setStatus(EEntityStatus.ACTIVE);

		beUtils.updateBaseEntity(entity);

		return true;
	}

	/**
	 * Archive the Company with active data.
	 *
	 * @param code The targeted BaseEntity
	 * @return true if successful
	 */
	public Boolean archive(String code) {
		log.info("=========================Archive Company=========================");
		log.info("Source Code = " + userToken.getUserCode());
		log.info("Company Code = " + code);

		BaseEntity entity = beUtils.getBaseEntityOrNull(code);

		if (entity == null) {
			log.error("Company not found");
			return false;
		}

		entity.setStatus(EEntityStatus.ARCHIVED);
		beUtils.updateBaseEntity(entity);

		return true;
	}

	/**
	 * Cancel the Company with active data.
	 *
	 * @param code The targeted BaseEntity
	 * @return true if successful
	 */
	public Boolean cancel(String code) {
		log.info("=========================Cancel Company Creation=========================");
		log.info("Source Code = " + userToken.getUserCode());
		log.info("Company Code = " + code);

		BaseEntity entity = beUtils.getBaseEntityOrNull(code);

		if (entity == null) {
			log.error("Company not found");
			return false;
		}
		entity.setStatus(EEntityStatus.DELETED);
		beUtils.updateBaseEntity(entity);

		return true;
	}

	/**
	 * Abort the Company creation.
	 *
	 * @param code The targeted BaseEntity
	 * @return true if successful
	 */
	public Boolean abort(String code) {
		log.info("=========================Abort Company Creation=========================");
		log.info("Source Code = " + userToken.getUserCode());
		log.info("Company Code = " + code);

		BaseEntity entity = beUtils.getBaseEntityOrNull(code);

		if (entity == null) {
			log.error("Company not found");
			return false;
		}
		entity.setStatus(EEntityStatus.DELETED);
		beUtils.updateBaseEntity(entity);

		return true;
	}
}