package life.genny.gadatron.cache;

import java.lang.invoke.MethodHandles;

import javax.inject.Inject;

import org.jboss.logging.Logger;

import life.genny.qwandaq.utils.CacheUtils;
import life.genny.qwandaq.utils.CapabilityUtils;

public class RoleCaching {

	static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass());

	@Inject
	CapabilityUtils capabilityUtils;

	public static void saveToCache() {

		String productCode = "gadatron";

		// CacheUtils.putObject(productCode, "ROL_PERSON:REDIRECT", "DASHBOARD_VIEW");
	}

}
