package life.genny.gadatron.route;

import static life.genny.kogito.common.utils.KogitoUtils.UseService.SELF;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;

import life.genny.kogito.common.utils.KogitoUtils;
import life.genny.qwandaq.message.MessageData;
import life.genny.qwandaq.message.QEventMessage;
import life.genny.qwandaq.models.UserToken;

/**
 * Events
 */
@ApplicationScoped
public class Events {
	
	@Inject
	UserToken userToken;

	@Inject
	KogitoUtils kogitoUtils;

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
			kogitoUtils.triggerWorkflow(SELF,"testQuestion", payload);
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
	}

}
