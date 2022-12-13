package life.genny.gadatron.service;

import life.genny.qwandaq.attribute.EntityAttribute;
import life.genny.qwandaq.entity.BaseEntity;
import life.genny.qwandaq.entity.Definition;
import life.genny.qwandaq.models.UserToken;
import life.genny.qwandaq.utils.BaseEntityUtils;
import life.genny.qwandaq.utils.QwandaUtils;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.invoke.MethodHandles;

@ApplicationScoped
public class AmrizalService {

    static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass());

    @Inject
    BaseEntityUtils beUtils;

    @Inject
    QwandaUtils qwandaUtils;

    @Inject
    UserToken userToken;

    public String createPersonAmrizal(String def, String value) {
        Definition beDefinition = beUtils.getDefinition(def);

        BaseEntity person = beUtils.create(beDefinition);

        String[] fullName = value.split(" ");

        person.setName(value);

        person = beUtils.addValue(person, "PRI_FIRSTNAME", fullName[0]);
        person = beUtils.addValue(person, "PRI_LASTNAME", fullName[1]);
        person = beUtils.addValue(person, "PRI_EMAIL", (String.join(".", fullName) + "@gada.io"));

        beUtils.updateBaseEntity(person);

        // for (EntityAttribute ea : person.getBaseEntityAttributes())
        // log.info(ea.getAttributeCode() + ": " + ea.getValueString());

        return person.getCode();
    }

    public void createQuestionGroup(String def, String value) {
        String code = "QQQ_QUESTION_GROUP";
        Definition beDef = beUtils.getDefinition(code);

        BaseEntity qg = beUtils.create(beDef);

        log.info(code + " length: " + qg.getBaseEntityAttributes().size());

        for (EntityAttribute ea : qg.getBaseEntityAttributes())
            log.info("attributeCode: " + ea.getAttributeCode());
    }
}
