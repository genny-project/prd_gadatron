package life.genny.gadatron.testing;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.jboss.logging.Logger;

import life.genny.qwandaq.EEntityStatus;
import life.genny.qwandaq.entity.BaseEntity;
import life.genny.qwandaq.entity.SearchEntity;
import life.genny.qwandaq.entity.search.trait.Filter;
import life.genny.qwandaq.entity.search.trait.Operator;
import life.genny.qwandaq.models.UserToken;
import life.genny.qwandaq.utils.BaseEntityUtils;
import life.genny.qwandaq.utils.QwandaUtils;
import life.genny.qwandaq.utils.SearchUtils;

@ApplicationScoped
public class TestServiceImpl implements TestService {

        static final Logger log = Logger.getLogger(TestServiceImpl.class);

        static Jsonb jsonb = JsonbBuilder.create();

        static final String productCode = "gadatron";

        @Inject
        UserToken userToken;

        @Inject
        QwandaUtils qwandaUtils;

        @Inject
        BaseEntityUtils beUtils;

        @Inject
        SearchUtils searchUtils;

        public BaseEntity getTestBaseEntity(String beCode) {
                BaseEntity be = beUtils.getBaseEntityOrNull(beCode);
                return be;
        }

        public String getTestBaseEntityJson(String beCode) {
                BaseEntity be = beUtils.getBaseEntityOrNull(beCode);
                return jsonb.toJson(be);
        }

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
