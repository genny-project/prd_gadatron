package life.genny.gadatron.search;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import life.genny.gadatron.Constants;
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

		entities.add(getSBECompanies(Constants.SBE_TREE_ITEM_COMPANIES));
		entities.add(getSBEPersons(Constants.SBE_TREE_ITEM_PERSONS));
		entities.add(getSBEDocuments(Constants.SBE_TREE_ITEM_DOCUMENTS));
		entities.add(getSBEMessages(Constants.SBE_TREE_ITEM_MESSAGES));

		// save to cache
		entities.stream().forEach(e -> {
			e.setRealm(realm);
			CacheUtils.putObject(realm, e.getCode(), e);
		});
	}

	public SearchEntity getSBECompanies(final String name){
		return new SearchEntity(name, "Companies")
				.addSort(Constants.PRI_NAME, "NAME", SearchEntity.Sort.ASC)
				.addFilter(Constants.PRI_CODE, SearchEntity.StringFilter.LIKE, "CPY_%")
				.addColumn(Constants.PRI_LOGO, "LOGO")
				.addColumn(Constants.PRI_NAME, "NAME")
				.addColumn(Constants.PRI_CREATED_DATE, "DATE CREATED")
				.addColumn(Constants.PRI_STATUS, "STATUS")
				.addColumn(Constants.PRI_HC_VALIDATION_DOC_URL, "VALIDATION")
				.addColumn(Constants.PRI_PHONE, "PHONE")
				.addColumn(Constants.PRI_ADDRESS_FULL, "ADDRESS")
				.addColumn(Constants.PRI_ADDRESS_STATE, "STATE")
				.addColumn(Constants.PRI_ADDRESS_COUNTRY, "COUNTRY")
				.addColumn(Constants.PRI_ABN, "ABN")
				.addColumn(Constants.PRI_LEGAL_NAME, "LEGAL NAME")
				.addColumn(Constants.PRI_WEBSITE, "WEBSITE")
				.addColumn(Constants.PRI_LINKEDIN_URL, "LINKEDIN")
				.addColumn(Constants.LNK_COMPANY_INDUSTRY, "COMPANY INDUSTRY")
				.addColumn(Constants.LNK_NUMBER_STAFF, "NUMBER OF STAFF")
				.addColumn(Constants.PRI_COMPANY_DESCRIPTION, "COMPANY DESCRIPTION")
				.setPageSize(20)
				.setPageStart(0);
	}

	public SearchEntity getSBEDocuments(String name){
		return new SearchEntity(name, "Documents")
				.addSort(Constants.PRI_CREATED, Constants.CREATED, SearchEntity.Sort.DESC)
				.setPageSize(20)
				.setPageStart(0);
	}

	public SearchEntity getSBEPersons(final String name){
		return new SearchEntity(name, "Persons")
				.addSort(Constants.PRI_CREATED, Constants.CREATED, SearchEntity.Sort.DESC)
				.addFilter(Constants.PRI_CODE, SearchEntity.StringFilter.LIKE, "PER_%")
				.addFilter(Constants.PRI_IS_INTERN, true)
				.addColumn(Constants.LNK_PERSON, "PERSON")
				.addColumn(Constants.PRI_PROFILE_PICTURE, "PROFILE PICTURE")
				.addColumn(Constants.PRI_NAME, "NAME")
				.addColumn(Constants.PRI_CREATED_DATE, "DATE CREATED")
				.addColumn(Constants.PRI_PREFERRED_NAME, "PREFERRED NAME")
				.addColumn(Constants.PRI_STATUS, "STATUS")
				.addColumn(Constants.PRI_IS_PROFILE_COMPLETED, "PROFILE STATUS")
				.addColumn(Constants.PRI_INDUSTRY, "INDUSTRY")
				.addColumn(Constants.PRI_OCCUPATION, "OCCUPATION")
				.addColumn(Constants.PRI_EMAIL, "EMAIL")
				.addColumn(Constants.PRI_MOBILE, "MOBILE")
				.addColumn(Constants.PRI_ADDRESS_SUBURB, "SUBURB")
				.addColumn(Constants.PRI_ADDRESS_STATE, "STATE")
				.addColumn(Constants.PRI_ADDRESS_COUNTRY, "COUNTRY")
				.addColumn(Constants.PRI_VIDEO_URL, "VIDEO URL")
				.addColumn(Constants.PRI_UPDATE_LINKEDIN, "LINKEDIN URL")
				.setPageSize(20)
				.setPageStart(0);
	}

	public SearchEntity getSBEMessages(final String name){
		return new SearchEntity(name, "Messages")
				.addSort(Constants.PRI_CREATED, Constants.CREATED, SearchEntity.Sort.DESC)
				.setPageSize(20)
				.setPageStart(0);
	}

}