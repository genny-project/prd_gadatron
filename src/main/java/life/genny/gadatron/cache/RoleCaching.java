package life.genny.gadatron.cache;

import static life.genny.gadatron.constants.GadatronConstants.ADMIN;
import static life.genny.gadatron.constants.GadatronConstants.ADMIN_ROLE;
import static life.genny.gadatron.constants.GadatronConstants.PRODUCT_CODE;

import static life.genny.qwandaq.datatype.CapabilityMode.ADD;
import static life.genny.qwandaq.datatype.CapabilityMode.EDIT;
import static life.genny.qwandaq.datatype.CapabilityMode.VIEW;

import java.lang.invoke.MethodHandles;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import life.genny.qwandaq.attribute.Attribute;
import life.genny.qwandaq.entity.BaseEntity;
import life.genny.qwandaq.managers.capabilities.CapabilitiesManager;
import life.genny.qwandaq.managers.capabilities.role.RoleBuilder;
import life.genny.qwandaq.utils.BaseEntityUtils;

@ApplicationScoped
public class RoleCaching {

	static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass());

	@Inject
	BaseEntityUtils beUtils;

	@Inject
	CapabilitiesManager capMan;

	/*
	 * Return a list of the roles (for testing)
	 */
	public void saveToCache() {

		// Not keen on storing this map for longer than we have to
		// Get capabilities
		Map<String, Attribute> capabilities = loadCapabilityAttributes();

		// admin role
		BaseEntity admin = new RoleBuilder(capMan, ADMIN_ROLE, "Admin", PRODUCT_CODE)
			.setCapabilityMap(capabilities)
			.addCapability(ADMIN, VIEW, ADD, EDIT)

			// Views addView(capabilityCode) == addCapability(capabilityCode, VIEW)
			.build();

	}

	private Map<String, Attribute> loadCapabilityAttributes() {
		String[][] attribData = {
			{ADMIN, "Manipulate Admin"},
		};

		return capMan.getCapabilityMap(PRODUCT_CODE, attribData);
	}

	// Leaving this here in case we want to further test roles
	//
	// public void testRoles(List<BaseEntity> roles) {
	// 	BaseEntity admin = roles.get(0);
	// 	BaseEntity tenant = roles.get(1);
		
	// 	testRoleCap(admin, "CAP_ADMIN", false, VIEW);
	// 	testRoleCap(admin, "CAP_TENANT", false, ADD);
	// 	testRoleCap(admin, "CAP_ADMIN", false, EDIT);
	// 	testRoleCap(admin, "CAP_" + PROPERTY_VIEW, true, ADD, VIEW, EDIT);
	// 	testRoleCap(admin, "CAP_ADMIN", true, VIEW, ADD, EDIT, DELETE);
	// 	testRoleCap(admin, "CAP_ADMIN", false, VIEW, ADD, EDIT, DELETE);
	// }

	// private boolean testRoleCap(BaseEntity be, String rawAttr, boolean hasAll, CapabilityMode... modes) {
	// 	log.info("Test: " + be.getCode() + " has " + (hasAll ? "all " : "") + rawAttr + ": " + capMan.getModeString(modes));
	// 	boolean result;
	// 	try {
	// 		result = capMan.roleHasCapability(be, rawAttr, hasAll, modes);
	// 		log.info("		- Result: " + result);
	// 		return result;
	// 	} catch (RoleException e) {
	// 		// TODO Auto-generated catch block
	// 		e.printStackTrace();
	// 	}
	// 	return false;
	// }

}
