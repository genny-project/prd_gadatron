package life.genny.gadatron.route;

import life.genny.kogito.common.utils.KogitoUtils;
import life.genny.qwandaq.Answer;
import life.genny.qwandaq.attribute.Attribute;
import life.genny.qwandaq.entity.BaseEntity;
import life.genny.qwandaq.message.MessageData;
import life.genny.qwandaq.message.QEventMessage;
import life.genny.qwandaq.models.UserToken;
import life.genny.qwandaq.utils.BaseEntityUtils;
import life.genny.qwandaq.utils.QwandaUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.lang.invoke.MethodHandles;

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

	@ConfigProperty(name = "quarkus.application.name")
	String productCode;

	/**
	 * @param msg
	 */
	public void route(QEventMessage msg) {

		MessageData data = msg.getData();
		String code = data.getCode();

		log.info("message received: code: " + code);

		if (userToken == null) {
			log.error("userToken  is null - possible cause is no user logged in");
		}
		if (kogitoUtils == null) {
			log.error("kogitoUtils is null - possible cause is no userToken");
		}

		// add person
		if ("QUE_ADD_PERSON".equals(code)) {
			JsonObjectBuilder payloadBuilder = Json.createObjectBuilder()
					.add("productCode", productCode)
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
					.add("productCode", productCode)
					.add("questionCode", msg.getData().getCode().substring("TESTQ_".length()))
					.add("entityCode", msg.getData().getTargetCode())
					.add("targetCode", msg.getData().getTargetCode());

			String content = msg.getData().getContent();
			if (content != null) {
				payloadBuilder.add("content", content);

				log.info("Content = " + content);
				/* Load the LNK_DOT */

				BaseEntity target = beUtils.getBaseEntityByCode(productCode, msg.getData().getTargetCode());
				Attribute lnkDot = qwandaUtils.getAttribute("LNK_DOT");
				target.addAnswer(new Answer(target, target, lnkDot, "[\"" + content + "\"]"));
				beUtils.updateBaseEntity(productCode, target);
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
				kogitoUtils.triggerWorkflow(SELF, "testQuestion", payload);
			} else {
				log.error("kogitoUtils is Null");
			}
		}

		// test parent Call
		if (code.startsWith("TESTQ2_QUE_")) {
			log.info("Calling parent group Group ..." + msg.getData().getCode() + " msg=" + msg);
			JsonObjectBuilder payloadBuilder = Json.createObjectBuilder()
					.add("productCode", productCode)
					.add("questionCode", msg.getData().getCode().substring("TESTQ2_".length()));

			String content = msg.getData().getContent();

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
				kogitoUtils.triggerWorkflow(SELF, "baliPersonLC", payload);
			} else {
				log.error("kogitoUtils is Null");
			}
		}
	}

}
