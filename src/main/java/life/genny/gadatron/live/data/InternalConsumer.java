package life.genny.gadatron.live.data;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import life.genny.qwandaq.entity.SearchEntity;
import life.genny.qwandaq.entity.search.trait.Operator;
import life.genny.qwandaq.message.QEventMessage;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;
import org.kie.api.runtime.KieRuntimeBuilder;
import org.kie.api.runtime.KieSession;

import io.quarkus.runtime.StartupEvent;
import io.smallrye.reactive.messaging.annotations.Blocking;
import life.genny.gadatron.cache.RoleCaching;
import life.genny.gadatron.route.Events;
import life.genny.gadatron.search.SearchCaching;
import life.genny.kogito.common.utils.KogitoUtils;
import life.genny.qwandaq.Answer;
import life.genny.qwandaq.attribute.Attribute;
import life.genny.qwandaq.models.UserToken;
import life.genny.serviceq.Service;
import life.genny.serviceq.intf.GennyScopeInit;
import life.genny.kogito.common.service.SearchService;
import life.genny.qwandaq.constants.GennyConstants;
import java.time.LocalDateTime;

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

	@Inject
	SearchService searchService;

	@Inject
	Events events;

	/**
	 * Execute on start up.
	 *
	 * @param ev The startup event
	 */
	void onStart(@Observes StartupEvent ev) {
		service.fullServiceInit();
		searchCaching.saveToCache(userToken.getRealm());
		//RoleCaching.saveToCache();
	}

	/**
	 * Consume data messages.
	 *
	 * @param data The incoming data
	 */
	@Incoming("genny_data")
	@Blocking
	public void getData(String data) {

		Instant start = Instant.now();
		scope.init(data);

		// ensure the message is for this service
		if (!"gadatron".equals(userToken.getProductCode())) {
			scope.destroy();
			return;
		}

		// process data using inference
		List<Answer> answers = kogitoUtils.runDataInference(data);
		kogitoUtils.funnelAnswers(answers);
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

		// init scope and process msg
		Instant start = Instant.now();
		scope.init(event);

		// check if event is a valid event
		QEventMessage msg = null;
		try {
			msg = jsonb.fromJson(event, QEventMessage.class);
		} catch (Exception e) {
			log.error("Cannot parse this event! " + event);
			e.printStackTrace();
			return;
		}
		events.route(msg);
		scope.destroy();

		// log duration
		Instant end = Instant.now();
		log.info("Duration = " + Duration.between(start, end).toMillis() + "ms");
	}

}
