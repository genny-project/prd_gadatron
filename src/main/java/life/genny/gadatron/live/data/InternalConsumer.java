package life.genny.gadatron.live.data;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import life.genny.qwandaq.entity.SearchEntity;
import life.genny.qwandaq.entity.search.trait.Operator;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;
import org.kie.api.runtime.KieRuntimeBuilder;
import org.kie.api.runtime.KieSession;

import io.quarkus.runtime.StartupEvent;
import io.smallrye.reactive.messaging.annotations.Blocking;
import life.genny.gadatron.cache.RoleCaching;
import life.genny.gadatron.search.SearchCaching;
import life.genny.kogito.common.utils.KogitoUtils;
import life.genny.qwandaq.Answer;
import life.genny.qwandaq.attribute.Attribute;
import life.genny.qwandaq.models.UserToken;
import life.genny.serviceq.Service;
import life.genny.serviceq.intf.GennyScopeInit;
import life.genny.kogito.common.service.SearchService;
import life.genny.qwandaq.constants.GennyConstants;
import static life.genny.kogito.common.service.SearchService.SearchOptions;
import life.genny.gadatron.utils.FilterUtils;
import java.time.LocalDateTime;

@ApplicationScoped
public class InternalConsumer {

	static final Logger log = Logger.getLogger(InternalConsumer.class);

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
	FilterUtils filterUtils;

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

		Instant start = Instant.now();
		// init scope and process msg
		scope.init(event);
		kogitoUtils.routeEvent(event);
		scope.destroy();

		Map<String, Object> attMap = filterUtils.parseEventMessage(event, SearchOptions.PAGINATION);

		if (filterUtils.isPaginationEvent(attMap)) {
			userToken.init(attMap.get(GennyConstants.TOKEN).toString());
			String sbeCode = attMap.get(GennyConstants.TARGETCODE).toString();
			String code = attMap.get(GennyConstants.CODE).toString();

			searchService.handleSortAndSearch(code, code, "", sbeCode, SearchService.SearchOptions.PAGINATION);
		}
		// log duration
		Instant end = Instant.now();
		log.info("Duration = " + Duration.between(start, end).toMillis() + "ms");
	}

	/**
	 * This method is used for sorting,searching and pagination in the table
	 * 
	 * @param data message data
	 */
	@Incoming("data")
	@Blocking
	public void getDataFromExternalBridge(String data) {
		Instant start = Instant.now();

		try {
			Map<String, Object> attrMap = filterUtils.parseEventMessage(data, SearchOptions.SEARCH);

			if (attrMap != null) {
				userToken.init(attrMap.get(GennyConstants.TOKEN).toString());
				String code = filterUtils.getSafeValueByCode(attrMap, GennyConstants.CODE);
				String attrCode = filterUtils.getSafeValueByCode(attrMap, GennyConstants.ATTRIBUTECODE);
				String attrName = "";
				String value = filterUtils.getSafeValueByCode(attrMap, GennyConstants.VALUE);
				String targetCode = filterUtils.getSafeValueByCode(attrMap, GennyConstants.TARGETCODE);

				if (attrCode.matches("SRT_.*") && targetCode.matches("SBE_.*")) { // Go to sorting
					searchService.handleSortAndSearch(attrCode, attrName, value, targetCode, SearchOptions.SEARCH);

					String lastSuffix = filterUtils.getLastSuffixCodeByFilterValue(value);
					searchService.sendFilterGroup(targetCode, GennyConstants.QUE_FILTER_GRP, lastSuffix, true);
				} else if (attrCode.equalsIgnoreCase(GennyConstants.SEARCH_TEXT)) {
					attrCode = Attribute.PRI_NAME;
					attrName = Operator.LIKE.toString();
					value = "%" + value.replaceFirst("!", "") + "%";
					List<String> targetCodes = (List) attrMap.get(GennyConstants.TARGETCODES);

					if (targetCodes.size() > 1) { // Go to bucket
						searchService.handleBucketSearch(attrCode, attrName, value, targetCodes);
					} else { // Go to search text
						searchService.handleSortAndSearch(attrCode, attrName, value, targetCode, SearchOptions.SEARCH);

						String lastSuffix = filterUtils.getLastSuffixCodeByFilterValue(value);
						searchService.sendFilterGroup(targetCode, GennyConstants.QUE_FILTER_GRP, lastSuffix, true);
					}
				} else if (filterUtils.isFilerColumnSelected(code, attrCode)) { // Go to filter column selected
					String queCode = filterUtils.getQuestionCodeByFilterValue(value);
					searchService.setFilterParamValByKey(GennyConstants.ATTRIBUTECODE, value);
					searchService.setFilterParamValByKey(GennyConstants.QUE_FILTER_COLUMN, queCode);

					searchService.sendFilterGroup(targetCode, GennyConstants.QUE_FILTER_GRP, queCode, true);
					searchService.sendFilterOption(queCode, targetCode);

					boolean isSelectBox = filterUtils.isFilterSelectQuestion(value);
					if (isSelectBox) {
						String linkVal = filterUtils.getLinkVal(value);
						searchService.sendFilterValue(GennyConstants.QUE_ADD_FILTER_GRP, queCode,
								FilterUtils.LNK_CORE, linkVal);
					} else {
						searchService.setFilterParamValByKey(GennyConstants.QUE_FILTER_VALUE, "");
					}
				} else if (filterUtils.isFilerOptionSelected(code, attrCode)) { // Go to filter option selected
					searchService.setFilterParamValByKey(GennyConstants.QUE_FILTER_OPTION, value);
				} else if (filterUtils.isFilterValue(code)) {
					searchService.setFilterParamValByKey(GennyConstants.QUE_FILTER_VALUE, value);
				}
			}
		} catch (Exception ex) {
		}

		log.info("Received Data : " + data);
		Instant end = Instant.now();
		log.info("Duration = " + Duration.between(start, end).toMillis() + "ms");
	}

	/**
	 * This method is to handle filter events in table and bucket page
	 * 
	 * @param event Event data
	 */
	@Incoming("events")
	@Blocking
	public void getFilterEvent(String event) {
		Instant start = Instant.now();

		try {
			Map<String, Object> attrMap = filterUtils.parseEventMessage(event, SearchOptions.FILTER);

			String code = filterUtils.getSafeValueByCode(attrMap, GennyConstants.CODE);
			String attrName = "";
			String targetCode = filterUtils.getSafeValueByCode(attrMap, GennyConstants.TARGETCODE);
			String question = "";

			if (searchService.isFilterTag(code)) {
				searchService.removeFilterTag(code);
				searchService.sendFilterGroup(targetCode, GennyConstants.QUE_FILTER_GRP, question, true);
			} else if (code.equalsIgnoreCase(GennyConstants.QUE_TABLE_LAZY_LOAD)) {
				userToken.init(attrMap.get(GennyConstants.TOKEN).toString());

				// send data when clicking on more in bucket
				searchService.handleSortAndSearch(code, code, "", targetCode, SearchOptions.PAGINATION_BUCKET);
			} else if (filterUtils.isValidFilterBox(code)) {
				userToken.init(attrMap.get(GennyConstants.TOKEN).toString());
				String sbeCode = filterUtils.getSearchEntityCodeByMsgCode(code);
				String lastSuffix = "";
				searchService.sendFilterGroup(sbeCode, GennyConstants.QUE_FILTER_GRP, lastSuffix, true);

			} else if (filterUtils.isFilterSubmit(code)) {
				userToken.init(attrMap.get(GennyConstants.TOKEN).toString());
				question = searchService.getFilterParamValByKey(GennyConstants.QUE_FILTER_COLUMN);
				attrName = searchService.getFilterParamValByKey(GennyConstants.QUE_FILTER_OPTION);
				String value = searchService.getFilterParamValByKey(GennyConstants.QUE_FILTER_VALUE)
						.replaceFirst(GennyConstants.SEL_PREF, "");
				String label = filterUtils.getLabelByValueCode(question, value);

				String attrCode = searchService.getFilterParamValByKey(GennyConstants.ATTRIBUTECODE);

				String sbeCode = filterUtils.getCleanSBECode(targetCode);
				boolean isDateTime = filterUtils.isDateTimeSelected(question);

				searchService.addFilterParamToList();
				searchService.sendFilterGroup(sbeCode, GennyConstants.QUE_FILTER_GRP, question, true);

				if (isDateTime) {
					Operator operator = filterUtils.getFilterOperatorByFilterVal(attrName);
					LocalDateTime localDateTime = filterUtils.parseStringToDate(label);
					searchService.handleFilterByDateTime(question, attrCode, operator, localDateTime, sbeCode);
				} else {
					Operator operator = filterUtils.getStringOpertorByFilterVal(attrName);
					searchService.handleFilterByString(question, attrCode, operator, label, sbeCode);
				}
			}

		} catch (Exception ex) {
		}

		log.info("Received Data : " + event);
		Instant end = Instant.now();
		log.info("Duration = " + Duration.between(start, end).toMillis() + "ms");
	}

}
