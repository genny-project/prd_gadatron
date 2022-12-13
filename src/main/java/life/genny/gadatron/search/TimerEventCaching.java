package life.genny.gadatron.search;

import java.lang.invoke.MethodHandles;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import life.genny.qwandaq.entity.Definition;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import life.genny.qwandaq.Answer;
import life.genny.qwandaq.entity.BaseEntity;
import life.genny.qwandaq.models.AttributeCodeValueString;
import life.genny.qwandaq.models.ServiceToken;
import life.genny.qwandaq.utils.BaseEntityUtils;
import life.genny.qwandaq.utils.CacheUtils;
import life.genny.qwandaq.utils.DefUtils;
import life.genny.qwandaq.utils.QwandaUtils;

@ApplicationScoped
public class TimerEventCaching {

	static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass());

	static Definition timerEventDef = null;

	static final String QUE_ADD_APPLICATION_GRP = "QUE_ADD_APPLICATION_GRP";

	static final Integer HOURS_0 = 0;
	static final Integer HOURS_24 = 24 * 60 * 60;
	static final Integer HOURS_36 = 36 * 60 * 60;
	static final Integer HOURS_48 = 48 * 60 * 60;

	@Inject
	BaseEntityUtils beUtils;

	@Inject
	DefUtils defUtils;

	@Inject
	QwandaUtils qwandaUtils;

	@Inject
	ServiceToken serviceToken;

	// MILESTONES

	// @Transactional
	public void init(final String productCode) {
		log.info("=========================construct TimerEvents =========================");

		timerEventDef = beUtils.getDefinition("DEF_TIMER_EVENT");

		BaseEntity personDef = beUtils.getBaseEntityByCode(productCode, "DEF_PERSON");

		// Application
		createTimerEventBE(personDef,
				QUE_ADD_APPLICATION_GRP, "PERSON_START",
				"Person Start", HOURS_0,
				new AttributeCodeValueString("PRI_PQ_STAGE", "GREEN"),
				new AttributeCodeValueString("PRI_STATUS", "PENDING"));

		createTimerEventBE(personDef,
				QUE_ADD_APPLICATION_GRP, "PERSON_WARNING",
				"Person Warning", HOURS_24,
				new AttributeCodeValueString("PRI_PQ_STAGE", "ORANGE"));

		createTimerEventBE(personDef,
				QUE_ADD_APPLICATION_GRP, "PERSON_DANGER",
				"Person Danger", HOURS_48,
				new AttributeCodeValueString("PRI_PQ_STAGE", "RED"));
	}

	/**
	 * Get TimerEventBE for parameters
	 * 
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return TimerEvent base entity
	 */
	public BaseEntity createTimerEventBE(BaseEntity coreDefBe, String questionCode, String milestoneCode, String name,
			Integer minutes, AttributeCodeValueString... attributeCodeValues) {

		// If in dev mode then divide time by 12 (hours) * 60 (minutes)

		minutes = minutes / (12 * 60);

		String prefix = timerEventDef.getValueAsString("PRI_PREFIX");
		String beCode = milestoneCode;
		if (!beCode.startsWith(prefix)) {
			beCode = prefix + "_" + beCode;
		}
		log.info("Creating TimerEventBE = " + beCode + ":" + name);
		BaseEntity newBe = beUtils.create(timerEventDef, name, beCode);
		newBe.setRealm(coreDefBe.getRealm());

		newBe.addAnswer(new Answer(newBe, newBe, qwandaUtils.getAttribute("PRI_NAME"), name));
		newBe.addAnswer(new Answer(newBe, newBe, qwandaUtils.getAttribute("PRI_CODE"), beCode));
		newBe.addAnswer(
				new Answer(newBe, newBe, qwandaUtils.getAttribute("LNK_QUESTION"), "[\"" + questionCode + "\"]"));
		newBe.addAnswer(new Answer(newBe, newBe, qwandaUtils.getAttribute("PRI_MILESTONE"), milestoneCode));
		newBe.addAnswer(new Answer(newBe, newBe, qwandaUtils.getAttribute("PRI_MINUTES"), "" + minutes));

		String concatAttributeValueString = "";
		for (AttributeCodeValueString acvs : attributeCodeValues) { // too hard to use a Array.stream(...)
			// Confirm that the coreEntityDef will accept the attributeCode
			if (defUtils.attributeValueValidForDEF(coreDefBe, acvs)) {
				concatAttributeValueString += (acvs.getAttributeCode() + ":" + acvs.getValue() + ",");
			}
		}
		if (!StringUtils.isBlank(concatAttributeValueString)) {
			concatAttributeValueString = concatAttributeValueString.substring(0,
					concatAttributeValueString.length() - 1);
			concatAttributeValueString = "["
					+ concatAttributeValueString
					+ "]";
		} else {
			concatAttributeValueString = "[]";
		}

		newBe.addAnswer(new Answer(newBe, newBe, qwandaUtils.getAttribute("PRI_ATTRIBUTECODE_VALUES"),
				concatAttributeValueString));

		beUtils.updateBaseEntity(newBe);

		CacheUtils.putObject(coreDefBe.getRealm(), newBe.getCode(), newBe);

		return newBe;
	}

}
