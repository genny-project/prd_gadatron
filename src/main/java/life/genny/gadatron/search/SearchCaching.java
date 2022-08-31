package life.genny.gadatron.search;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import life.genny.gadatron.Constants;
import life.genny.gadatron.service.SearchTextServiceImpl;
import org.jboss.logging.Logger;

import life.genny.qwandaq.entity.SearchEntity;
import life.genny.qwandaq.utils.CacheUtils;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SearchCaching {

	static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass());

	public void saveToCache(final String realm) {
		log.info("=========================saveToCache=========================");

		final List<SearchEntity> entities = new ArrayList<>();

		entities.add(getSBECompanies(Constants.COMPANIES));

		entities.add(getSBEPersons(Constants.PERSONS));

		entities.add(getSBEDocuments(Constants.DOCUMENTS));

		// save to cache
		entities.stream().forEach(e -> {
			e.setRealm(realm);
			CacheUtils.putObject(realm, e.getCode(), e);
		});
	}

	public SearchEntity getSBECompanies(final String name){
		return new SearchEntity(name, "Host Companies")
				.addSort("PRI_NAME", "NAME", SearchEntity.Sort.ASC)
				.addFilter("PRI_CODE", SearchEntity.StringFilter.LIKE, "CPY_%")
				.addFilter("PRI_IS_HOST_CPY", true)
				.addColumn("PRI_LOGO", "LOGO")
				.addColumn("PRI_NAME", "NAME")
				.addColumn("PRI_CREATED_DATE", "DATE CREATED")
				.addColumn("PRI_STATUS", "STATUS")
				.addColumn("PRI_HC_VALIDATION_DOC_URL", "VALIDATION")
				.addColumn("PRI_PHONE", "PHONE")
				.addColumn("PRI_ADDRESS_FULL", "ADDRESS")
				.addColumn("PRI_ADDRESS_STATE", "STATE")
				.addColumn("PRI_ADDRESS_COUNTRY", "COUNTRY")
				.addColumn("PRI_ABN", "ABN")
				.addColumn("PRI_LEGAL_NAME", "LEGAL NAME")
				.addColumn("PRI_WEBSITE", "WEBSITE")
				.addColumn("PRI_LINKEDIN_URL", "LINKEDIN")
				.addColumn("LNK_COMPANY_INDUSTRY", "COMPANY INDUSTRY")
				.addColumn("LNK_NUMBER_STAFF", "NUMBER OF STAFF")
				.addColumn("PRI_COMPANY_DESCRIPTION", "COMPANY DESCRIPTION")
				.setPageSize(20)
				.setPageStart(0);
	}

	public SearchEntity getSBEDocuments(String name){
		return new SearchEntity(name, "Docments")
				.addSort("PRI_CREATED", "Created", SearchEntity.Sort.DESC)
				.addFilter("PRI_CODE", SearchEntity.StringFilter.LIKE, "CPY_%")
				.addFilter("PRI_IS_INTERNSHIP", true)
				.addColumn("PRI_COMPANY_LOGO", "COMPANY LOGO")
				.addColumn("PRI_TITLE", "TITLE")
				.addColumn("PRI_CREATED_DATE", "DATE CREATED")
				.addColumn("PRI_STATUS", "STATUS")
				.addColumn("PRI_LEGAL_NAME", "HOST COMPANY")
				.addColumn("PRI_ADDRESS_FULL", "ADDRESS")
				.addColumn("PRI_ADDRESS_STATE", "STATE")
				.addColumn("PRI_ADDRESS_COUNTRY", "COUNTRY")
				.addColumn("PRI_START_DATE", "PROPOSED START DATE")
				.addColumn("LNK_COMPANY_INDUSTRY", "INDUSTRY")
				.addColumn("PRI_ASSOC_NO_OF_INTERNS", "NUMBER OF ALLOWED INTERNS")
				.addColumn("PRI_SOFTWARE", "SOFTWARE")
				.addColumn("PRI_WORKSITE", "WORKSITE")
				.addColumn("PRI_SPECIALISATION", "SPECIALISATION")
				.addColumn("PRI_ROLES_AND_RESPONSIBILITIES", "ROLES AND RESPONSIBILITIES")
				.addColumn("PRI_BASE_LEARNING_OUTCOMES", "BASE LEARNING OUTCOMES")
				.addColumn("PRI_BASE_LEARNING_OUTCOMES", "TECHNICAL SKILLS LEARNING OUTCOMES")
				.addColumn("PRI_WHICH_DAYS_STRIPPED", "WHICH DAYS")
				.addColumn("PRI_NO_OF_INTERNS", "NO OF INTERNS")
				.addColumn("PRI_INTRO_VIDEO", "VIDEO")
				.setPageSize(20)
				.setPageStart(0);
	}

	public SearchEntity getSBEPersons(final String name){
		return new SearchEntity(name, "Persons")
				.addSort("PRI_CREATED", "Created", SearchEntity.Sort.DESC)
				.addFilter("PRI_CODE", SearchEntity.StringFilter.LIKE, "PER_%")
				.addFilter("PRI_IS_INTERN", true)
				.addColumn("LNK_PERSON", "PERSON")
				.addColumn("PRI_PROFILE_PICTURE", "PROFILE PICTURE")
				.addColumn("PRI_NAME", "NAME")
				.addColumn("PRI_CREATED_DATE", "DATE CREATED")
				.addColumn("PRI_PREFERRED_NAME", "PREFERRED NAME")
				.addColumn("PRI_STATUS", "STATUS")
				.addColumn("PRI_IS_PROFILE_COMPLETED", "PROFILE STATUS")
				.addColumn("PRI_INDUSTRY", "INDUSTRY")
				.addColumn("PRI_OCCUPATION", "OCCUPATION")
				.addColumn("PRI_EDU_PROVIDER_NAME", "EDUCATION PROVIDER")
				.addColumn("PRI_INTERNSHIP_DURATION", "INTERNSHIP DURATION")
				.addColumn("PRI_DAYS_PER_WEEK", "DAYS PER WEEK")
				.addColumn("PRI_EMAIL", "EMAIL")
				.addColumn("PRI_MOBILE", "MOBILE")
				.addColumn("PRI_AGENT_NAME", "AGENT NAME")
				.addColumn("PRI_ASSOC_AGENCY", "ASSOC AGENCY")
				.addColumn("PRI_START_DATE", "START DATE")
				.addColumn("PRI_ADDRESS_SUBURB", "SUBURB")
				.addColumn("PRI_ADDRESS_STATE", "STATE")
				.addColumn("PRI_ADDRESS_COUNTRY", "COUNTRY")
				.addColumn("PRI_VIDEO_URL", "VIDEO URL")
				.addColumn("PRI_PERCENTAGE_JOURNALS", "COMPLETED JOURNALS")
				.addColumn("PRI_IS_EDU_PROVIDER_ACADEMY", "ACADEMY")
				.addColumn("PRI_INTERN_STUDENT_ID", "STUDENT ID")
				.addColumn("PRI_UPDATE_LINKEDIN", "LINKEDIN URL")
				.setPageSize(20)
				.setPageStart(0);
	}

	public SearchEntity getSBEMessages(final String name){
		return new SearchEntity(name, "Messages")
				.addSort("PRI_CREATED", "Created", SearchEntity.Sort.DESC)
				.addFilter("PRI_CODE", SearchEntity.StringFilter.LIKE, "PER_%")
				.addFilter("PRI_IS_INTERN", true)
				.addColumn("LNK_PERSON", "PERSON")
				.addColumn("PRI_PROFILE_PICTURE", "PROFILE PICTURE")
				.addColumn("PRI_NAME", "NAME")
				.addColumn("PRI_CREATED_DATE", "DATE CREATED")
				.addColumn("PRI_PREFERRED_NAME", "PREFERRED NAME")
				.addColumn("PRI_STATUS", "STATUS")
				.addColumn("PRI_IS_PROFILE_COMPLETED", "PROFILE STATUS")
				.addColumn("PRI_INDUSTRY", "INDUSTRY")
				.addColumn("PRI_OCCUPATION", "OCCUPATION")
				.addColumn("PRI_EDU_PROVIDER_NAME", "EDUCATION PROVIDER")
				.addColumn("PRI_INTERNSHIP_DURATION", "INTERNSHIP DURATION")
				.addColumn("PRI_DAYS_PER_WEEK", "DAYS PER WEEK")
				.addColumn("PRI_EMAIL", "EMAIL")
				.addColumn("PRI_MOBILE", "MOBILE")
				.addColumn("PRI_AGENT_NAME", "AGENT NAME")
				.addColumn("PRI_ASSOC_AGENCY", "ASSOC AGENCY")
				.addColumn("PRI_START_DATE", "START DATE")
				.addColumn("PRI_ADDRESS_SUBURB", "SUBURB")
				.addColumn("PRI_ADDRESS_STATE", "STATE")
				.addColumn("PRI_ADDRESS_COUNTRY", "COUNTRY")
				.addColumn("PRI_VIDEO_URL", "VIDEO URL")
				.addColumn("PRI_PERCENTAGE_JOURNALS", "COMPLETED JOURNALS")
				.addColumn("PRI_IS_EDU_PROVIDER_ACADEMY", "ACADEMY")
				.addColumn("PRI_INTERN_STUDENT_ID", "STUDENT ID")
				.addColumn("PRI_UPDATE_LINKEDIN", "LINKEDIN URL")
				.setPageSize(20)
				.setPageStart(0);
	}

}