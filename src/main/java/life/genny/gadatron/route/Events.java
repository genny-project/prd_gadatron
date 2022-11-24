package life.genny.gadatron.route;

import com.google.common.reflect.TypeToken;

import life.genny.gadatron.service.AmrizalService;
import life.genny.gadatron.service.BalService;
import life.genny.gadatron.service.WayanService;
import life.genny.kogito.common.utils.KogitoUtils;
import life.genny.qwandaq.Answer;
import life.genny.qwandaq.attribute.Attribute;
import life.genny.qwandaq.entity.BaseEntity;
import life.genny.qwandaq.message.MessageData;
import life.genny.qwandaq.message.QEventMessage;
import life.genny.qwandaq.models.UserToken;
import life.genny.qwandaq.utils.BaseEntityUtils;
import life.genny.qwandaq.utils.QwandaUtils;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.lang.invoke.MethodHandles;
import java.util.Map;

import static life.genny.gadatron.constants.GadatronConstants.PRODUCT_CODE;
import static life.genny.kogito.common.utils.KogitoUtils.UseService.SELF;

/**
 * Events
 */
@ApplicationScoped
public class Events {

	static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass());

	private static final String DEF_BALI = "DEF_BALI_PERSON";

	@Inject
	UserToken userToken;

	@Inject
	KogitoUtils kogitoUtils;

	@Inject
	QwandaUtils qwandaUtils;

	@Inject
	BaseEntityUtils beUtils;

	@Inject
	WayanService wayanService;

	@Inject
	BalService balService;

	@Inject
	AmrizalService amService;

	/**
	 * @param msg
	 */
	public void route(QEventMessage msg) {

		MessageData data = msg.getData();
		String code = data.getCode();

		if (userToken == null) {
			log.error("userToken  is null - possible cause is no user logged in");
		}
		if (kogitoUtils == null) {
			log.error("kogitoUtils is null - possible cause is no userToken");
		}

		// add person
		if ("QUE_ADD_PERSON".equals(code)) {
			JsonObjectBuilder payloadBuilder = Json.createObjectBuilder()
					.add("questionCode", "QUE_PERSON_GRP")
					.add("targetCode", "PER_940F6070-356B-4AF0-99F5-663E2CB5AAA4");

			if (userToken != null) {
				if (userToken.getUserCode() != null) {
					payloadBuilder.add("sourceCode", userToken.getUserCode());
					payloadBuilder.add("userCode", userToken.getUserCode());
				}
			}
			JsonObject payload = payloadBuilder.build();

			kogitoUtils.triggerWorkflow(SELF, "testQuestion", payload);
			return;
		}

		// add admin
		if ("QUE_ADD_ADMIN".equals(code)) {
			JsonObjectBuilder jsonBuilder = Json.createObjectBuilder()
					.add("questionCode", msg.getData().getCode())
					.add("entityCode", msg.getData().getTargetCode());

			if (userToken != null) {
				if (userToken.getUserCode() != null) {
					jsonBuilder.add("userCode", userToken.getUserCode());
				}
			}
			JsonObject json = jsonBuilder.build();

			kogitoUtils.triggerWorkflow(SELF, "adminLifecycle", json);
			return;
		}

		// bucket event
		if ("ACT_PRI_EVENT_BUCKET1".equals(code)) {
			JsonObject json = Json.createObjectBuilder()
					.add("sourceCode", userToken.getUserCode())
					.add("internCode", msg.getData().getTargetCode())
					.build();
			kogitoUtils.triggerWorkflow(SELF, "application", json);
			return;
		}

		// test question event
		if (code.startsWith("TESTQ_QUE_")) {
			log.info("Displaying Test Question Group ..." + msg.getData().getCode() + " msg=" + msg);
			JsonObjectBuilder payloadBuilder = Json.createObjectBuilder()
					.add("questionCode", msg.getData().getCode().substring("TESTQ_".length()))
					.add("entityCode", msg.getData().getTargetCode())
					.add("targetCode", msg.getData().getTargetCode());

			String content = msg.getData().getContent();
			if (content != null) {
				payloadBuilder.add("content", content);

				log.info("Content = " + content);
				/* Load the LNK_DOT */

				BaseEntity target = beUtils.getBaseEntityByCode(PRODUCT_CODE, msg.getData().getTargetCode());
				Attribute lnkDot = qwandaUtils.getAttribute("LNK_DOT");
				target.addAnswer(new Answer(target, target, lnkDot, "[\"" + content + "\"]"));
				beUtils.updateBaseEntity(PRODUCT_CODE, target);
			}

			if (userToken != null) {
				if (userToken.getUserCode() != null) {
					payloadBuilder.add("sourceCode", userToken.getUserCode());
					payloadBuilder.add("userCode", userToken.getUserCode());
				}
			} else {
				log.error("userToken is Null");
			}

			JsonObject payload = payloadBuilder.build();

			log.info("Payload = " + payload.toString());

			if (kogitoUtils != null) {
				kogitoUtils.triggerWorkflow(SELF, "testQuestionGT2", payload);
			} else {
				log.error("kogitoUtils is Null");
			}
		}

		if (code.startsWith("GADA_WAYAN_")) {
			log.info("Displaying GADA_WAYAN_ Test Question Group ..." + msg.getData().getCode() + " msg=" + msg);
			JsonObjectBuilder payloadBuilder = Json.createObjectBuilder()
					.add("questionCode", msg.getData().getCode().substring("GADA_WAYAN_".length()))
					.add("entityCode", msg.getData().getTargetCode())
					.add("targetCode", msg.getData().getTargetCode());
			String content = msg.getData().getContent();

			if (code.endsWith("QUE_USER_PROFILE_GRP")) {
				if (content != null) {
					payloadBuilder.add("content", content);
				}

				JsonObject payload = payloadBuilder.build();
				log.info("Payload = " + payload.toString());

				kogitoUtils.triggerWorkflow(SELF, "testQuestionGT2", payload);
			} else {
				if (content != null) {
					log.info("Content = " + content);

					if (content.startsWith("\"{") && content.endsWith("}\"")) {
						/* Load the LNK_DOT */

						BaseEntity target = beUtils.getBaseEntityByCode(PRODUCT_CODE, msg.getData().getTargetCode());
						Attribute lnkDot = qwandaUtils.getAttribute("LNK_DOT");
						target.addAnswer(new Answer(target, target, lnkDot, "[\"" + content + "\"]"));
						beUtils.updateBaseEntity(PRODUCT_CODE, target);

						Jsonb jsonb = JsonbBuilder.create();
						Map<String, String> mContent = jsonb.fromJson(content, new TypeToken<Map<String, String>>() {
						}.getType());
						String beCode = wayanService.createAPerson(mContent);
						if (beCode != null) {
							log.info("created person is :" + beCode);
							payloadBuilder.remove("targetCode");
							payloadBuilder.remove("entityCode");
							payloadBuilder.add("targetCode", beCode);
							payloadBuilder.add("entityCode", beCode);
						}

						JsonObject payload = payloadBuilder.build();
						log.info("Payload = " + payload.toString());
						kogitoUtils.triggerWorkflow(SELF, "testQuestionGT2", payload);
					} else {
						payloadBuilder.add("content", content);
						JsonObject payload = payloadBuilder.build();
						kogitoUtils.triggerWorkflow(SELF, "testWayan", payload);
					}
				}
			}

		}

		// test question event
		if (code.startsWith("GADA_TAZ_CREATE_PER")) {
			log.info("Creating Taz Person ..." + msg.getData().getCode() + " msg=" + msg);
			JsonObjectBuilder payloadBuilder = Json.createObjectBuilder()
					.add("questionCode", msg.getData().getCode().substring("GADA_TAZ_CREATE_PER".length()))
					.add("entityCode", msg.getData().getTargetCode())
					.add("targetCode", msg.getData().getTargetCode());

			String content = msg.getData().getContent();
			if (content != null) {
				payloadBuilder.add("content", content);

				log.info("Content = " + content);
				/* Load the LNK_DOT */

				// BaseEntity target = beUtils.getBaseEntityByCode(PRODUCT_CODE,
				// msg.getData().getTargetCode());
				// Attribute lnkDot = qwandaUtils.getAttribute("LNK_DOT");
				// target.addAnswer(new Answer(target, target, lnkDot, "[\"" + content +
				// "\"]"));
				// beUtils.updateBaseEntity(PRODUCT_CODE, target);

			}

			if (userToken != null) {
				if (userToken.getUserCode() != null) {
					payloadBuilder.add("sourceCode", userToken.getUserCode());
					payloadBuilder.add("userCode", userToken.getUserCode());
				}
			}

			JsonObject payload = payloadBuilder.build();

			log.info("Payload = " + payload.toString());

			// kogitoUtils.triggerWorkflow(SELF, "testQuestionGT2", payload);
			balService.createPersonBal("DEF_PERSON", content);
			return;
		}

		// test question eventIrvan
		if (code.startsWith("GADA_IRVAN_")) {
			log.info("Irvan Listener ..." + msg.getData().getCode() + " msg=" + msg);
			JsonObjectBuilder payloadBuilder = Json.createObjectBuilder()
					.add("questionCode", msg.getData().getCode().substring("GADA_IRVAN_".length()))
					.add("entityCode", msg.getData().getTargetCode())
					.add("targetCode", msg.getData().getTargetCode());

			String content = msg.getData().getContent();
			if (content != null) {
				payloadBuilder.add("content", content);

				log.info("Content = " + content);
				/* Load the LNK_DOT */
				if (content.startsWith("DOT_")) {
					BaseEntity target = beUtils.getBaseEntityByCode(PRODUCT_CODE, msg.getData().getTargetCode());
					Attribute lnkDot = qwandaUtils.getAttribute("LNK_DOT");
					target.addAnswer(new Answer(target, target, lnkDot, "[\"" + content + "\"]"));
					beUtils.updateBaseEntity(PRODUCT_CODE, target);
				}

			}
			if (userToken != null) {
				if (userToken.getUserCode() != null) {
					payloadBuilder.add("sourceCode", userToken.getUserCode());
					payloadBuilder.add("userCode", userToken.getUserCode());
				}
			}

			JsonObject payload = payloadBuilder.build();

			log.info("Payload = " + payload.toString());

			kogitoUtils.triggerWorkflow(SELF, "testQuestionIrvan", payload);
			return;
		}

		if (code.startsWith("GADA_AMRIZAL_QUE_")) {
			log.info("Displaying Amrizal test question ..." + msg.getData().getCode() + " msg=" + msg);
			JsonObjectBuilder payloadBuilder = Json.createObjectBuilder()
					.add("questionCode", msg.getData().getCode().substring("GADA_AMRIZAL_".length()));

			String content = msg.getData().getContent();
			if (content != null) {
				payloadBuilder.add("content", content);

				log.info("Content = " + content);
				/* Load the LNK_DOT */

				String amrizalCode = amService.createPersonAmrizal(DEF_BALI, content);

				if (amrizalCode != null)
					payloadBuilder.add("entityCode", amrizalCode).add("targetCode", amrizalCode);
				else
					payloadBuilder
							.add("entityCode", msg.getData().getTargetCode())
							.add("targetCode", msg.getData().getTargetCode());

				BaseEntity target = beUtils.getBaseEntityByCode(PRODUCT_CODE, amrizalCode);
				Attribute lnkDot = qwandaUtils.getAttribute("LNK_DOT");
				target.addAnswer(new Answer(target, target, lnkDot, "[\"" + content + "\"]"));
				beUtils.updateBaseEntity(PRODUCT_CODE, target);
			}

			if (userToken != null) {
				if (userToken.getUserCode() != null) {
					payloadBuilder.add("sourceCode", userToken.getUserCode());
					payloadBuilder.add("userCode", userToken.getUserCode());
				}
			} else {
				log.error("userToken is Null");
			}

			JsonObject payload = payloadBuilder.build();

			log.info("Payload = " + payload.toString());

			if (kogitoUtils != null) {
				kogitoUtils.triggerWorkflow(SELF, "testQuestionGT2", payload);
			} else {
				log.error("kogitoUtils is Null");
			}
		}

	}

}
