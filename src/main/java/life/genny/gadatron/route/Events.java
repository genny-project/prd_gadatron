package life.genny.gadatron.route;

import com.google.common.reflect.TypeToken;
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

		// add person
		if ("QUE_ADD_PERSON".equals(code)) {
			JsonObject payload = Json.createObjectBuilder()
					.add("questionCode", "QUE_PERSON_GRP")
					.add("userCode", userToken.getUserCode())
					.add("sourceCode", userToken.getUserCode())
					.add("targetCode", "PER_940F6070-356B-4AF0-99F5-663E2CB5AAA4")
					.build();
			kogitoUtils.triggerWorkflow(SELF, "testQuestion", payload);
			return;
		}

		// add admin
		if ("QUE_ADD_ADMIN".equals(code)) {
			JsonObject json = Json.createObjectBuilder()
					.add("questionCode", msg.getData().getCode())
					.add("entityCode", msg.getData().getTargetCode())
					.add("userCode", userToken.getUserCode())
					.build();
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
					.add("userCode", userToken.getUserCode())
					.add("sourceCode", userToken.getUserCode())
					.add("entityCode", msg.getData().getTargetCode())
					.add("targetCode", msg.getData().getTargetCode());

			String content = msg.getData().getContent();
			if (content != null) {
				payloadBuilder.add("content", content);

				System.out.println("Content = " + content);
				/* Load the LNK_DOT */

				BaseEntity target = beUtils.getBaseEntityByCode(PRODUCT_CODE, msg.getData().getTargetCode());
				Attribute lnkDot = qwandaUtils.getAttribute("LNK_DOT");
				target.addAnswer(new Answer(target, target, lnkDot, "[\"" + content + "\"]"));
				beUtils.updateBaseEntity(PRODUCT_CODE, target);
			}

			JsonObject payload = payloadBuilder.build();

			System.out.println("Payload = " + payload.toString());

			kogitoUtils.triggerWorkflow(SELF, "testQuestionGT2", payload);
		}

		if (code.startsWith("GADA_WAYAN_")) {
			log.info("Displaying GADA_WAYAN_ Test Question Group ..." + msg.getData().getCode() + " msg=" + msg);
			JsonObjectBuilder payloadBuilder = Json.createObjectBuilder()
					.add("questionCode", msg.getData().getCode().substring("GADA_WAYAN_".length()))
					.add("userCode", userToken.getUserCode())
					.add("sourceCode", userToken.getUserCode())
					.add("entityCode", msg.getData().getTargetCode())
					.add("targetCode", msg.getData().getTargetCode());
			String content = msg.getData().getContent();

			if (code.endsWith("ADD_PERSON")) {
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
			} else {
				if (content != null) {
					payloadBuilder.add("content", content);
				}

				JsonObject payload = payloadBuilder.build();
				log.info("Payload = " + payload.toString());

				kogitoUtils.triggerWorkflow(SELF, "testQuestionGT2", payload);
			}

		}

	}

}
