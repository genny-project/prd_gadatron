package life.genny.gadatron.service;

import io.quarkus.runtime.StartupEvent;
import life.genny.qwandaq.Answer;
import life.genny.qwandaq.EEntityStatus;
import life.genny.qwandaq.entity.BaseEntity;
import life.genny.qwandaq.entity.search.SearchEntity;
import life.genny.qwandaq.entity.search.trait.Filter;
import life.genny.qwandaq.entity.search.trait.Operator;
import life.genny.qwandaq.models.UserToken;
import life.genny.qwandaq.utils.BaseEntityUtils;
import life.genny.qwandaq.utils.DatabaseUtils;
import life.genny.qwandaq.utils.QwandaUtils;
import life.genny.qwandaq.utils.SearchUtils;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class WayanService {
    static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass());

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

    @Inject
    DatabaseUtils databaseUtils;

    /**
     * Execute on start up.
     *
     * @param ev The startup event
     */
    void onStart(@Observes StartupEvent ev) {
        log.info("Starting my own service");

//        databaseUtils.
    }

    public BaseEntity getTestBaseEntity(String beCode) {
        BaseEntity be = beUtils.getBaseEntityOrNull(beCode);
        return be;
    }

    public String getTestBaseEntityJson(String beCode) {
        BaseEntity be = beUtils.getBaseEntityOrNull(beCode);
        return jsonb.toJson(be);
    }

    public String createAPerson2(String content) {
        if (content != null && !content.isEmpty()) {
            final String name = content;
            BaseEntity personDef = beUtils.getBaseEntity("DEF_PERSON");
            BaseEntity person = beUtils.create(personDef, name);
            String[] names = name.split(" ");
            if (names.length > 0) {
                person = beUtils.addValue(person,"PRI_FIRSTNAME", names[0]);
                StringBuilder lastName = new StringBuilder();
                for (int i = 1; i < names.length; i++) {
                    if (lastName.length() > 0) {
                        lastName.append(" ");
                    }
                    lastName.append(names[i]);
                }
                person = beUtils.addValue(person,"PRI_LASTNAME", lastName.toString());
            } else {
                person = beUtils.addValue(person,"PRI_FIRSTNAME", name);
            }
            person = beUtils.addValue(person,"PRI_UUID", person.getCode().substring("PER_".length()));
            person = beUtils.addValue(person,"PRI_EMAIL", String.join(".", names)+"@gada.io");
            beUtils.updateBaseEntity(person);
            log.info("New Person:"+person.getCode());
            return person.getCode();
        }
        return null;
    }

    public String createAPerson(Map<String, String> content) {
        if (content != null && !content.isEmpty()) {
            String name = "Person";
            if (content.containsKey("ATT_PRI_FIRSTNAME")) {
                name = content.get("ATT_PRI_FIRSTNAME");
            }
            BaseEntity personDef = beUtils.getBaseEntity("DEF_PERSON");
            String uuid = personDef.getValue("PRI_PREFIX").get()+"_"+(content.containsKey("PRI_UUID")? content.get("PRI_UUID"): UUID.randomUUID().toString());
            BaseEntity person = beUtils.create(personDef, name, uuid);

            for (String key : content.keySet()) {
                log.info("data from message: "+key+":"+content.get(key));
                qwandaUtils.saveAnswer(new Answer(userToken.getUserCode(), person.getCode(), key, content.get(key)));
            }
            return person.getCode();
        }
        return null;
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
