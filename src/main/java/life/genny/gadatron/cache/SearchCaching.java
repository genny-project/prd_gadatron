package life.genny.gadatron.cache;

import static life.genny.gadatron.constants.GadatronConstants.PRODUCT_CODE;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import life.genny.qwandaq.EEntityStatus;
import life.genny.qwandaq.entity.search.SearchEntity;
import life.genny.qwandaq.attribute.Attribute;
import life.genny.qwandaq.datatype.DataType;
import life.genny.qwandaq.datatype.capability.core.CapabilityBuilder;
import life.genny.qwandaq.datatype.capability.core.node.PermissionMode;
import life.genny.qwandaq.models.GennySettings;
import org.jboss.logging.Logger;

import life.genny.qwandaq.entity.search.trait.Action;
import life.genny.qwandaq.entity.search.trait.Column;
import life.genny.qwandaq.entity.search.trait.AssociatedColumn;
import life.genny.qwandaq.entity.search.trait.Filter;
import life.genny.qwandaq.entity.search.trait.Operator;
import life.genny.qwandaq.entity.search.trait.Ord;
import life.genny.qwandaq.entity.search.trait.Sort;
import life.genny.qwandaq.entity.search.trait.Trait;
import life.genny.qwandaq.utils.CacheUtils;

import javax.enterprise.context.ApplicationScoped;

import static life.genny.qwandaq.attribute.Attribute.LNK_DEF;
import static life.genny.qwandaq.attribute.Attribute.PRI_IMAGE_URL;
import static life.genny.qwandaq.attribute.Attribute.PRI_NAME;
import static life.genny.qwandaq.attribute.Attribute.PRI_EMAIL;
import static life.genny.qwandaq.attribute.Attribute.PRI_MOBILE;
import static life.genny.qwandaq.attribute.Attribute.PRI_CREATED;
import static life.genny.qwandaq.attribute.Attribute.PRI_UPDATED;

import static life.genny.gadatron.constants.GadatronConstants.*;

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

	// Sidebar
	public static final String SBE_TABLE_APPOINTMENTS = "SBE_TABLE_APPOINTMENTS";
	public static final String SBE_TABLE_COMMUNICATIONS = "SBE_TABLE_COMMUNICATIONS";
	public static final String SBE_TABLE_DEV_UI = "SBE_TABLE_DEV_UI";
	public static final String SBE_TABLE_DOCUMENTS = "SBE_TABLE_DOCUMENTS";
	public static final String SBE_TABLE_EDU_PROVIDERS = "SBE_TABLE_EDU_PROVIDERS";
	public static final String SBE_TABLE_HOST_COMPANIES = "SBE_TABLE_HOST_COMPANIES";
	public static final String SBE_TABLE_INTERNSHIPS = "SBE_TABLE_INTERNSHIPS";
	public static final String SBE_TABLE_LOGBOOK = "SBE_TABLE_LOGBOOK";
	public static final String SBE_TABLE_PERSON = "SBE_TABLE_PERSON";
	public static final String SBE_TABLE_BALI_PERSON = "SBE_TABLE_BALI_PERSON";

	// contacts
	public static final String SBE_TABLE_AGENTS = "SBE_TABLE_AGENTS";
	public static final String SBE_TABLE_EPRS = "SBE_TABLE_EPRS";
	public static final String SBE_TABLE_HCRS = "SBE_TABLE_HCRS";
	public static final String SBE_TABLE_INTERNS = "SBE_TABLE_INTERNS";
	public static final String SBE_TABLE_REFERRERS = "SBE_TABLE_REFERRERS";

	// bucket
	public static final String SBE_BUCKET = "SBE_BUCKET";
	public static final String SBE_APPLIED_APPLICATIONS = "SBE_APPLIED_APPLICATIONS";
	public static final String SBE_AVAILABLE_INTERNS = "SBE_AVAILABLE_INTERNS";
	public static final String SBE_INPROGRESS_APPLICATIONS = "SBE_INPROGRESS_APPLICATIONS";
	public static final String SBE_INTERVIEWED_APPLICATIONS = "SBE_INTERVIEWED_APPLICATIONS";
	public static final String SBE_OFFERED_APPLICATIONS = "SBE_OFFERED_APPLICATIONS";
	public static final String SBE_PLACED_APPLICATIONS = "SBE_PLACED_APPLICATIONS";
	public static final String SBE_SHORTLISTED_APPLICATIONS = "SBE_SHORTLISTED_APPLICATIONS";

	public static final String VIEW = "VIEW";
	public static final String EDIT = "EDIT";
	public static final String APPLY = "APPLY";
	public static final String CREATE = "CREATE";
	public static final String DELETE = "DELETE";

	public void saveToCache(String realm) {
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

		cacheSearch(
				new SearchEntity(SBE_TABLE_PERSON, "People")
						.add(new Filter(LNK_DEF, Operator.CONTAINS, DEF_PERSON))
						// View own application only
						// .add(Trait.decorator(new Filter(LNK_PERSON, Operator.CONTAINS, "USER_CODE"))
						// .addCapabilityRequirement(
						// new CapabilityBuilder(PERSON).view(PermissionMode.SELF)
						// .view(PermissionMode.ALL, true)
						// .buildCap())
						// .build())
						// .add(new AssociatedColumn(LNK_STAFF, PRI_POSITION, "Staff Position"))
						.add(new Column("PRI_IMAGE_URL", "Profile Picture"))
						.add(new Column("PRI_NAME", "Name"))
						.add(new Column("PRI_CREATED", "Date Created"))
						.add(new Column("PRI_STATUS", "Status"))
						.add(new AssociatedColumn("LNK_COMPANY", "PRI_NAME", "Company"))
						.add(new Column("PRI_EMAIL", "Email"))
						.add(new Column("PRI_MOBILE", "Mobile"))
						.add(new Action(VIEW, "View"))
						.setPageSize(20) // TODO, use default page size
						.setPageStart(0));

		List<SearchEntity> entities = new ArrayList<>();

		// sidebar
		entities.add(getSBEAppointment(realm, SearchCaching.SBE_TABLE_APPOINTMENTS));
		entities.add(getSBECommunications(realm, SearchCaching.SBE_TABLE_COMMUNICATIONS));
		entities.add(getSBEDevUI(realm, SearchCaching.SBE_TABLE_DEV_UI));
		entities.add(getSBEDocuments(realm, SearchCaching.SBE_TABLE_DOCUMENTS));
		entities.add(getSBEEduProvider(realm, SearchCaching.SBE_TABLE_EDU_PROVIDERS));
		entities.add(getSBEHostCompany(realm, SearchCaching.SBE_TABLE_HOST_COMPANIES));
		entities.add(getSBEInternships(realm, SearchCaching.SBE_TABLE_INTERNSHIPS));
		entities.add(getSBELogbook(realm, SearchCaching.SBE_TABLE_LOGBOOK));

		// Contacts
		entities.add(getSBEAgents(realm, SearchCaching.SBE_TABLE_AGENTS));
		entities.add(getSBEEduProviderRep(realm, SearchCaching.SBE_TABLE_EPRS));
		entities.add(getSBEHostCompanyRep(realm, SearchCaching.SBE_TABLE_HCRS));
		entities.add(getSBEInterns(realm, SearchCaching.SBE_TABLE_INTERNS));
		entities.add(getSBEReferralPartnerRep(realm, SearchCaching.SBE_TABLE_REFERRERS));

		// bucket page
		entities.add(getSBEBucketApplied(realm, SearchCaching.SBE_APPLIED_APPLICATIONS));
		entities.add(getSBEBucketAvailable(realm, SearchCaching.SBE_AVAILABLE_INTERNS));
		entities.add(getSBEBucketInprogressApp(realm, SearchCaching.SBE_INPROGRESS_APPLICATIONS));
		entities.add(getSBEBucketInterview(realm, SearchCaching.SBE_INTERVIEWED_APPLICATIONS));
		entities.add(getSBEBucketOffered(realm, SearchCaching.SBE_OFFERED_APPLICATIONS));
		entities.add(getSBEBucketPlaced(realm, SearchCaching.SBE_PLACED_APPLICATIONS));
		entities.add(getSBEBucketShortlisted(realm, SearchCaching.SBE_SHORTLISTED_APPLICATIONS));

		// save to cache
		entities.stream().forEach(e -> {
			e.setRealm(realm);
			CacheUtils.putObject(realm, e.getCode(), e);
		});

		// DEF_AGENT
		cacheDropdown("DEF_AGENT",
				new SearchEntity("SBE_SER_LNK_AGENCY", "Agency Dropdown")
						.add(new Filter("LNK_DEF", Operator.CONTAINS, "DEF_AGENCY")));

		// DEF_COMPANY
		cacheDropdown("DEF_COMPANY",
				new SearchEntity("SBE_SER_LNK_COMPANY_INDUSTRY", "Company Industry Dropdown")
						.setLinkValue("COMPANY_INDUSTRY"));
		cacheDropdown("DEF_COMPANY",
				new SearchEntity("SBE_SER_LNK_NUMBER_STAFF", "Number of Staffs Dropdown")
						.setLinkValue("NO_OF_STAFF"));

		// DEF_HOST_CPY_REP
		cacheDropdown("DEF_HOST_CPY_REP",
				new SearchEntity("SBE_SER_LNK_HOST_COMPANY", "Host Company Dropdown")
						.add(new Filter("LNK_DEF", Operator.CONTAINS, "DEF_HOST_COMPANY")));

		// DEF_EDU_PRO_REP
		cacheDropdown("DEF_EDU_PRO_REP",
				new SearchEntity("SBE_SER_LNK_EDU_PROVIDER", "Edu Provider Dropdown")
						.add(new Filter("LNK_DEF", Operator.CONTAINS, "DEF_EDU_PROVIDER")));

		// DEF_INTERN
		cacheDropdown("DEF_INTERN",
				new SearchEntity("SBE_SER_LNK_EDU_PROVIDER", "Edu Provider Dropdown")
						.add(new Filter("LNK_DEF", Operator.CONTAINS, "DEF_EDU_PROVIDER")));

		// DEF_INTERNSHIP
		cacheDropdown("DEF_INTERNSHIP",
				new SearchEntity("SBE_SER_LNK_HOST_COMPANY", "Host Company Dropdown")
						.add(new Filter("LNK_DEF", Operator.CONTAINS, "DEF_HOST_COMPANY")));
		cacheDropdown("DEF_INTERNSHIP",
				new SearchEntity("SBE_SER_LNK_HOST_COMPANY_REP", "Host Company Rep Dropdown")
						.add(new Filter("LNK_DEF", Operator.CONTAINS, "DEF_HOST_COMPANY_REP"))
						.add(new Filter("LNK_HOST_COMPANY", Operator.CONTAINS, "[[SOURCE.LNK_HOST_COMPANY]]")));
		cacheDropdown("DEF_INTERNSHIP",
				new SearchEntity("SBE_SER_LNK_INTERN_SUPERVISOR", "Intern Supervisor Dropdown")
						.add(new Filter("LNK_DEF", Operator.CONTAINS, "DEF_HOST_COMPANY_REP"))
						.add(new Filter("LNK_HOST_COMPANY", Operator.CONTAINS, "[[SOURCE.LNK_HOST_COMPANY]]")));
		cacheDropdown("DEF_INTERNSHIP",
				new SearchEntity("SBE_SER_LNK_INTERNSHIP_TYPE", "Internship Type Dropdown")
						.setLinkValue("INTERNSHIP_TYPE"));
		cacheDropdown("DEF_INTERNSHIP",
				new SearchEntity("SBE_SER_LNK_INDUSTRY", "Industry Dropdown")
						.setLinkValue("INDUSTRY"));
		cacheDropdown("DEF_INTERNSHIP",
				new SearchEntity("SBE_SER_LNK_OCCUPATION", "Occupation Dropdown")
						.setSourceCode("[[TARGET.LNK_INDUSTRY]]")
						.setLinkValue("LNK_IND"));
		cacheDropdown("DEF_INTERNSHIP",
				new SearchEntity("SBE_SER_LNK_DRESS_CODE", "Dress Code Dropdown")
						.setLinkValue("DRESS_CODE"));
		cacheDropdown("DEF_INTERNSHIP",
				new SearchEntity("SBE_SER_LNK_NO_OF_INTERNS", "No Of Interns Dropdown")
						.setLinkValue("NO_OF_INTERNS"));
		cacheDropdown("DEF_INTERNSHIP",
				new SearchEntity("SBE_SER_LNK_WORKSITE_SELECT", "Worksite Dropdown")
						.setLinkValue("WORKSITE"));
		cacheDropdown("DEF_INTERNSHIP",
				new SearchEntity("SBE_SER_LNK_SOFTWARE", "Software Dropdown")
						.setLinkValue("SOFTWARE"));

		cacheDropdown("DEF_APPLICATION",
				new SearchEntity("SBE_SER_LNK_HOST_COMPANY", "Host Company Dropdown")
						.add(new Filter("PRI_IS_HOST_CPY", true)));
		cacheDropdown("DEF_APPLICATION",
				new SearchEntity("SBE_SER_LNK_INTERNSHIP", "Internship Dropdown")
						.add(new Filter("PRI_IS_INTERNSHIP", true))
						.add(new Filter("LNK_HOST_COMPANY", Operator.CONTAINS, "[[SOURCE.LNK_HOST_COMPANY]]")));

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

	/**
	 * Get search base entity of appointment
	 *
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return search base entity of appointment
	 */
	public SearchEntity getSBEAppointment(String realm, String name) {
		SearchEntity searchBE = new SearchEntity(name, "Appointments")
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
				.add(new Sort("PRI_CODE", Ord.ASC))
				.add(new Column("PRI_CODE", "Code"))
				.add(new Column("PRI_NAME", "Name"))
				.add(new Action("EDIT", "Edit"))
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
				.add(new Filter("PRI_CODE", Operator.LIKE, "DOC_%"))
				// .addColumn("PRI_NAME", "Name") //not working
				// .addColumn("PRI_LEGAL_NAME", "Legal Name")
				// .addAssociatedColumn("LNK_SIGNEE", "PRI_NAME", "Signee")
				.add(new Action("EDIT", "Edit"))
				.setPageStart(0)
				.setPageSize(10000);

		searchBE.setRealm(realm);
		CacheUtils.putObject(realm, searchBE.getCode(), searchBE);

		return searchBE;
	}

	/**
	 * Get search base entity of education provider
	 *
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return search base entity of education provider
	 */
	public SearchEntity getSBEEduProvider(String realm, String name) {
		SearchEntity searchBE = new SearchEntity(name, "Education Providers")
				/* Sorts */
				.add(new Sort("PRI_NAME", Ord.ASC))

				/* Filters */
				.add(new Filter("PRI_CODE", Operator.LIKE, "CPY_%"))
				.add(new Filter("PRI_IS_EDU_PROVIDER", true))
				.add(new Filter("PRI_STATUS", Operator.EQUALS, "ACTIVE"))

				/* Table Columns */
				.add(new Column("PRI_NAME", "Name"))
				.add(new Column("PRI_STATUS", "Status"))
				.add(new Column("PRI_ADDRESS_FULL", "Address"))
				.add(new Column("PRI_IMAGE_URL", "Logo"))

				/* Filterable Columns */
				.addFilterableColumn("PRI_NAME", "Name")

				/* Wildcard Whitelisted Attributes */
				// .addWhitelist("PRI_NAME")

				/* Table actions */
				.add(new Action("PRI_EVENT_VIEW", "View Profile"))
				.setPageStart(0).setPageSize(GennySettings.defaultBucketSize());

		Attribute priIndex = new Attribute("PRI_INDEX", "PRI_INDEX", new DataType(Integer.class));
		searchBE.addAttribute(priIndex, 1.0, 1);

		searchBE.setRealm(realm);
		CacheUtils.putObject(realm, searchBE.getCode(), searchBE);

		return searchBE;
	}

	/**
	 * Get search base entity of host company
	 *
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return search base entity of host company
	 */
	public SearchEntity getSBEHostCompany(String realm, String name) {
		SearchEntity searchBE = new SearchEntity(name, "Host Companies")
				/* Sorts */
				.add(new Sort("PRI_NAME", Ord.ASC))
				.add(new Sort("PRI_CREATED_DATE", Ord.DESC))
				/* .addSort("PRI_ASSOC_INDUSTRY", "Company Industry", SearchEntity.Sort.ASC) */
				/* .addSort("PRI_ADDRESS_STATE", "State", SearchEntity.Sort.ASC) */
				/* .addSort("PRI_VALIDATION", "Validation", SearchEntity.Sort.ASC) */
				/* .addSort("PRI_STATUS", "Status", SearchEntity.Sort.ASC) */

				/* Filters */
				.add(new Filter("PRI_CODE", Operator.LIKE, "CPY_%"))
				.add(new Filter("PRI_IS_HOST_CPY", true))
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
				.addFilterableColumn("TEXT_PRI_NAME", "Name")
				.addFilterableColumn("YES_NO_PRI_DJP_AGREE", "DJP Agree")
				// .addFilterableColumn("PRI_TEXT_INDUSTRY", "Company Industry")
				.addFilterableColumn("TEXT_PRI_VALIDATION", "Validation")
				.addFilterableColumn("TEXT_PRI_STATUS", "Status")
				.addFilterableColumn("DATETIME_PRI_CREATED_DATE", "Date Created")

				/* Wildcard Whitelisted Attributes */
				.addWhitelist("PRI_NAME")

				/* Table actions */
				.add(new Action("PRI_EVENT_VIEW", "View Profile"))
				.setPageStart(0).setPageSize(GennySettings.defaultBucketSize());

		Attribute priIndex = new Attribute("PRI_INDEX", "PRI_INDEX", new DataType(Integer.class));
		searchBE.addAttribute(priIndex, 1.0, 1);

		searchBE.setRealm(realm);
		CacheUtils.putObject(realm, searchBE.getCode(), searchBE);

		return searchBE;
	}

	/**
	 * Get search base entity of internships
	 *
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return search base entity of internships
	 */
	public SearchEntity getSBEInternships(String realm, String name) {
		SearchEntity searchBE = new SearchEntity(name, "Internships")
				/* Sorts */
				.add(new Sort("PRI_CREATED", Ord.DESC))
				.add(new Sort("PRI_STATUS", Ord.ASC))
				/* .addSort("PRI_ASSOC_INDUSTRY","Industry",SearchEntity.Sort.ASC) */
				/* .addSort("PRI_ASSOC_HC","Host Company",SearchEntity.Sort.ASC) */
				/* .addSort("PRI_INTERNSHIP_START_DATE","Start Date",SearchEntity.Sort.ASC) */

				/* Filters */
				.add(new Filter("PRI_IS_INTERNSHIP", true))
				.add(new Filter("PRI_CODE", Operator.LIKE, "BEG_%"))
				/* .addFilter("PRI_STATUS", SearchEntity.StringFilter.LIKE, "ACTIVE") */
				/* Table Columns */
				.add(new Column("PRI_IMAGE_URL", "Company Logo"))
				.add(new Column("PRI_NAME", "Title"))
				/* .addColumn("PRI_INTERNSHIP_TITLE", "Title") */
				.add(new Column("PRI_CREATED", "Date Created"))
				.add(new Column("PRI_STATUS", "Status"))
				/* .addColumn("PRI_ASSOC_HC", "Host Company") */
				.add(new AssociatedColumn("LNK_HOST_COMPANY", "PRI_NAME", "Host Company"))
				.add(new Column("PRI_ADDRESS_FULL", "Address"))
				.add(new Column("PRI_ADDRESS_STATE", "State"))
				.add(new Column("PRI_ADDRESS_COUNTRY", "Country"))
				.add(new Column("PRI_INTERNSHIP_START_DATE", "Proposed Start Date"))
				.add(new Column("PRI_ASSOC_INDUSTRY", "Industry"))
				.add(new Column("PRI_NO_OF_INTERNS", "Number of allowed Interns"))
				.add(new AssociatedColumn("LNK_SOFTWARE", "PRI_NAME", "Software"))
				.add(new AssociatedColumn("LNK_WORKSITE_SELECT", "PRI_NAME", "Worksite"))
				.add(new Column("PRI_ASSOC_OCCUPATION", "Specialisation"))

				/* Filterable Columns */
				.addFilterableColumn("TEXT_PRI_NAME", "Title")
				.addFilterableColumn("TEXT_PRI_STATUS", "Status")
				.addFilterableColumn("TEXT_PRI_INDUSTRY", "Industry")
				.addFilterableColumn("TEXT_PRI_ASSOC_HC", "Host Company")
				.addFilterableColumn("SELECT_PRI_COUNTRY_FIELD_PRI_ADDRESS_COUNTRY", "Country")
				.addFilterableColumn("DATETIME_PRI_CREATED", "Date Created")
				// .addFilterableColumn("SELECT_PRI_INTERNSHIP_TYPE", "Internship Type")

				/* Wildcard blacklist */
				/* .addBlacklist("PRI_ROLES_AND_RESPONSIBILITIES") */
				/* .addBlacklist("PRI_BASE_LEARNING_OUTCOMES") */

				/* Wildcard Whitelisted Attributes */
				.addWhitelist("PRI_NAME")
				/* .addWhitelist("PRI_ASSOC_HC") */
				.setWildcardDepth(1)

				/* Row Actions */
				.add(new Action("PRI_EVENT_VIEW", "View"))

				/* Table Actions */
				// .addSearchAction("PRI_EVENT_MAP_VIEW", "Map View")

				.setPageStart(0).setPageSize(30);

		Attribute priIndex = new Attribute("PRI_INDEX", "PRI_INDEX", new DataType(Integer.class));
		searchBE.addAttribute(priIndex, 1.0, 1);

		searchBE.setRealm(realm);
		CacheUtils.putObject(realm, searchBE.getCode(), searchBE);

		return searchBE;
	}

	/**
	 * Get search base entity of internships
	 *
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return search base entity of internships
	 */
	public SearchEntity getSBELogbook(String realm, String name) {
		SearchEntity searchBE = new SearchEntity(name, "Logbook")
				.add(new Filter("PRI_CODE", Operator.LIKE, "JNL_%"))
				.add(new Sort("PRI_JOURNAL_DATE", Ord.ASC))
				.add(new Column("PRI_JOURNAL_DATE", "Date"))
				.add(new Column("PRI_JOURNAL_HOURS", "Hours"))
				.add(new Column("PRI_STATUS", "Status"))
				.add(new Column("PRI_JOURNAL_TASKS", "Roles and Responsibilities"))
				.add(new Column("PRI_JOURNAL_LEARNING_OUTCOMES", "Learning Outcomes"))
				.add(new Column("PRI_HAS_DOWNLOAD_LINK", " "))
				// .setDisplayMode("journal")
				.setPageStart(0)
				.setPageSize(1000);

		Attribute priIndex = new Attribute("PRI_INDEX", "PRI_INDEX", new DataType(Integer.class));
		searchBE.addAttribute(priIndex, 1.0, 1);

		searchBE.setRealm(realm);
		CacheUtils.putObject(realm, searchBE.getCode(), searchBE);

		return searchBE;
	}

	/**
	 * Get search base entity of agents
	 *
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return search base entity of agents
	 */
	public SearchEntity getSBEAgents(String realm, String name) {
		SearchEntity searchBE = new SearchEntity(name, "Agents")
				/* Sorts */
				.add(new Sort("PRI_NAME", Ord.ASC))

				/* Filters */
				.add(new Filter("PRI_CODE", Operator.LIKE, "PER_%"))
				.add(new Filter("PRI_IS_AGENT", true))

				/* Table Columns */
				.add(new Column("PRI_IMAGE_URL", "Profile Picture"))
				.add(new Column("PRI_NAME", "Name"))
				.add(new Column("PRI_STATUS", "Status"))
				.add(new AssociatedColumn("LNK_AGENCY", "PRI_NAME", "Agency Name"))
				.add(new Column("PRI_EMAIL", "Email"))
				.add(new Column("PRI_MOBILE", "Mobile"))
				.add(new Column("PRI_ADDRESS_FULL", "Address"))

				/* Filterable Columns */
				.addFilterableColumn("TEXT_PRI_NAME", "Name")

				/* Wildcard Whitelisted Attributes */
				.addWhitelist("PRI_NAME")

				/* Table actions */
				.add(new Action("PRI_EVENT_VIEW", "View Profile"))
				.setPageStart(0).setPageSize(GennySettings.defaultBucketSize());

		Attribute priIndex = new Attribute("PRI_INDEX", "PRI_INDEX", new DataType(Integer.class));
		searchBE.addAttribute(priIndex, 1.0, 1);

		searchBE.setRealm(realm);
		CacheUtils.putObject(realm, searchBE.getCode(), searchBE);

		return searchBE;
	}

	/**
	 * Get search base entity of education provider representatives
	 *
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return search base entity of education provider representatives
	 */
	public SearchEntity getSBEEduProviderRep(String realm, String name) {
		SearchEntity searchBE = new SearchEntity(name, "Education Provider Representatives")
				/* Sorts */
				.add(new Sort("PRI_NAME", Ord.ASC))
				/* .addSort("PRI_STATUS","Status",SearchEntity.Sort.ASC) */
				/* .addSort("PRI_ASSOC_EP","Education Provider",SearchEntity.Sort.ASC) */
				/* .addSort("PRI_JOB_TITLE","Job Title",SearchEntity.Sort.ASC) */
				/* .addSort("PRI_DEPARTMENT","Department",SearchEntity.Sort.ASC) */

				/* Filters */
				/*
				 * .addFilter("PRI_NAME",SearchEntity.StringFilter.LIKE, "%%")
				 * .addFilter("PRI_STATUS",SearchEntity.StringFilter.LIKE, "%%")
				 * .addFilter("PRI_ASSOC_EP",SearchEntity.StringFilter.LIKE, "%%")
				 * .addFilter("PRI_JOB_TITLE",SearchEntity.StringFilter.LIKE, "%%")
				 * .addFilter("PRI_DEPARTMENT",SearchEntity.StringFilter.LIKE, "%%")
				 */
				.add(new Filter("PRI_CODE", Operator.LIKE, "PER_%"))
				.add(new Filter("PRI_IS_EDU_PRO_REP", true))

				/* Table Columns */
				.add(new Column("PRI_IMAGE_URL", "Profile Picture"))
				.add(new Column("PRI_NAME", "Name"))
				.add(new Column("PRI_STATUS", "Status"))
				.add(new Column("PRI_ASSOC_EP", "Education Provider"))
				.add(new Column("PRI_EMAIL", "Email"))
				.add(new Column("PRI_MOBILE", "Mobile"))
				.add(new Column("PRI_JOB_TITLE", "Job Title"))
				.add(new Column("PRI_DEPARTMENT", "Department"))
				.add(new Column("PRI_LINKEDIN_URL", "LinkedIn"))

				/* Filterable Columns */
				.addFilterableColumn("TEXT_PRI_NAME", "Name")
				.addFilterableColumn("TEXT_PRI_STATUS", "Status")
				.addFilterableColumn("TEXT_PRI_ASSOC_EP", "Education Provider")

				/* Wildcard Whitelisted Attributes */
				.addWhitelist("PRI_NAME")

				/* Table actions */
				.add(new Action("PRI_EVENT_VIEW", "View Profile"))
				.setPageStart(0).setPageSize(GennySettings.defaultBucketSize());

		Attribute priIndex = new Attribute("PRI_INDEX", "PRI_INDEX", new DataType(Integer.class));
		searchBE.addAttribute(priIndex, 1.0, 1);

		searchBE.setRealm(realm);
		CacheUtils.putObject(realm, searchBE.getCode(), searchBE);

		return searchBE;
	}

	/**
	 * Get search base entity of Host Company Representatives
	 *
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return search base entity of Host Company Representatives
	 */
	public SearchEntity getSBEHostCompanyRep(String realm, String name) {
		SearchEntity searchBE = new SearchEntity(name, "Host Company Representatives")
				/* Sorts */
				.add(new Sort("PRI_NAME", Ord.ASC))
				.add(new Sort("PRI_CREATED_DATE", Ord.DESC))
				/* .addSort("PRI_STATUS","Status",SearchEntity.Sort.ASC) */
				/* .addSort("PRI_ASSOC_HC","Company",SearchEntity.Sort.ASC) */

				/* Filters */
				.add(new Filter("PRI_CODE", Operator.LIKE, "PER_%"))
				.add(new Filter("PRI_IS_HOST_CPY_REP", true))

				/* Table Columns */
				.add(new Column("PRI_IMAGE_URL", "Profile Picture"))
				.add(new Column("PRI_NAME", "Name"))
				.add(new Column("PRI_CREATED_DATE", "Date Created"))
				.add(new Column("PRI_STATUS", "Status"))
				.add(new AssociatedColumn("LNK_HOST_COMPANY", "PRI_NAME", "Host Company"))
				.add(new Column("PRI_EMAIL", "Email"))
				.add(new Column("PRI_MOBILE", "Mobile"))

				/* Filterable Columns */
				.addFilterableColumn("TEXT_PRI_NAME", "Name")
				.addFilterableColumn("TEXT_PRI_STATUS", "Status")
				.addFilterableColumn("TEXT_PRI_ASSOC_HC", "Company")
				.addFilterableColumn("DATETIME_PRI_DATE", "Date Created")

				/* Wildcard Whitelisted Attributes */
				.addWhitelist("PRI_NAME")

				/* Table actions */
				.add(new Action("PRI_EVENT_VIEW", "View Profile"))
				.setPageStart(0).setPageSize(GennySettings.defaultBucketSize());

		Attribute priIndex = new Attribute("PRI_INDEX", "PRI_INDEX", new DataType(Integer.class));
		searchBE.addAttribute(priIndex, 1.0, 1);

		searchBE.setRealm(realm);
		CacheUtils.putObject(realm, searchBE.getCode(), searchBE);

		return searchBE;
	}

	/**
	 * Get search base entity of interns
	 *
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return search base entity of interns
	 */
	public SearchEntity getSBEInterns(String realm, String name) {
		SearchEntity searchBE = new SearchEntity(name, "Interns")
				/* Sorts */
				.add(new Sort("PRI_NAME", Ord.ASC))
				.add(new Sort("PRI_CREATED_DATE", Ord.DESC))
				/* .addSort("PRI_STATUS","Status", SearchEntity.Sort.ASC) */
				/* .addSort("PRI_ASSOC_INDUSTRY", "Industry", SearchEntity.Sort.ASC) */
				/* .addSort("PRI_NUM_JOURNALS", "Journals", SearchEntity.Sort.DESC) */
				/* .addSort("PRI_START_DATE","Start Date",SearchEntity.Sort.ASC) */
				/* .addSort("PRI_ASSOC_EP", "Education Provider", SearchEntity.Sort.ASC) */
				/* .addSort("PRI_ADDRESS_STATE", "State",SearchEntity.Sort.ASC) */

				/* Filters */
				.add(new Filter("PRI_CODE", Operator.LIKE, "PER_%"))
				.add(new Filter("PRI_IS_INTERN", true))
				/* .setSearchStatus(EEntityStatus.PENDING) */

				/* Table Columns */
				.add(new Column("PRI_IMAGE_URL", "Profile Picture"))
				.add(new Column("PRI_NAME", "Name"))
				.add(new Column("PRI_CREATED_DATE", "Date Created"))
				.add(new Column("PRI_PREFERRED_NAME", "Preferred Name"))
				.add(new Column("PRI_STATUS", "Status"))
				.add(new AssociatedColumn("LNK_INDUSTRY", "PRI_NAME", "Industry"))
				.add(new AssociatedColumn("LNK_OCCUPATION", "PRI_NAME", "Occupation"))
				.add(new AssociatedColumn("LNK_EDU_PROVIDER", "PRI_NAME", "Education Provider"))
				.add(new AssociatedColumn("LNK_INTERNSHIP_DURATION", "PRI_NAME", "Internship Duration"))
				.add(new AssociatedColumn("LNK_DAYS_PER_WEEK", "PRI_NAME", "Days per week"))
				.add(new Column("PRI_EMAIL", "Email"))
				.add(new Column("PRI_MOBILE", "Mobile"))
				.add(new AssociatedColumn("LNK_AGENT", "PRI_NAME", "Agent Name"))
				.add(new AssociatedColumn("LNK_AGENT", "PRI_ASSOC_AGENCY", "Assoc Agency"))
				.add(new Column("PRI_START_DATE", "Start Date"))
				.add(new Column("PRI_ADDRESS_SUBURB", "Suburb"))
				.add(new Column("PRI_ADDRESS_STATE", "State"))
				.add(new Column("PRI_ADDRESS_COUNTRY", "Country"))
				.add(new Column("PRI_VIDEO_URL", "Video URL"))
				.add(new Column("PRI_CV", "CV"))
				.add(new Column("PRI_NUM_JOURNALS", "Completed Journals"))
				.add(new AssociatedColumn("LNK_COMP_INTERNSHIP", "PRI_NAME", "Academy"))

				/* Filterable Columns */
				.addFilterableColumn("TEXT_PRI_NAME", "Name")
				.addFilterableColumn("TEXT_PRI_STATUS", "Status")
				.addFilterableColumn("TEXT_PRI_ASSOC_INDUSTRY", "Industry")
				.addFilterableColumn("DATETIME_PRI_START_DATE", "Start Date")
				.addFilterableColumn("TEXT_PRI_AGENT_NAME", "Agent Name")
				.addFilterableColumn("TEXT_PRI_BATCH", "Batch")
				.addFilterableColumn("SELECT_PRI_COUNTRY_FIELD_PRI_ADDRESS_COUNTRY", "Country")
				.addFilterableColumn("DATETIME_PRI_CREATED_DATE", "Date Created")
				.addFilterableColumn("SELECT_PRI_COMPLETE_INTERNSHIP", "Academy")

				/* Table actions */
				.add(new Action("PRI_EVENT_VIEW", "View Profile"))
				.add(new Action("PRI_EVENT_JOURNAL_VIEW", "View Logbook"))
				/* .addAction("PRI_EVENT_APPLY", "Apply to an Internship") */

				/* Wildcard Whitelisted Attributes */
				.addWhitelist("PRI_NAME")

				/* Row Action */
				// .addRowAction("PRI_EVENT_VIEW_APPLICATIONS", "View Applications")
				.add(new Action("PRI_EVENT_VIEW_APPLICATIONS", "View Applications"))
				.setCachable(true)
				.setWildcardDepth(1)
				.setPageStart(0).setPageSize(GennySettings.defaultBucketSize());

		Attribute priIndex = new Attribute("PRI_INDEX", "PRI_INDEX", new DataType(Integer.class));
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
	public SearchEntity getSBEReferralPartnerRep(String realm, String name) {
		SearchEntity searchBE = new SearchEntity(name, "Referrers")
				/* Sorts */
				.add(new Sort("PRI_NAME", Ord.ASC))

				/* Filters */
				.add(new Filter("PRI_CODE", Operator.LIKE, "PER_%"))
				.add(new Filter("PRI_IS_REF_PART_REP", true))

				/* Table Columns */
				.add(new Column("PRI_IMAGE_URL", "Profile Picture"))
				.add(new Column("PRI_NAME", "Name"))
				.add(new Column("PRI_STATUS", "Status"))
				.add(new Column("PRI_EMAIL", "Email"))
				.add(new Column("PRI_MOBILE", "Mobile"))

				/* Filterable Columns */
				.addFilterableColumn("PRI_NAME", "Name")

				/* Table actions */
				.add(new Action("PRI_EVENT_VIEW", "View Profile"))
				.setPageStart(0).setPageSize(GennySettings.defaultBucketSize());

		Attribute priIndex = new Attribute("PRI_INDEX", "PRI_INDEX", new DataType(Integer.class));
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
	public void setSBEBucketView(String realm, String name) {
		List<String> bucketCodes = new ArrayList<String>();
		bucketCodes.add(SBE_AVAILABLE_INTERNS);
		bucketCodes.add(SBE_APPLIED_APPLICATIONS);
		bucketCodes.add(SBE_SHORTLISTED_APPLICATIONS);
		bucketCodes.add(SBE_INTERVIEWED_APPLICATIONS);
		bucketCodes.add(SBE_OFFERED_APPLICATIONS);
		bucketCodes.add(SBE_PLACED_APPLICATIONS);
		bucketCodes.add(SBE_INPROGRESS_APPLICATIONS);

		CacheUtils.putObject(realm, SBE_BUCKET, bucketCodes);
	}

	/**
	 * Get search base entity of applied bucket
	 *
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return search base entity of applied bucket
	 */
	public SearchEntity getSBEBucketApplied(String realm, String name) {
		SearchEntity searchBE = new SearchEntity(name, "Applied")
				.add(new Sort("PRI_CREATED", Ord.DESC))
				.add(new Filter("PRI_CODE", Operator.LIKE, "APP_%"))
				.add(new Filter("PRI_STAGE", Operator.EQUALS, "APPLIED"))
				.add(new Filter("PRI_DISABLED", false))

				/*
				 * .addFilter("PRI_INTERN_CODE.LNK_SELECT_BATCH",
				 * SearchEntity.StringFilter.EQUAL, "[\"SEL_DIGITAL_JOBS\"]")
				 */

				.add(new AssociatedColumn("PRI_INTERN_CODE", "PRI_NAME", "Name"))
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

	/**
	 * Get search base entity of bucket Available
	 *
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return search base entity of bucket Available
	 */
	public SearchEntity getSBEBucketAvailable(String realm, String name) {
		SearchEntity searchBE = new SearchEntity(name, "Available")
				.add(new Sort("PRI_CREATED", Ord.DESC))
				.add(new Filter("PRI_CODE", Operator.LIKE, "PER_%"))
				.add(new Filter("PRI_IS_INTERN", true))
				.add(new Filter("PRI_DISABLED", false))
				/*
				 * .addFilter("PRI_STATUS", SearchEntity.StringFilter.NOT_LIKE, "P%") THIS IS A
				 * REAL UGLY SMARTASS HACK - ACC - I DID THIS TO AVOID PLACED OR PROGRESS
				 */
				.add(new Filter("PRI_STATUS", Operator.NOT_EQUALS, "PLACED"))
				.add(new Filter("PRI_STATUS", Operator.NOT_EQUALS, "PROGRESS"))
				.add(new Filter("PRI_STATUS", Operator.NOT_EQUALS, "PENDING"))

				.add(new Column("PRI_NAME", "Name"))
				.add(new AssociatedColumn("LNK_OCCUPATION", "PRI_NAME", " "))
				.add(new Column("PRI_MOBILE", "Mobile"))
				.add(new Column("PRI_EMAIL", "Email"))

				.add(new AssociatedColumn("LNK_EDU_PROVIDER", "PRI_NAME", "Education Provider"))
				.add(new AssociatedColumn("LNK_INDUSTRY", "PRI_NAME", "Industry"))
				.add(new AssociatedColumn("LNK_INTERNSHIP_DURATION", "PRI_NAME", "Duration"))
				.add(new Column("PRI_STATUS_COLOR", " "))
				.add(new Column("PRI_IMAGE_URL", " "))
				.add(new AssociatedColumn("LNK_AGENT", "PRI_NAME", "Agent Name"))
				.add(new AssociatedColumn("LNK_AGENT", "PRI_IMAGE_URL", "Agent Image"))
				.add(new AssociatedColumn("LNK_COMP_INTERNSHIP", "PRI_COLOUR", "Completing Internship Type Colour"))

				/* Wildcard Whitelisted Attributes */
				.addWhitelist("PRI_NAME")

				// .setDisplayMode("CARD:MAIN_2")
				/* Actions */
				.add(new Action("PRI_EVENT_POST_INTERVIEW", "Enter Star Rating"))
				.add(new Action("PRI_EVENT_APPLY", "Apply to an Internship"))
				.add(new Action("PRI_EVENT_VIEW", "View Intern Profile"))
				.add(new Action("PRI_EVENT_VIEW_APPLICATIONS", "View Intern's Applications"))
				.add(new Action("PRI_EVENT_ARCHIVE_INTERN", "Archive Intern"))
				.add(new Action("PRI_EVENT_ACCESS_NOTES_INTERN", "Access Notes"))
				.add(new Action("PRI_EVENT_UNARCHIVE_INTERN", "Unarchive Intern"))
				.setCachable(true)
				.setWildcardDepth(1)
				.setPageStart(0)
				.setPageSize(GennySettings.defaultBucketSize());

		searchBE.setRealm(realm);
		CacheUtils.putObject(realm, searchBE.getCode(), searchBE);

		return searchBE;
	}

	/**
	 * Get search base entity of bucket In Progress
	 *
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return search base entity of bucket In Progress
	 */
	public SearchEntity getSBEBucketInprogressApp(String realm, String name) {
		SearchEntity searchBE = new SearchEntity(name, "In Progress")
				.add(new Sort("PRI_CREATED", Ord.DESC))
				.add(new Filter("PRI_CODE", Operator.LIKE, "APP_%"))
				.add(new Filter("PRI_STAGE", Operator.EQUALS, "PROGRESS"))
				.add(new Filter("PRI_DISABLED", false))

				.add(new AssociatedColumn("PRI_INTERN_CODE", "PRI_NAME", "Name"))
				.add(new AssociatedColumn("LNK_INTERNSHIP", "LNK_HOST_COMPANY", "PRI_NAME", "Host Company"))
				.add(new AssociatedColumn("LNK_INTERNSHIP", "PRI_NAME", "Internship"))
				.add(new AssociatedColumn("LNK_EDU_PROVIDER", "PRI_NAME", "Education Provider"))
				/* .addColumn("PRI_ASSOC_HC", "Host Company") */
				/* .addColumn("PRI_TITLE", "Internship") */
				.add(new Column("PRI_END_DATE", "End Date"))
				.add(new Column("PRI_PROGRESS", "Application Progress"))

				.add(new Column("PRI_INTERN_STUDENT_ID", "Student ID"))
				.add(new Column("PRI_ASSOC_INDUSTRY", "Industry"))
				.add(new Column("PRI_ASSOC_EP", "Edu Provider"))
				.add(new Column("PRI_ASSOC_OCCUPATION", "Occupation"))
				.add(new Column("PRI_ADDRESS_FULL", "Address"))
				.add(new Column("PRI_TRANSPORT", "Transport"))
				.add(new Column("PRI_INTERN_MOBILE", "Mobile"))
				.add(new Column("PRI_INTERN_EMAIL", "Email"))

				.add(new Column("PRI_STATUS_COLOR", " "))
				.add(new Column("PRI_IMAGE_URL", " "))
				.add(new Column("PRI_IMAGE_SECONDARY", " "))
				.add(new AssociatedColumn("PRI_INTERN_CODE", "LNK_AGENT", "PRI_NAME", "Agent Name"))
				.add(new AssociatedColumn("PRI_INTERN_CODE", "LNK_AGENT", "PRI_IMAGE_URL", "Agent Image"))
				.add(new AssociatedColumn("LNK_COMP_INTERNSHIP", "PRI_COLOUR", "Completing Internship Type Colour"))

				.add(new Column("PRI_START_DATE", "Internship Start Date"))
				.add(new Column("PRI_END_DATE", "Internship End Date"))
				.add(new Column("PRI_ASSOC_DURATION", "Internship Duration"))
				.add(new Column("PRI_DAYS_PER_WEEK", "Number of Days per week"))

				/* Wildcard blacklist */
				/* .addBlacklist("PRI_ROLES_AND_RESPONSIBILITIES") */
				/* .addBlacklist("PRI_BASE_LEARNING_OUTCOMES") */

				/* Wildcard Whitelisted Attributes */
				.addWhitelist("PRI_NAME")

				// .setDisplayMode("CARD:MAIN_5")
				/* Actions */
				.add(new Action("PRI_EVENT_ACCESS_NOTES_APPLICATION", "Access Notes"))
				.add(new Action("PRI_EVENT_VIEW_APPLICATION", "View Internship Summary"))
				.add(new Action("PRI_EVENT_VIEW_AGREEMENT", "View/Sign Agreement Document"))
				.add(new Action("PRI_EVENT_VIEW", "View Intern Profile"))
				.add(new Action("PRI_EVENT_JOURNAL_VIEW", "View Logbook"))
				.add(new Action("PRI_EVENT_FINISH_APPLICATION", "Finish Internship"))
				.add(new Action("PRI_EVENT_EDIT_AGREEMENT", "Edit Agreement Document Data"))
				.add(new Action("PRI_EVENT_DOWNLOAD_INTERNSHIP_AGREEMENT_DOC",
						"Download Internship Agreement Document"))
				.add(new Action("PRI_EVENT_ON_HOLD_APPLICATION", "On Hold"))
				.add(new Action("PRI_EVENT_WITHDRAW_APPLICATION", "Withdraw"))
				.setCachable(true)
				.setWildcardDepth(2)
				.setPageStart(0)
				.setPageSize(GennySettings.defaultBucketSize());

		searchBE.setRealm(realm);
		CacheUtils.putObject(realm, searchBE.getCode(), searchBE);

		return searchBE;
	}

	/**
	 * Get search base entity of bucket interview
	 *
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return search base entity of bucket interview
	 */
	public SearchEntity getSBEBucketInterview(String realm, String name) {
		SearchEntity searchBE = new SearchEntity(name, "Interview")
				.add(new Sort("PRI_CREATED", Ord.DESC))
				.add(new Filter("PRI_CODE", Operator.LIKE, "APP_%"))
				.add(new Filter("PRI_STAGE", Operator.EQUALS, "INTERVIEWED"))
				.add(new Filter("PRI_DISABLED", false))

				.add(new AssociatedColumn("PRI_INTERN_CODE", "PRI_NAME", "Name"))
				.add(new AssociatedColumn("LNK_INTERNSHIP", "LNK_HOST_COMPANY", "PRI_NAME", "Host Company"))
				.add(new AssociatedColumn("LNK_INTERNSHIP", "PRI_NAME", "Internship"))
				.add(new AssociatedColumn("LNK_EDU_PROVIDER", "PRI_NAME", "Education Provider"))
				/* .addColumn("PRI_ASSOC_HC", "Host Company") */
				/* .addColumn("PRI_TITLE", "Internship") */
				.add(new Column("PRI_INTERVIEW_START_DATETIME", "Interview Date"))
				.add(new Column("PRI_INTERVIEW_TYPE", "Interview Type"))

				.add(new Column("PRI_ASSOC_INDUSTRY", "Industry"))
				.add(new Column("PRI_INTERN_STUDENT_ID", "Student ID"))
				.add(new Column("PRI_ASSOC_EP", "Edu Provider"))
				.add(new Column("PRI_ASSOC_OCCUPATION", "Occupation"))
				.add(new Column("PRI_ADDRESS_FULL", "Address"))

				.add(new Column("PRI_TRANSPORT", "Transport"))
				.add(new Column("PRI_INTERN_MOBILE", "Mobile"))
				.add(new Column("PRI_INTERN_EMAIL", "Email"))

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

				// .setDisplayMode("CARD:MAIN_5")
				/* Actions */
				.add(new Action("PRI_EVENT_OFFERED_APPLICATION", "Make an Offer"))
				.add(new Action("PRI_EVENT_ACCESS_NOTES_APPLICATION", "Access Notes"))
				.add(new Action("PRI_EVENT_VIEW_APPLICATION", "View Internship Summary"))
				.add(new Action("PRI_EVENT_VIEW", "View Intern Profile"))
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

	/**
	 * Get search base entity of bucket Offered
	 *
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return search base entity of bucket Offered
	 */
	public SearchEntity getSBEBucketOffered(String realm, String name) {
		SearchEntity searchBE = new SearchEntity(name, "Offered")
				.add(new Sort("PRI_CREATED", Ord.DESC))
				.add(new Filter("PRI_CODE", Operator.LIKE, "APP_%"))
				.add(new Filter("PRI_STAGE", Operator.EQUALS, "OFFERED"))
				.add(new Filter("PRI_DISABLED", false))

				.add(new AssociatedColumn("PRI_INTERN_CODE", "PRI_NAME", "Name"))
				.add(new AssociatedColumn("LNK_INTERNSHIP", "LNK_HOST_COMPANY", "PRI_NAME", "Host Company"))
				.add(new AssociatedColumn("LNK_INTERNSHIP", "PRI_NAME", "Internship"))
				.add(new AssociatedColumn("LNK_EDU_PROVIDER", "PRI_NAME", "Education Provider"))
				/* .addColumn("PRI_ASSOC_HC", "Host Company") */
				/* .addColumn("PRI_TITLE", "Internship") */

				.add(new Column("PRI_INTERN_STUDENT_ID", "Student ID"))
				.add(new Column("PRI_ASSOC_INDUSTRY", "Industry"))
				.add(new Column("PRI_ASSOC_EP", "Edu Provider"))
				.add(new Column("PRI_START_DATE", "Start Date"))
				.add(new Column("PRI_ASSOC_OCCUPATION", "Occupation"))
				.add(new Column("PRI_ADDRESS_FULL", "Address"))
				.add(new Column("PRI_TRANSPORT", "Transport"))
				.add(new Column("PRI_INTERN_MOBILE", "Mobile"))
				.add(new Column("PRI_INTERN_EMAIL", "Email"))

				.add(new Column("PRI_AGR_DOC_OUTCOME_SIGNATURE", "Outcome Signature"))
				.add(new Column("PRI_AGR_DOC_INT_SIGNATURE", "Intern Signature"))
				.add(new Column("PRI_AGR_DOC_HC_SIGNATURE", "Company Signature"))

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
				/* .addAction("PRI_EVENT_VIEW_AGREEMENT", "View Agreement Document") */
				.add(new Action("PRI_EVENT_ACCESS_NOTES_APPLICATION", "Access Notes"))
				.add(new Action("PRI_EVENT_VIEW_AGREEMENT", "View/Sign Agreement Document"))
				.add(new Action("PRI_EVENT_VIEW_APPLICATION", "View Internship Summary"))
				.add(new Action("PRI_EVENT_VIEW", "View Intern Profile"))
				.add(new Action("PRI_EVENT_EDIT_AGREEMENT", "Edit Agreement Document Data"))
				.add(new Action("PRI_EVENT_DOWNLOAD_INTERNSHIP_AGREEMENT_DOC",
						"Download Internship Agreement Document"))
				.add(new Action("PRI_EVENT_ON_HOLD_APPLICATION", "On Hold"))
				.add(new Action("PRI_EVENT_REJECT_APPLICATION", "Reject"))
				.add(new Action("PRI_EVENT_WITHDRAW_APPLICATION", "Withdraw"))
				.add(new Action("PRI_EVENT_PLACE_APPLICATION", "Place"))
				.setCachable(true)
				.setWildcardDepth(2)
				.setPageStart(0)
				.setPageSize(GennySettings.defaultBucketSize());

		searchBE.setRealm(realm);
		CacheUtils.putObject(realm, searchBE.getCode(), searchBE);

		return searchBE;
	}

	/**
	 * Get search base entity of bucket Placed
	 *
	 * @param realm Realm of product
	 * @param name  Caching name
	 * @return search base entity of bucket Placed
	 */
	public SearchEntity getSBEBucketPlaced(String realm, String name) {
		SearchEntity searchBE = new SearchEntity(name, "Placed")
				.add(new Sort("PRI_CREATED", Ord.DESC))
				.add(new Filter("PRI_CODE", Operator.LIKE, "APP_%"))
				.add(new Filter("PRI_STAGE", Operator.EQUALS, "PLACED"))
				.add(new Filter("PRI_DISABLED", false))

				.add(new AssociatedColumn("PRI_INTERN_CODE", "PRI_NAME", "Name"))
				.add(new AssociatedColumn("LNK_INTERNSHIP", "LNK_HOST_COMPANY", "PRI_NAME", "Host Company"))
				.add(new AssociatedColumn("LNK_INTERNSHIP", "PRI_NAME", "Internship"))
				.add(new AssociatedColumn("LNK_EDU_PROVIDER", "PRI_NAME", "Education Provider"))
				/* .addColumn("PRI_ASSOC_HC", "Host Company") */
				/* .addColumn("PRI_TITLE", "Internship") */
				.add(new Column("PRI_START_DATE", "Start Date"))

				.add(new Column("PRI_ASSOC_INDUSTRY", "Industry"))
				.add(new Column("PRI_ASSOC_EP", "Edu Provider"))
				.add(new Column("PRI_START_DATE", "Start Date"))
				.add(new Column("PRI_ASSOC_OCCUPATION", "Occupation"))
				.add(new Column("PRI_ADDRESS_FULL", "Address"))
				.add(new Column("PRI_TRANSPORT", "Transport"))
				.add(new Column("PRI_INTERN_MOBILE", "Mobile"))
				.add(new Column("PRI_INTERN_EMAIL", "Email"))
				.add(new Column("PRI_INTERN_STUDENT_ID", "Student ID"))

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

				// .setDisplayMode("CARD:MAIN_4")
				/* Actions */
				/* .addAction("VIEW_AGREEMENT", "View") */
				.add(new Action("PRI_EVENT_INPROGRESS_APPLICATION", "Begin Internship"))
				.add(new Action("PRI_EVENT_ACCESS_NOTES_APPLICATION", "Access Notes"))
				.add(new Action("PRI_EVENT_VIEW_AGREEMENT", "View/Sign Agreement Document"))
				.add(new Action("PRI_EVENT_VIEW_APPLICATION", "View Internship Summary"))
				.add(new Action("PRI_EVENT_VIEW", "View Intern Profile"))
				.add(new Action("PRI_EVENT_DOWNLOAD_INTERNSHIP_AGREEMENT_DOC",
						"Download Internship Agreement Document"))
				.add(new Action("PRI_EVENT_ON_HOLD_APPLICATION", "On Hold"))
				.add(new Action("PRI_EVENT_WITHDRAW_APPLICATION", "Withdraw"))
				/* .addDefaultAction("PRI_EVENT_INPROGRESS_APPLICATION", "Begin Internship") */
				.setCachable(true)
				.setWildcardDepth(2)
				.setPageStart(0)
				.setPageSize(GennySettings.defaultBucketSize());

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
	public SearchEntity getSBEBucketShortlisted(String realm, String name) {
		SearchEntity searchBE = new SearchEntity(name, "Shortlisted")
				.add(new Sort("PRI_CREATED", Ord.DESC))
				.add(new Filter("PRI_CODE", Operator.LIKE, "APP_%"))
				.add(new Filter("PRI_STAGE", Operator.EQUALS, "SHORTLISTED"))
				.add(new Filter("PRI_DISABLED", false))

				/*
				 * .addFilter("PRI_INTERN_CODE.LNK_COMP_INTERNSHIP",
				 * SearchEntity.StringFilter.EQUAL, "[\"SEL_DIGITAL_JOBS\"]")
				 */

				/* .addColumn("PRI_INTERN_NAME", "Name") */
				/* .addColumn("PRI_ASSOC_HC", "Host Company") */
				/* .addColumn("PRI_TITLE", "Internship") */
				.add(new AssociatedColumn("PRI_INTERN_CODE", "PRI_NAME", "Name"))
				.add(new AssociatedColumn("LNK_INTERNSHIP", "LNK_HOST_COMPANY", "PRI_NAME", "Host Company"))
				.add(new AssociatedColumn("LNK_INTERNSHIP", "PRI_NAME", "Internship"))
				.add(new AssociatedColumn("LNK_EDU_PROVIDER", "PRI_NAME", "Education Provider"))

				.add(new Column("PRI_INTERN_EMAIL", "Email"))
				.add(new Column("PRI_ASSOC_INDUSTRY", "Industry"))
				.add(new Column("PRI_ASSOC_OCCUPATION", "Occupation"))
				.add(new Column("PRI_ASSOC_EP", "Edu Provider"))
				.add(new Column("PRI_ADDRESS_FULL", "Address"))
				.add(new Column("PRI_TRANSPORT", "Transport"))
				.add(new Column("PRI_INTERN_MOBILE", "Mobile"))

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
				.add(new Action("PRI_EVENT_INTERVIEW_APPLICATION", "Schedule an Interview"))
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
}
