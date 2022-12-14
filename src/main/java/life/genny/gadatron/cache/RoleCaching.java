package life.genny.gadatron.cache;

import life.genny.qwandaq.attribute.Attribute;
import life.genny.qwandaq.datatype.capability.core.node.CapabilityMode;
import life.genny.qwandaq.entity.BaseEntity;
import life.genny.qwandaq.entity.search.SearchEntity;
import life.genny.qwandaq.entity.search.trait.Filter;
import life.genny.qwandaq.entity.search.trait.Operator;
import life.genny.qwandaq.managers.capabilities.CapabilitiesManager;
import life.genny.qwandaq.managers.capabilities.role.RoleBuilder;
import life.genny.qwandaq.managers.capabilities.role.RoleManager;
import life.genny.qwandaq.utils.BaseEntityUtils;
import life.genny.qwandaq.utils.SearchUtils;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;

import static life.genny.gadatron.constants.GadatronConstants.*;
import static life.genny.qwandaq.datatype.capability.core.node.PermissionMode.*;

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

	static final String productCode = "gadatron";

	public void saveToCache() {

		// Not keen on storing this map for longer than we have to
		// Get capabilities
		Map<String, Attribute> capabilities = loadCapabilityAttributes();

		new RoleBuilder(ADMIN_ROLE, "Admin", PRODUCT_CODE)
				.setCapabilityMap(capabilities)
				.setRoleRedirect(QUE_TABLE_PERSON)
				.addCapability(ADMIN).view(ALL).add(ALL).edit(ALL).delete(ALL)
				.addNode(CapabilityMode.ADD, ALL, false).build()
				.addCapability(BALI_PERSON).view(ALL).add(ALL).edit(ALL).delete(ALL).build()
				.addCapability(PERSON).view(ALL).add(ALL).edit(ALL).delete(ALL).build()

				.addCapability(COMPANY).view(ALL).build()
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
			// add the admin role to the user
			roleManager.attachRole(testUserBE, "ADMIN");
		} else {
			log.error("No test User - testuser@gada.io found!");
		}
	}

	private Map<String, Attribute> loadCapabilityAttributes() {
		String[][] attribData = {
				{ ADMIN, "Manipulate Admin" },
				{ PERSON, "Manipulate Person" },
				{ BALI_PERSON, "Manipulate Bali Person" },
				{ DASHBOARD, "Dashboard View" },
				{ COMPANY, "Manipulate Company" },
				{ ACTION_APPLY, "Use Apply Action" }
		};

		return capMan.getCapabilityAttributeMap(PRODUCT_CODE, attribData);
	}
}
