package life.genny.gadatron.cache;

import static life.genny.gadatron.constants.GadatronConstants.PRODUCT_CODE;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import life.genny.qwandaq.EEntityStatus;
import life.genny.qwandaq.attribute.Attribute;
import life.genny.qwandaq.datatype.DataType;
import life.genny.qwandaq.models.GennySettings;
import org.jboss.logging.Logger;

import life.genny.qwandaq.entity.search.SearchEntity;
import life.genny.qwandaq.entity.search.trait.Action;
import life.genny.qwandaq.entity.search.trait.Column;
import life.genny.qwandaq.entity.search.trait.AssociatedColumn;
import life.genny.qwandaq.entity.search.trait.Filter;
import life.genny.qwandaq.entity.search.trait.Operator;
import life.genny.qwandaq.entity.search.trait.Ord;
import life.genny.qwandaq.entity.search.trait.Sort;
import life.genny.qwandaq.utils.CacheUtils;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SearchCaching {

	static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass());

	// Sidebar
	public static final String SBE_APPOINTMENTS = "SBE_APPOINTMENTS";
	public static final String SBE_COMMUNICATIONS = "SBE_COMMUNICATIONS";
	public static final String SBE_DEV_UI = "SBE_DEV_UI";
	public static final String SBE_DOCUMENT_TEMPLATES = "SBE_DOCUMENTS_TEMPLATES";
	public static final String SBE_COMPANIES_VIEW = "SBE_COMPANIES_VIEW";
	public static final String SBE_COMPANIES = "SBE_COMPANIES";
	public static final String SBE_STAFF = "SBE_STAFF";

	// bucket
	public static final String SBE_TAB_BUCKET_VIEW = "SBE_TAB_BUCKET_VIEW";
	public static final String SBE_BUCKET1 = "SBE_BUCKET1";
	public static final String SBE_BUCKET2 = "SBE_BUCKET2";
	public static final String SBE_BUCKET3 = "SBE_BUCKET3";

	public static void saveToCache() {
		log.info("=========================saveToCache=========================");

		// Tables
		cacheSearch(new SearchEntity(
				SBE_APPOINTMENTS, "Appointments")
				.add(new Sort("PRI_CREATED", Ord.DESC))
				.add(new Filter("PRI_CODE", Operator.LIKE, "APT_%"))
				.add(new Filter("PRI_IS_APPOINTMENT", true))
				.add(new Column("PRI_CODE", "Code"))
				.add(new Column("PRI_NAME", "Name"))
				.add(new Column("PRI_DURATION_MIN", "Minimum Duration"))
				.add(new Column("PRI_START_DATETIME", "Start Date"))
				.add(new Column("PRI_ACTUAL_START_DATETIME", "Actual Start Date"))
				.add(new Column("PRI_GENERIC_URL", "URL (Optional)"))
				/* .addColumn("LNK_SELECT_TOOL", "Select Tool") */
				.add(new Column("LNK_RESOURCES", "Resources"))
				.add(new Column("LNK_RESOURCES_ACCEPTED", "Accepted Resources"))
				.add(new Action("EDIT", "Edit"))
				.setPageStart(0)
				.setPageSize(10000));

		cacheSearch(new SearchEntity(
				SBE_COMMUNICATIONS, "Communications Templates")
				.add(new Sort("PRI_NAME", Ord.ASC))
				/* .addSort("PRI_CREATED_DATE", "Date Created", SearchEntity.Sort.DESC) */
				.add(new Filter("PRI_CODE", Operator.LIKE, "MSG_%"))

				.add(new Column("PRI_CODE", "Code"))
				.add(new Column("PRI_NAME", "Name"))
				.add(new Column("PRI_DESCRIPTION", "Description"))
				.add(new Column("PRI_DEFAULT_MSG_TYPE", "Default Message Type"))
				.add(new Column("PRI_CONTEXT_LIST", "Context List"))
				.add(new Column("PRI_CONTEXT_ASSOCIATIONS", "Context Associations"))
				.add(new Column("PRI_CC", "CC"))
				.add(new Column("PRI_BCC", "BCC"))
				.add(new Column("PRI_BODY", "Body"))

				.add(new Action("PRI_EVENT_EDIT_MESSAGE", "Edit"))

				.setPageStart(0)
				.setPageSize(10000));

		cacheSearch(new SearchEntity(
				SBE_DEV_UI, "Dev UI")
				.add(new Sort("PRI_CODE", Ord.ASC))
				.add(new Column("PRI_CODE", "Code"))
				.add(new Column("PRI_NAME", "Name"))
				.add(new Action("EDIT", "Edit"))
				.setSearchStatus(EEntityStatus.DELETED)
				.setPageStart(0)
				.setPageSize(50));

		cacheSearch(new SearchEntity(SBE_DOCUMENT_TEMPLATES, "Document Templates")
				// .addSort("PRI_LEGAL_NAME", "Legal Name", SearchEntity.Sort.ASC)
				// .addSort("PRI_NAME", "Name", SearchEntity.Sort.ASC) // not working
				.add(new Filter("PRI_CODE", Operator.LIKE, "DOT_%"))
				// .addColumn("PRI_NAME", "Name") //not working
				// .addColumn("PRI_LEGAL_NAME", "Legal Name")
				// .addAssociatedColumn("LNK_SIGNEE", "PRI_NAME", "Signee")
				.add(new Action("EDIT", "Edit"))
				.setPageStart(0)
				.setPageSize(10000));

		cacheSearch(new SearchEntity(
				SBE_COMPANIES, "Companies")
				/* Sorts */
				.add(new Sort("PRI_NAME", Ord.ASC))
				.add(new Sort("PRI_CREATED_DATE", Ord.DESC))
				/* .addSort("PRI_ASSOC_INDUSTRY", "Company Industry", SearchEntity.Sort.ASC) */
				/* .addSort("PRI_ADDRESS_STATE", "State", SearchEntity.Sort.ASC) */
				/* .addSort("PRI_VALIDATION", "Validation", SearchEntity.Sort.ASC) */
				/* .addSort("PRI_STATUS", "Status", SearchEntity.Sort.ASC) */

				/* Filters */
				.add(new Filter("PRI_CODE", Operator.LIKE, "CPY_%"))
				.add(new Filter("PRI_STATUS", Operator.EQUALS, "ACTIVE"))

				/* Table Columns */
				.add(new Column("PRI_IMAGE_URL", "Logo"))
				.add(new Column("PRI_NAME", "Name"))
				.add(new Column("PRI_CREATED_DATE", "Date Created"))
				.add(new Column("PRI_STATUS", "Status"))
				.add(new Column("PRI_VALIDATION", "Validation"))
				.add(new Column("PRI_MOBILE", "Phone"))
				.add(new Column("PRI_ADDRESS_FULL", "Address"))
				.add(new Column("PRI_ADDRESS_STATE", "State"))
				.add(new Column("PRI_ADDRESS_COUNTRY", "Country"))

				/* Filterable Columns */
				.addFilterableColumn("PRI_NAME", "Name")
				.addFilterableColumn("PRI_ASSOC_INDUSTRY", "Company Industry")
				.addFilterableColumn("PRI_VALIDATION", "Validation")
				.addFilterableColumn("PRI_STATUS", "Status")
				.addFilterableColumn("PRI_CREATED_DATE", "Date Created")

				/* Wildcard Whitelisted Attributes */
				.addWhitelist("PRI_NAME")

				/* Table actions */
				.add(new Action("PRI_EVENT_VIEW", "View Profile"))
				.setPageStart(0).setPageSize(GennySettings.defaultBucketSize()));

		cacheSearch(new SearchEntity(SBE_STAFF, "Staff")
				/* Sorts */
				.add(new Sort("PRI_NAME", Ord.ASC))
				.add(new Sort("PRI_CREATED_DATE", Ord.DESC))
				/* .addSort("PRI_STATUS","Status",SearchEntity.Sort.ASC) */

				/* Filters */
				.add(new Filter("PRI_CODE", Operator.LIKE, "PER_%"))

				/* Table Columns */
				.add(new Column("PRI_IMAGE_URL", "Profile Picture"))
				.add(new Column("PRI_NAME", "Name"))
				.add(new Column("PRI_CREATED_DATE", "Date Created"))
				.add(new Column("PRI_STATUS", "Status"))
				.add(new AssociatedColumn("LNK_COMPANY", "PRI_NAME", "Company"))
				.add(new Column("PRI_EMAIL", "Email"))
				.add(new Column("PRI_MOBILE", "Mobile"))

				/* Filterable Columns */
				.addFilterableColumn("PRI_NAME", "Name")
				.addFilterableColumn("PRI_STATUS", "Status")
				.addFilterableColumn("PRI_ASSOC_HC", "Company")
				.addFilterableColumn("PRI_CREATED_DATE", "Date Created")

				/* Wildcard Whitelisted Attributes */
				.addWhitelist("PRI_NAME")

				/* Table actions */
				.add(new Action("PRI_EVENT_VIEW", "View Profile"))
				.setPageStart(0).setPageSize(GennySettings.defaultBucketSize()));

	}

	/**
	 * Set Buckets
	 * 
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return void
	 */
	public void setSBETabBucketView(String realm, String name) {
		List<String> bucketCodes = new ArrayList<String>();
		bucketCodes.add(SBE_BUCKET1);
		bucketCodes.add(SBE_BUCKET2);
		bucketCodes.add(SBE_BUCKET3);

		CacheUtils.putObject(realm, SBE_TAB_BUCKET_VIEW, bucketCodes);
	}

	/**
	 * Get search base entity of applied bucket
	 * 
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return search base entity of applied bucket
	 */
	public SearchEntity getSBEBucket1(String realm, String name) {
		SearchEntity searchBE = new SearchEntity(name, "Bucket1")
				.add(new Sort("PRI_CREATED", Ord.DESC))
				.add(new Filter("PRI_CODE", Operator.LIKE, "APP_%"))
				.add(new Filter("PRI_STAGE", Operator.EQUALS, "APPLIED"))
				.add(new Filter("PRI_DISABLED", false))

				/*
				 * .addFilter("PRI_CODE.LNK_SELECT_BATCH",
				 * SearchEntity.StringFilter.EQUAL, "[\"SEL_TEST\"]")
				 */

				.add(new AssociatedColumn("LNK_INTERNSHIP", "LNK_HOST_COMPANY", "PRI_NAME", "Host Company"))
				.add(new AssociatedColumn("LNK_INTERNSHIP", "PRI_NAME", "Internship"))
				.add(new AssociatedColumn("LNK_EDU_PROVIDER", "PRI_NAME", "Education Provider"))
				/* .addColumn("PRI_ASSOC_HC", "Host Company") */
				/* .addColumn("PRI_TITLE", "Internship") */

				/* .addColumn("PRI_INTERN_EMAIL", "Email") */
				.add(new Column("PRI_INTERN_STUDENT_ID", "Student ID"))
				.add(new Column("PRI_ASSOC_INDUSTRY", "Industry"))
				.add(new Column("PRI_ASSOC_EP", "Edu Provider"))
				.add(new Column("PRI_ASSOC_OCCUPATION", "Occupation"))
				.add(new Column("PRI_ADDRESS_FULL", "Address"))
				.add(new Column("PRI_TRANSPORT", "Transport"))
				/* .addColumn("PRI_INTERN_MOBILE", "Mobile") */

				.add(new Column("PRI_STATUS_COLOR", " "))
				.add(new Column("PRI_IMAGE_URL", " "))
				.add(new Column("PRI_IMAGE_SECONDARY", " "))
				.add(new AssociatedColumn("PRI_INTERN_CODE", "LNK_AGENT", "PRI_NAME", "Agent Name"))
				.add(new AssociatedColumn("PRI_INTERN_CODE", "LNK_AGENT", "PRI_IMAGE_URL", "Agent Image"))
				.add(new AssociatedColumn("LNK_COMP_INTERNSHIP", "PRI_COLOUR", "Completing Internship Type Colour"))

				/* Wildcard blacklist */
				/* .addBlacklist("PRI_ROLES_AND_RESPONSIBILITIES") */
				/* .addBlacklist("PRI_BASE_LEARNING_OUTCOMES") */

				/* Wildcard Whitelisted Attributes */
				.addWhitelist("PRI_NAME")

				// .setDisplayMode("CARD:MAIN_3")
				/* Actions */
				.add(new Action("PRI_EVENT_SHORTLIST_APPLICATION", "Shortlist"))
				.add(new Action("PRI_EVENT_ACCESS_NOTES_APPLICATION", "Access Notes"))
				.add(new Action("PRI_EVENT_VIEW", "View Intern Profile"))
				.add(new Action("PRI_EVENT_VIEW_APPLICATION", "View Internship Summary"))
				.add(new Action("PRI_EVENT_EDIT_APPLICATION", "Edit Internship Summary"))
				.add(new Action("PRI_EVENT_ON_HOLD_APPLICATION", "On Hold"))
				.add(new Action("PRI_EVENT_REJECT_APPLICATION", "Reject"))
				.add(new Action("PRI_EVENT_WITHDRAW_APPLICATION", "Withdraw"))
				.setCachable(true)
				.setWildcardDepth(2)
				.setPageStart(0)
				.setPageSize(GennySettings.defaultBucketSize());

		searchBE.setRealm(realm);
		CacheUtils.putObject(realm, searchBE.getCode(), searchBE);

		return searchBE;
	}

	private static void cacheSearch(SearchEntity entity) {
		entity.setRealm(PRODUCT_CODE);
		CacheUtils.putObject(PRODUCT_CODE, entity.getCode(), entity);
	}

	private static void cacheDropdown(String definitionCode, SearchEntity entity) {
		entity.setRealm(PRODUCT_CODE);
		String key = new StringBuilder(definitionCode).append(":").append(entity.getCode()).toString();
		log.info("Caching Dropdown -> " + key);
		CacheUtils.putObject(PRODUCT_CODE, key, entity);
	}

}
