package life.genny.gadatron.route;

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
import java.lang.invoke.MethodHandles;

import static life.genny.gadatron.constants.GadatronConstants.PRODUCT_CODE;
import static life.genny.kogito.common.utils.KogitoUtils.UseService.SELF;

/**
 * Events
 */
@ApplicationScoped
public class Events {

	static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass());

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

	/**
	 * @param msg
	 */
	public void route(QEventMessage msg) {

		MessageData data = msg.getData();
		String code = data.getCode();

		log.info("message received: code: "+code);

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
					.add("sourceCode", msg.getData().getSourceCode())
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
					payloadBuilder.add("content", content);
					JsonObject payload = payloadBuilder.build();
					kogitoUtils.triggerWorkflow(SELF, "testWayan", payload);
				}
			}
		}

		if (code.startsWith("QUE_WAYAN_") || code.equals("QUE_USER_PROFILE_VIEW")) {
			String sourceCode = msg.getData().getSourceCode();
			if (sourceCode == null || sourceCode.isEmpty()) return;
			String nextQuestionCode = switch (code) {
				case "QUE_WAYAN_CREATEQUESTION_VIEW" -> "QUE_WAYAN_CREATEQUESTION_GRP";
				case "QUE_WAYAN_CREATEBALIPERSON_VIEW" -> "QUE_BALI_PERSON_GRP";
				case "QUE_USER_PROFILE_VIEW" -> "QUE_USER_PROFILE_GRP";
				default -> "";
			};

			String entityCode = switch (code) {
				case "QUE_WAYAN_CREATEQUESTION_VIEW" -> "DEF_QUESTION";
				case "QUE_WAYAN_CREATEBALIPERSON_VIEW" -> "DEF_BALI_PERSON";
				case "QUE_USER_PROFILE_VIEW" -> "DEF_PERSON";
				default -> "";
			};

			if (nextQuestionCode.isEmpty()) {
				return;
			}

			JsonObjectBuilder payloadBuilder = Json.createObjectBuilder()
					.add("questionCode", nextQuestionCode)
					.add("sourceCode", sourceCode)
					.add("targetCode", msg.getData().getTargetCode())
					.add("entityCode", entityCode);

			payloadBuilder.add("content", "Simple Question");

			JsonObject payload = payloadBuilder.build();
			kogitoUtils.triggerWorkflow(SELF, "testWayanQuestion", payload);
			return;
		}
	}

}
