package life.genny.gadatron.search;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import life.genny.qwandaq.EEntityStatus;
import life.genny.qwandaq.attribute.AttributeInteger;
import life.genny.qwandaq.models.GennySettings;
import org.jboss.logging.Logger;

import life.genny.qwandaq.entity.SearchEntity;
import life.genny.qwandaq.utils.CacheUtils;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SearchCaching {

	static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass());

	// Sidebar
	public static final String SBE_APPOINTMENTS = "SBE_APPOINTMENTS";
	public static final String SBE_COMMUNICATIONS = "SBE_COMMUNICATIONS";
	public static final String SBE_DEV_UI = "SBE_DEV_UI";
	public static final String SBE_DOCUMENTS_GRP = "SBE_DOCUMENTS_GRP";

	// contacts
	public static final String SBE_PERSONS = "SBE_PERSONS";
	public static final String SBE_TABLE_PERSONS = "SBE_TABLE_PERSONS";
	public static final String SBE_COMPANIES_VIEW = "SBE_COMPANIES_VIEW";
	public static final String SBE_TABLE_COMPANIES = "SBE_TABLE_COMPANIES";

	// bucket
	public static final String SBE_TAB_BUCKET_VIEW = "SBE_TAB_BUCKET_VIEW";
	public static final String SBE_BUCKET1 = "SBE_BUCKET1";
	public static final String SBE_BUCKET2 = "SBE_BUCKET2";
	public static final String SBE_BUCKET3 = "SBE_BUCKET3";

	/**
	 * Set caching for all entities
	 * 
	 * @param realm Realm of product
	 */
	public void saveToCache(String realm) {
		log.info("=========================saveToCache=========================");

		List<SearchEntity> entities = new ArrayList<>();

		// sidebar
		entities.add(getSBEAppointment(realm, SearchCaching.SBE_APPOINTMENTS));
		entities.add(getSBECommunications(realm, SearchCaching.SBE_COMMUNICATIONS));
		entities.add(getSBEDevUI(realm, SearchCaching.SBE_DEV_UI));
		entities.add(getSBEDocuments(realm, SearchCaching.SBE_DOCUMENTS_GRP));

		// Contacts
		entities.add(getSBEPersons(realm, SearchCaching.SBE_PERSONS));
		entities.add(getSBECompanies(realm, SearchCaching.SBE_COMPANIES_VIEW));

		// bucket page
		setSBETabBucketView(realm, SearchCaching.SBE_TAB_BUCKET_VIEW);
		entities.add(getSBEBucket1(realm, SearchCaching.SBE_BUCKET1));
		entities.add(getSBEBucket2(realm, SearchCaching.SBE_BUCKET2));
		entities.add(getSBEBucket3(realm, SearchCaching.SBE_BUCKET3));

		// save to cache
		entities.stream().forEach(e -> {
			e.setRealm(realm);
			CacheUtils.putObject(realm, e.getCode(), e);
		});
	}

	/**
	 * Get search base entity of appointment
	 * 
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return search base entity of appointment
	 */
	public SearchEntity getSBEAppointment(String realm, String name) {
		SearchEntity searchBE = new SearchEntity(name, "Appointments")
				.addSort("PRI_CREATED", "Created", SearchEntity.Sort.DESC)
				.addFilter("PRI_CODE", SearchEntity.StringFilter.LIKE, "APT_%")
				.addFilter("PRI_IS_APPOINTMENT", true)
				.addColumn("PRI_CODE", "Code")
				.addColumn("PRI_NAME", "Name")
				.addColumn("PRI_DURATION_MIN", "Minimum Duration")
				.addColumn("PRI_START_DATETIME", "Start Date")
				.addColumn("PRI_ACTUAL_START_DATETIME", "Actual Start Date")
				.addColumn("PRI_GENERIC_URL", "URL (Optional)")
				/* .addColumn("LNK_SELECT_TOOL", "Select Tool") */
				.addColumn("LNK_RESOURCES", "Resources")
				.addColumn("LNK_RESOURCES_ACCEPTED", "Accepted Resources")
				.addAction("EDIT", "Edit")
				.setPageStart(0)
				.setPageSize(10000);

		searchBE.setRealm(realm);
		CacheUtils.putObject(realm, searchBE.getCode(), searchBE);

		return searchBE;
	}

	/**
	 * Get search base entity of communications
	 * 
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return search base entity of communications
	 */
	public SearchEntity getSBECommunications(String realm, String name) {
		SearchEntity searchBE = new SearchEntity(name, "Communications Templates")
				.addSort("PRI_NAME", "Name", SearchEntity.Sort.ASC)
				/* .addSort("PRI_CREATED_DATE", "Date Created", SearchEntity.Sort.DESC) */
				.addFilter("PRI_CODE", SearchEntity.StringFilter.LIKE, "MSG_%")

				.addColumn("PRI_CODE", "Code")
				.addColumn("PRI_NAME", "Name")
				.addColumn("PRI_DESCRIPTION", "Description")
				.addColumn("PRI_DEFAULT_MSG_TYPE", "Default Message Type")
				.addColumn("PRI_CONTEXT_LIST", "Context List")
				.addColumn("PRI_CONTEXT_ASSOCIATIONS", "Context Associations")
				.addColumn("PRI_CC", "CC")
				.addColumn("PRI_BCC", "BCC")
				.addColumn("PRI_BODY", "Body")

				.addAction("PRI_EVENT_EDIT_MESSAGE", "Edit")

				.setPageStart(0)
				.setPageSize(10000);

		searchBE.setRealm(realm);
		CacheUtils.putObject(realm, searchBE.getCode(), searchBE);

		return searchBE;
	}

	/**
	 * Get search base entity of dev ui
	 * 
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return search base entity of dev ui
	 */
	public SearchEntity getSBEDevUI(String realm, String name) {
		SearchEntity searchBE = new SearchEntity(name, "Dev UI")
				.addSort("PRI_CODE", "Code", SearchEntity.Sort.ASC)
				.addColumn("PRI_CODE", "Code")
				.addColumn("PRI_NAME", "Name")
				.addAction("EDIT", "Edit")
				.setSearchStatus(EEntityStatus.DELETED)
				.setPageStart(0)
				.setPageSize(50);

		searchBE.setRealm(realm);
		CacheUtils.putObject(realm, searchBE.getCode(), searchBE);

		return searchBE;
	}

	/**
	 * Get search base entity of documents
	 * 
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return search base entity of documents
	 */
	public SearchEntity getSBEDocuments(String realm, String name) {
		SearchEntity searchBE = new SearchEntity(name, "Documents")
				// .addSort("PRI_LEGAL_NAME", "Legal Name", SearchEntity.Sort.ASC)
				// .addSort("PRI_NAME", "Name", SearchEntity.Sort.ASC) // not working
				.addFilter("PRI_CODE", SearchEntity.StringFilter.LIKE, "DOC_%")
				// .addColumn("PRI_NAME", "Name") //not working
				// .addColumn("PRI_LEGAL_NAME", "Legal Name")
				// .addAssociatedColumn("LNK_SIGNEE", "PRI_NAME", "Signee")
				.addAction("EDIT", "Edit")
				.setPageStart(0)
				.setPageSize(10000);

		searchBE.setRealm(realm);
		CacheUtils.putObject(name, searchBE.getCode(), searchBE);

		return searchBE;
	}

	/**
	 * Get search base entity of persons
	 * 
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return search base entity of persons
	 */
	public SearchEntity getSBEPersons(String realm, String name) {
		SearchEntity searchBE = new SearchEntity(name, "People")
				/* Sorts */
				.addSort("PRI_NAME", "Name", SearchEntity.Sort.ASC)

				/* Filters */
				.addFilter("PRI_CODE", SearchEntity.StringFilter.LIKE, "PER_%")
				.addFilter("PRI_STATUS", SearchEntity.StringFilter.EQUAL, "ACTIVE")

				/* Table Columns */
				.addColumn("PRI_NAME", "Name")
				.addColumn("PRI_STATUS", "Status")
				.addColumn("PRI_ADDRESS_FULL", "Address")
				.addColumn("PRI_IMAGE_URL", "Logo")

				/* Filterable Columns */
				.addFilterableColumn("PRI_NAME", "Name")

				/* Wildcard Whitelisted Attributes */
				.addWhitelist("PRI_NAME")

				/* Table actions */
				.addAction("PRI_EVENT_VIEW", "View Profile")
				.setPageStart(0).setPageSize(GennySettings.defaultBucketSize());

		AttributeInteger priIndex = new AttributeInteger("PRI_INDEX", "PRI_INDEX");
		searchBE.addAttribute(priIndex, 1.0, 1);

		searchBE.setRealm(realm);
		CacheUtils.putObject(realm, searchBE.getCode(), searchBE);

		return searchBE;
	}

	/**
	 * Get search base entity of company
	 * 
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return search base entity of company
	 */
	public SearchEntity getSBECompanies(String realm, String name) {
		SearchEntity searchBE = new SearchEntity("SBE_COMPANIES_VIEW", "Companies")
				/* Sorts */
				.addSort("PRI_NAME", "Name", SearchEntity.Sort.ASC)
				.addSort("PRI_CREATED_DATE", "Date Created", SearchEntity.Sort.DESC)
				/* .addSort("PRI_ASSOC_INDUSTRY", "Company Industry", SearchEntity.Sort.ASC) */
				/* .addSort("PRI_ADDRESS_STATE", "State", SearchEntity.Sort.ASC) */
				/* .addSort("PRI_VALIDATION", "Validation", SearchEntity.Sort.ASC) */
				/* .addSort("PRI_STATUS", "Status", SearchEntity.Sort.ASC) */

				/* Filters */
				.addFilter("PRI_CODE", SearchEntity.StringFilter.LIKE, "CPY_%")
				.addFilter("PRI_STATUS", SearchEntity.StringFilter.EQUAL, "ACTIVE")

				/* Table Columns */
				.addColumn("PRI_IMAGE_URL", "Logo")
				.addColumn("PRI_NAME", "Name")
				.addColumn("PRI_CREATED_DATE", "Date Created")
				.addColumn("PRI_STATUS", "Status")
				.addColumn("PRI_VALIDATION", "Validation")
				.addColumn("PRI_MOBILE", "Phone")
				.addColumn("PRI_ADDRESS_FULL", "Address")
				.addColumn("PRI_ADDRESS_STATE", "State")
				.addColumn("PRI_ADDRESS_COUNTRY", "Country")

				/* Filterable Columns */
				.addFilterableColumn("PRI_NAME", "Name")
				.addFilterableColumn("PRI_DJP_AGREE", "DJP Agree")
				.addFilterableColumn("PRI_ASSOC_INDUSTRY", "Company Industry")
				.addFilterableColumn("PRI_VALIDATION", "Validation")
				.addFilterableColumn("PRI_STATUS", "Status")
				.addFilterableColumn("PRI_CREATED_DATE", "Date Created")

				/* Wildcard Whitelisted Attributes */
				.addWhitelist("PRI_NAME")

				/* Table actions */
				.addAction("PRI_EVENT_VIEW", "View Profile")
				.setPageStart(0).setPageSize(GennySettings.defaultBucketSize());

		AttributeInteger priIndex = new AttributeInteger("PRI_INDEX", "PRI_INDEX");
		searchBE.addAttribute(priIndex, 1.0, 1);

		searchBE.setRealm(realm);
		CacheUtils.putObject(realm, searchBE.getCode(), searchBE);

		return searchBE;
	}

	/**
	 * Get search base entity of Referrers
	 * 
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return search base entity of Referrers
	 */
	public void setSBETabBucketView(String realm, String name) {
		List<String> bucketCodes = new ArrayList<String>();
		bucketCodes.add("SBE_AVAILABLE_INTERNS");
		bucketCodes.add("SBE_APPLIED_APPLICATIONS");
		bucketCodes.add("SBE_SHORTLISTED_APPLICATIONS");
		bucketCodes.add("SBE_INTERVIEWED_APPLICATIONS");
		bucketCodes.add("SBE_OFFERED_APPLICATIONS");
		bucketCodes.add("SBE_PLACED_APPLICATIONS");
		bucketCodes.add("SBE_INPROGRESS_APPLICATIONS");

		CacheUtils.putObject(realm, "SBE_TAB_BUCKET_VIEW", bucketCodes);
	}

	/**
	 * Get search base entity of bucket1
	 * 
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return search base entity of bucket1
	 */
	public SearchEntity getSBEBucket1(String realm, String name) {
		SearchEntity searchBE = new SearchEntity(name, "Bucket1")
				.addSort("PRI_CREATED", "Created", SearchEntity.Sort.DESC)
				.addFilter("PRI_CODE", SearchEntity.StringFilter.LIKE, "PER_%")
				.addFilter("PRI_STAGE", SearchEntity.StringFilter.EQUAL, "STAGE1")
				.addFilter("PRI_DISABLED", false)

				.addAssociatedColumn("PRI_CODE", "PRI_NAME", "Name")
				/* .addColumn("PRI_ASSOC_HC", "Host Company") */
				.addColumn("PRI_ADDRESS_FULL", "Address")
				.addColumn("PRI_STATUS_COLOR", " ")
				.addColumn("PRI_IMAGE_URL", " ")
				.addColumn("PRI_IMAGE_SECONDARY", " ")

				/* Wildcard Whitelisted Attributes */
				.addWhitelist("PRI_NAME")

				.setDisplayMode("CARD:MAIN_3")
				/* Actions */
				.addAction("PRI_EVENT_BUCKET2", "Go to Bucket2")
				.addAction("PRI_EVENT_VIEW", "View Person Profile")
				.setCachable(true)
				.setWildcardDepth(2)
				.setPageStart(0)
				.setPageSize(GennySettings.defaultBucketSize());

		searchBE.setRealm(realm);
		CacheUtils.putObject(realm, searchBE.getCode(), searchBE);

		return searchBE;
	}

	/**
	 * Get search base entity of bucket1
	 * 
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return search base entity of bucket1
	 */
	public SearchEntity getSBEBucket2(String realm, String name) {
		SearchEntity searchBE = new SearchEntity(name, "Bucket2")
				.addSort("PRI_CREATED", "Created", SearchEntity.Sort.DESC)
				.addFilter("PRI_CODE", SearchEntity.StringFilter.LIKE, "PER_%")
				.addFilter("PRI_STAGE", SearchEntity.StringFilter.EQUAL, "STAGE2")
				.addFilter("PRI_DISABLED", false)

				.addAssociatedColumn("PRI_CODE", "PRI_NAME", "Name")
				/* .addColumn("PRI_ASSOC_HC", "Host Company") */
				.addColumn("PRI_ADDRESS_FULL", "Address")
				.addColumn("PRI_STATUS_COLOR", " ")
				.addColumn("PRI_IMAGE_URL", " ")
				.addColumn("PRI_IMAGE_SECONDARY", " ")

				/* Wildcard Whitelisted Attributes */
				.addWhitelist("PRI_NAME")

				.setDisplayMode("CARD:MAIN_3")
				/* Actions */
				.addAction("PRI_EVENT_BUCKET2", "Go to Bucket3")
				.addAction("PRI_EVENT_VIEW", "View Person Profile")
				.setCachable(true)
				.setWildcardDepth(2)
				.setPageStart(0)
				.setPageSize(GennySettings.defaultBucketSize());

		searchBE.setRealm(realm);
		CacheUtils.putObject(realm, searchBE.getCode(), searchBE);

		return searchBE;
	}

	/**
	 * Get search base entity of bucket3
	 * 
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return search base entity of bucket3
	 */
	public SearchEntity getSBEBucket3(String realm, String name) {
		SearchEntity searchBE = new SearchEntity(name, "Bucket3")
				.addSort("PRI_CREATED", "Created", SearchEntity.Sort.DESC)
				.addFilter("PRI_CODE", SearchEntity.StringFilter.LIKE, "PER_%")
				.addFilter("PRI_STAGE", SearchEntity.StringFilter.EQUAL, "STAGE3")
				.addFilter("PRI_DISABLED", false)

				.addAssociatedColumn("PRI_CODE", "PRI_NAME", "Name")
				/* .addColumn("PRI_ASSOC_HC", "Host Company") */
				.addColumn("PRI_ADDRESS_FULL", "Address")
				.addColumn("PRI_STATUS_COLOR", " ")
				.addColumn("PRI_IMAGE_URL", " ")
				.addColumn("PRI_IMAGE_SECONDARY", " ")

				/* Wildcard Whitelisted Attributes */
				.addWhitelist("PRI_NAME")

				.setDisplayMode("CARD:MAIN_3")
				/* Actions */
				.addAction("PRI_EVENT_BUCKET2", "Go to Bucket1")
				.addAction("PRI_EVENT_VIEW", "View Person Profile")
				.setCachable(true)
				.setWildcardDepth(2)
				.setPageStart(0)
				.setPageSize(GennySettings.defaultBucketSize());

		searchBE.setRealm(realm);
		CacheUtils.putObject(realm, searchBE.getCode(), searchBE);

		return searchBE;
	}

}