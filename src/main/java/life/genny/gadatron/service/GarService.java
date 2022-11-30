package life.genny.gadatron.service;
import life.genny.qwandaq.entity.BaseEntity;
import life.genny.qwandaq.utils.BaseEntityUtils;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.util.Date;

@ApplicationScoped
public class GarService {
    static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass());

    @Inject
    BaseEntityUtils beUtils;

    public String createPerson(String defCode, String content) {
        String name = content;
        log.info("Name: " + name);

        BaseEntity defBE = beUtils.getBaseEntityOrNull("gadatron", defCode);
        BaseEntity be = beUtils.create(defBE, name);
        log.info("BE: " + be);

        be = beUtils.addValue(be, "PRI_EMAIL", name.toLowerCase() + "_" + new Date().getTime() + "@gmail.com");
        be = beUtils.addValue(be, "PRI_NAME", name + " " + new Date().getTime());
        be = beUtils.addValue(be, "PRI_FIRSTNAME", name);
        be = beUtils.addValue(be, "PRI_LASTNAME", "" + new Date().getTime());

        beUtils.updateBaseEntity(be);

        return be.getCode();
    }
}
