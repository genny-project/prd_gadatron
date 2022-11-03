package life.genny.gadatron.live.data;

import static life.genny.gadatron.constants.GadatronConstants.PRODUCT_CODE;

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

import io.quarkus.runtime.StartupEvent;
import io.smallrye.reactive.messaging.annotations.Blocking;
import life.genny.kogito.common.utils.KogitoUtils;
import life.genny.gadatron.cache.RoleCaching;
import life.genny.gadatron.cache.SearchCaching;
import life.genny.gadatron.route.Events;
import life.genny.qwandaq.Answer;
import life.genny.qwandaq.message.QEventMessage;
import life.genny.qwandaq.models.UserToken;
import life.genny.qwandaq.utils.SecurityUtils;
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
	RoleCaching roleCaching;

	@Inject
	Events events;

	/**
	 * Execute on start up.
	 *
	 * @param ev The startup event
	 */
	void onStart(@Observes StartupEvent ev) {
		service.fullServiceInit();
		SearchCaching.saveToCache();
		roleCaching.saveToCache();
	}

	/**
	 * Consume from the lojing data topic.
	 *
	 * @param data The incoming data
	 */
	@Incoming("genny_data")
	@Blocking
	public void getData(String data) {

		Instant start = Instant.now();
		scope.init(data);

		// ensure the message is for this service
		if (!PRODUCT_CODE.equals(userToken.getProductCode())) {
			scope.destroy();
			// log duration
			Instant end = Instant.now();
			log.info("Duration = " + Duration.between(start, end).toMillis() + "ms");
			return;
		}

		// process data using inference
		List<Answer> answers = kogitoUtils.runDataInference(data);
		kogitoUtils.funnelAnswers(answers);

		// TODO: Do we need to save answers here?

		scope.destroy();

		// log duration
		Instant end = Instant.now();
		log.info("Duration = " + Duration.between(start, end).toMillis() + "ms");
	}

	/**
	 * Consume from the lojing events topic.
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

		log.info("Received Event : " + SecurityUtils.obfuscate(event));

		// If the event is a Dropdown then leave it for DropKick
		if ("DD".equals(msg.getEvent_type())) {
			return;
		}
		events.route(msg);
		scope.destroy();
		Instant end = Instant.now();
		log.info("Duration = " + Duration.between(start, end).toMillis() + "ms");
	}
}
