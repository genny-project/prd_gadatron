package life.genny.gadatron.live.data;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;
import org.kie.api.runtime.KieRuntimeBuilder;
import org.kie.api.runtime.KieSession;

import io.quarkus.runtime.StartupEvent;
import io.smallrye.reactive.messaging.annotations.Blocking;
import life.genny.gadatron.Constants;
import life.genny.gadatron.search.SearchCaching;
import life.genny.kogito.common.utils.KogitoUtils;
import life.genny.qwandaq.Answer;
import life.genny.qwandaq.message.QDataAnswerMessage;
import life.genny.qwandaq.models.UserToken;
import life.genny.qwandaq.utils.KafkaUtils;
import life.genny.serviceq.Service;
import life.genny.serviceq.intf.GennyScopeInit;

@ApplicationScoped
public class InternalConsumer {

	static final Logger log = Logger.getLogger(InternalConsumer.class);

	static Jsonb jsonb = JsonbBuilder.create();

	@Inject
	Service service;

	@Inject
	GennyScopeInit scope;

	@Inject
	UserToken userToken;

	@Inject
	KogitoUtils kogitoUtils;

	@Inject
	KieRuntimeBuilder kieRuntimeBuilder;

	@Inject
	SearchCaching searchCaching;

	KieSession ksession;

	/**
	 * Execute on start up.
	 *
	 * @param ev The startup event
	 */
	void onStart(@Observes StartupEvent ev) {
		service.fullServiceInit();
		kogitoUtils.initDataByRuleGroup("sidebar");
		kogitoUtils.initDataByRuleGroup("bucket");
	}

	/**
	 * Consume data messages.
	 *
	 * @param data The incoming data
	 */
	@Incoming("valid_data")
	@Blocking
	public void getData(String data) {

		Instant start = Instant.now();
		scope.init(data);

		// ensure the message is for this service
		if (!Constants.PRODUCT_CODE.equals(userToken.getProductCode())) {
			scope.destroy();
			return;
		}

		// process data using inference
		List<Answer> answers = kogitoUtils.runDataInference(data);

		// pass it on to the next stage of inference pipeline
		QDataAnswerMessage msg = new QDataAnswerMessage(answers);
		msg.setToken(userToken.getToken());
		KafkaUtils.writeMsg("genny_data", msg);
		scope.destroy();

		// log duration
		Instant end = Instant.now();
		log.info("Duration = " + Duration.between(start, end).toMillis() + "ms");
	}

	/**
	 * Consume event messages.
	 *
	 * @param event The incoming event
	 */
	@Incoming("genny_events")
	@Blocking
	public void getEvent(String event) {

		Instant start = Instant.now();
		// init scope and process msg
		scope.init(event);
		kogitoUtils.routeEvent(event);
		scope.destroy();
		// log duration
		Instant end = Instant.now();
		log.info("Duration = " + Duration.between(start, end).toMillis() + "ms");
	}
}
