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
import life.genny.qwandaq.utils.DatabaseUtils;

import life.genny.qwandaq.models.ServiceToken;
import io.quarkus.arc.Arc;

@ApplicationScoped
public class PersonService {

	static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass());

	static Jsonb jsonb = JsonbBuilder.create();

	static final String productCode = "gadatron";

	@Inject
	UserToken userToken;

	@Inject
	QwandaUtils qwandaUtils;

	@Inject
	BaseEntityUtils beUtils;

	@Inject
	SearchUtils searchUtils;

	@Inject
	ServiceToken serviceToken;

	@Inject
	DatabaseUtils databaseUtils;


	/**
	 * setup the Person with initial data.
	 *
	 * @param code       The targeted Person BaseEntity
	 * @param companyCode The associated company 
	 * @return true if successful
	 */
	public Boolean setup(String code, String companyCode) {
		log.info("=========================initialise Person=========================");
		log.info("Source Code = " + userToken.getUserCode());
		log.info("Person Code = " + code);
		log.info("Company Code = " + companyCode);

		BaseEntity person = beUtils.getBaseEntityOrNull(code);

		BaseEntity company = beUtils.getBaseEntityOrNull(companyCode);

		if (person == null) {
			log.error("Person not found");
			return false;
		}

		if (company == null) {
			log.error("Company not found");
			return false;
		}

		// set core attributes
		person = beUtils.addValue(person, "PRI_CODE", code);
		person = beUtils.addValue(person, "LNK_COMPANY", companyCode);
		person = beUtils.addValue(person, "PRI_NAME", company.getName());

		person = beUtils.addValue(person, "PRI_STAGE", "BUCKET1");
		person = beUtils.addValue(person, "PRI_STATUS", "PENDING");
		person = beUtils.addValue(person, "PRI_STATUS_COLOR", "#00FF00");

		// set compatible attributes that could be replaced with using LNKs
		person = beUtils.addValue(person, "PRI_IMAGE_URL", company.getValueAsString("PRI_IMAGE_URL"));

		person = beUtils.addValue(person, "PRI_APPLIED_BY", userToken.getUserCode());

		beUtils.updateBaseEntity(person);

		return true;
	}

	/**
	 * setup the Person with active data.
	 *
	 * @param personCode The targeted BaseEntity
	 * @return true if successful
	 */
	public Boolean activate(String code) {
		log.info("=========================activate Person=========================");
		log.info("Source Code = " + userToken.getUserCode());
		log.info("Person Code = " + code);

		BaseEntity person = beUtils.getBaseEntityOrNull(code);

		if (person == null) {
			log.error("Person not found");
			return false;
		}

		String companyCode = person.getValueAsString("LNK_COMPANY");
		companyCode = companyCode.substring(1, companyCode.length() - 1);
		companyCode = companyCode.substring(1, companyCode.length() - 1);
		log.info("Company Code = " + companyCode);
		BaseEntity company = beUtils.getBaseEntityFromLinkAttribute(person, "LNK_COMPANY");

		if (company == null) {
			log.error("Company not found");
			return false;
		}

		person = beUtils.addValue(person, "PRI_STAGE", "BUCKET1");
		person = beUtils.addValue(person, "PRI_STATUS", "ACTIVE");
		person = beUtils.addValue(person, "PRI_STATUS_COLOR", "#5CB85C");
		person = beUtils.addValue(person, "PRI_TITLE", company.getName());

		person.setStatus(EEntityStatus.ACTIVE);

		beUtils.updateBaseEntity(person);

		return true;
	}

	/**
	 * bucket2 the Person with active data.
	 *
	 * @param personCode The targeted BaseEntity
	 * @return true if successful
	 */
	public Boolean bucket2(String code) {
		log.info("=========================bucket2 Person=========================");
		log.info("Source Code = " + userToken.getUserCode());
		log.info("Person Code = " + code);

		BaseEntity person = beUtils.getBaseEntityOrNull(code);

		if (person == null) {
			log.error("Person not found");
			return false;
		}

		person = beUtils.addValue(person, "PRI_STAGE", "BUCKET2");
		beUtils.updateBaseEntity(person);

		return true;
	}

	/**
	 * Bucket3 the Person with active data.
	 *
	 * @param personCode The targeted BaseEntity
	 * @return true if successful
	 */
	public Boolean bucket3(String code) {
		log.info("=========================bucket3 Person=========================");
		log.info("Source Code = " + userToken.getUserCode());
		log.info("Person Code = " + code);

		BaseEntity person = beUtils.getBaseEntityOrNull(code);

		if (person == null) {
			log.error("Person not found");
			return false;
		}

		person = beUtils.addValue(person, "PRI_STAGE", "BUCKET3");
		beUtils.updateBaseEntity(person);

		return true;
	}




	/**
	 * Archive the Person with active data.
	 *
	 * @param personCode The targeted BaseEntity
	 * @return true if successful
	 */
	public Boolean archive(String code) {
		log.info("=========================Archive Person=========================");
		log.info("Source Code = " + userToken.getUserCode());
		log.info("Person Code = " + code);

		beUtils = new BaseEntityUtils(serviceToken); // assume userToken dead.
		BaseEntity person = beUtils.getBaseEntityOrNull(code);

		if (person == null) {
			log.error("Person not found");
			return false;
		}

		person = beUtils.addValue(person, "PRI_STAGE", "ARCHIVED");
		person.setStatus(EEntityStatus.ARCHIVED);
		beUtils.updateBaseEntity(person);

		return true;
	}

	/**
	 * Reject the Person with active data.
	 *
	 * @param personCode The targeted BaseEntity
	 * @return true if successful
	 */
	public Boolean reject(String code) {
		log.info("=========================Reject Person=========================");
		log.info("Source Code = " + userToken.getUserCode());
		log.info("Person Code = " + code);

		BaseEntity person = beUtils.getBaseEntityOrNull(code);

		if (person == null) {
			log.error("Person not found");
			return false;
		}

		person = beUtils.addValue(person, "PRI_STATUS", "REJECTED");
		// person = beUtils.addValue(person, "PRI_IS_DISABLED", "TRUE");
		beUtils.updateBaseEntity(person);

		return true;
	}

	/**
	 * Withdraw the Person with active data.
	 *
	 * @param personCode The targeted BaseEntity
	 * @return true if successful
	 */
	public Boolean withdraw(String code) {
		log.info("=========================Withdraw Person=========================");
		log.info("Source Code = " + userToken.getUserCode());
		log.info("Person Code = " + code);

		BaseEntity person = beUtils.getBaseEntityOrNull(code);

		if (person == null) {
			log.error("Person not found");
			return false;
		}

		person = beUtils.addValue(person, "PRI_STATUS", "WITHDRAWN");
		// person = beUtils.addValue(person, "PRI_IS_DISABLED", "TRUE");
		beUtils.updateBaseEntity(person);

		return true;
	}

	/**
	 * Cancel the Person with active data.
	 *
	 * @param personCode The targeted BaseEntity
	 * @return true if successful
	 */
	public Boolean cancel(String code) {
		log.info("=========================Cancel Person=========================");
		log.info("Source Code = " + userToken.getUserCode());
		log.info("Person Code = " + code);

		beUtils = new BaseEntityUtils(serviceToken); // assume userToken dead.
		BaseEntity person = beUtils.getBaseEntityOrNull(code);

		if (person == null) {
			log.error("Person not found");
			return false;
		}
		person.setStatus(EEntityStatus.DELETED);
		beUtils.updateBaseEntity(person);

		return true;
	}

	/**
	 * Expiredthe Person with active data.
	 *
	 * @param personCode The targeted BaseEntity
	 * @return true if successful
	 */
	public Boolean expired(String code) {
		log.info("=========================CExpired Person=========================");
		log.info("Source Code = " + userToken.getUserCode());
		log.info("Person Code = " + code);

		beUtils = new BaseEntityUtils(serviceToken); // assume userToken dead.
		BaseEntity person = beUtils.getBaseEntityOrNull(code);

		if (person == null) {
			log.error("Person not found");
			return false;
		}
		person.setStatus(EEntityStatus.DELETED);
		beUtils.updateBaseEntity(person);

		return true;
	}

	/**
	 * Abort the Person with active data.
	 *
	 * @param personCode The targeted BaseEntity
	 * @return true if successful
	 */
	public Boolean abort(String code) {
		log.info("=========================Abort Person=========================");
		log.info("Source Code = " + userToken.getUserCode());
		log.info("Person Code = " + code);
		beUtils = new BaseEntityUtils(serviceToken); // assume userToken dead.

		// activate request scope and fetch UserToken
		//Arc.container().requestContext().activate();
		String productCode = "gadatron"; // This is the gadatron service after all...
		log.info("database lookup is for "+productCode+" and code "+code);
		BaseEntity person = databaseUtils.findBaseEntityByCode(productCode,code);    //beUtils.getBaseEntityOrNull(code);

		if (person == null) {
			log.error("Person not found");
			return false;
		}
		person.setStatus(EEntityStatus.DELETED);
		databaseUtils.saveBaseEntity(person);

		return true;
	}
}
