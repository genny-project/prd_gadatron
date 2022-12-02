package life.genny.gadatron.service;

import io.quarkus.runtime.StartupEvent;
import life.genny.qwandaq.EEntityStatus;
import life.genny.qwandaq.entity.BaseEntity;
import life.genny.qwandaq.entity.search.SearchEntity;
import life.genny.qwandaq.entity.search.trait.Filter;
import life.genny.qwandaq.entity.search.trait.Operator;
import life.genny.qwandaq.models.ServiceToken;
import life.genny.qwandaq.models.UserToken;
import life.genny.qwandaq.utils.BaseEntityUtils;
import life.genny.qwandaq.utils.QwandaUtils;
import life.genny.qwandaq.utils.SearchUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.lang.invoke.MethodHandles;
import java.util.List;

@ApplicationScoped
public class IrvanService {

        static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass());

        static Jsonb jsonb = JsonbBuilder.create();

        // TO-DO
        // get product code from git properties
        // set to be dynamic
        //
        @ConfigProperty(name = "quarkus.application.name", defaultValue = "gadatron")
        public String productCode;

        // TO-DO
        // Try to use Service User Token
        // Available on start
        // STEAL IT!!!
        //
        @Inject
        UserToken userToken;
        // vvvvvvvvvvvvvvvvvvvvvvv
        @Inject
        ServiceToken serviceToken;

        @Inject
        QwandaUtils qwandaUtils;

        @Inject
        BaseEntityUtils beUtils;

        @Inject
        SearchUtils searchUtils;

        void onStart(@Observes StartupEvent ev) {
                // service.fullServiceInit();
                // log.info(MethodHandles.lookup().lookupClass() + ' init');
                log.info("Irvan starting");
                // createPersonBal("DEF_PERSON", "firstname");
        }

        public BaseEntity getTestBaseEntity(String beCode) {
                BaseEntity be = beUtils.getBaseEntityOrNull(beCode);
                return be;
        }

        public String getTestBaseEntityJson(String beCode) {
                BaseEntity be = beUtils.getBaseEntityOrNull(beCode);
                return jsonb.toJson(be);
        }

        // Function to be called from shell scripts
        public String createPersonIrvan(String defcode, String firstName) {
                log.info(defcode);
                log.info(firstName);
                BaseEntity defBE = beUtils.getBaseEntity(defcode);
                BaseEntity be = beUtils.create(defBE, firstName);

                be = beUtils.addValue(be, "PRI_FIRSTNAME", firstName);
                be = beUtils.addValue(be, "PRI_EMAIL", "irvan.pradita+" + firstName + "@onebyone.co");
                beUtils.updateBaseEntity(be);
                log.info(be);
                return be.getCode();
        }

        // public void createAnswerIrvan() {
        // BaseEntity be
        // }

        public void createTestMsg() {
                // MSG_IM_INTERN_APPLIED
                BaseEntity be = beUtils.getBaseEntityOrNull("MSG_GT_TEST");
                beUtils.addValue(be, "PRI_MILESTONE", "[\"BUCKET1\"]");

                // ATT_PRI_RECIPIENT_LNK

                beUtils.addValue(be, "PRI_RECIPIENT_LNK", "LNK_PERSON"); // other examples LNK_COMPANY:LNK_ADMIN
                // ATT_PRI_SENDER_LNK
                BaseEntity sender = getBaseEntityByEmail("adamcrow63@gmail.com");

                beUtils.addValue(be, "PRI_SENDER_LNK", userToken.getUserCode());
                // Save this target
                beUtils.updateBaseEntity(be);
        }

        public BaseEntity getBaseEntityByEmail(String email) {

                SearchEntity searchEntity = new SearchEntity("SBE_EMAIL", "Fetch BE associated with Email")
                                .add(new Filter("PRI_CODE", Operator.STARTS_WITH, "PER_"))
                                .add(new Filter("PRI_EMAIL", Operator.LIKE, "%" + email + "%"))
                                .setPageStart(0)
                                .setPageSize(1)
                                .setRealm(productCode);

                List<BaseEntity> personWithEmail = searchUtils.searchBaseEntitys(searchEntity);

                if (personWithEmail.size() > 0) {
                        log.info("Found " + personWithEmail.size() + " person with email " + email + " and be "
                                        + personWithEmail.get(0).getCode());
                        return personWithEmail.get(0);
                } else {
                        return null;
                }

        }

        public String getTestBaseEntityApplicationCode(String beCode) {

                createTestMsg(); // ensure the message is updated to use the new milestone

                BaseEntity applicationBE = getTestBaseEntity(beCode);

                // Now add test entityAttributes
                applicationBE.setName("Bob Console Test");
                applicationBE.setStatus(EEntityStatus.ACTIVE);

                applicationBE = beUtils.addValue(applicationBE, "PRI_TEST3", "Any");
                applicationBE = beUtils.addValue(applicationBE, "PRI_START_DATE", "16-Aug-22");

                // Save this target
                beUtils.updateBaseEntity(applicationBE);

                return applicationBE.getCode();
        }

}
