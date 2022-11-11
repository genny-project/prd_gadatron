package life.genny.gadatron.cache;

import static life.genny.gadatron.constants.GadatronConstants.ADMIN;
import static life.genny.gadatron.constants.GadatronConstants.ADMIN_ROLE;
import static life.genny.gadatron.constants.GadatronConstants.PRODUCT_CODE;

//import static life.genny.qwandaq.datatype.capability.PermissionMode.*;
import static life.genny.qwandaq.datatype.capability.core.node.PermissionMode.*;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import life.genny.qwandaq.attribute.Attribute;
import life.genny.qwandaq.entity.BaseEntity;
import life.genny.qwandaq.entity.SearchEntity;
import life.genny.qwandaq.entity.search.trait.Filter;
import life.genny.qwandaq.entity.search.trait.Operator;
import life.genny.qwandaq.managers.capabilities.CapabilitiesManager;
import life.genny.qwandaq.managers.capabilities.role.RoleBuilder;
import life.genny.qwandaq.managers.capabilities.role.RoleManager;
import life.genny.qwandaq.utils.BaseEntityUtils;
import life.genny.qwandaq.utils.DatabaseUtils;
import life.genny.qwandaq.utils.SearchUtils;

@ApplicationScoped
public class RoleCaching {

	static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass());

	@Inject
	BaseEntityUtils beUtils;

	@Inject
	CapabilitiesManager capMan;

	@Inject
	RoleManager roleManager;

	@Inject
	SearchUtils searchUtils;

	static final String productCode = "Gadatron";

	public void saveToCache() {
		Map<String, Attribute> capabilities = loadCapabilityAttributes();

		// admin role
		BaseEntity admin = new RoleBuilder(ADMIN_ROLE, "Admin", PRODUCT_CODE)
				.setCapabilityMap(capabilities)
				.addCapability(ADMIN).view(ALL).add(ALL).edit(ALL).build()

				// Views addView(capabilityCode) == addCapability(capabilityCode, VIEW)
				.build();

		// Set TestUser
		// fetch he baseentity for the user with email testuser@gada.io
		SearchEntity searchEntity = new SearchEntity("SBE_EMAIL", "Search for Email")
				.add(new Filter(Attribute.PRI_CODE, Operator.LIKE, "PER_%"))
				.add(new Filter("PRI_EMAIL", Operator.EQUALS, "testuser@gada.io"))
				.setPageStart(0)
				.setPageSize(100);

		searchEntity.setRealm(productCode);

		List<BaseEntity> bes = searchUtils.searchBaseEntitys(searchEntity);
		BaseEntity testUserBE = null;

		if ((bes != null) && (bes.size() > 0)) {
			testUserBE = bes.get(0);
			testUserBE = beUtils.getBaseEntity(productCode, testUserBE.getCode());
		} else {
			log.error("No test User - testuser@gada.io found!");
		}
		// add the admin role to the user
		roleManager.attachRole(testUserBE, "ADMIN");
	}

	private Map<String, Attribute> loadCapabilityAttributes() {
		String[][] attribData = {
				{ ADMIN, "Manipulate Admin" },
		};

		return capMan.getCapabilityAttributeMap(PRODUCT_CODE, attribData);
	}
}
