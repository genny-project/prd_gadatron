package life.genny.gadatron.service;
import io.quarkus.runtime.StartupEvent;
import life.genny.qwandaq.Question;
import life.genny.qwandaq.QuestionQuestion;
import life.genny.qwandaq.attribute.Attribute;
import life.genny.qwandaq.datatype.DataType;
import life.genny.qwandaq.entity.BaseEntity;
import life.genny.qwandaq.utils.BaseEntityUtils;
import life.genny.qwandaq.utils.DatabaseUtils;
import life.genny.qwandaq.utils.QwandaUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.util.Date;

@ApplicationScoped
public class GarService {
    static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass());

    @Inject
    BaseEntityUtils beUtils;

    @Inject
    protected QwandaUtils qwandaUtils;

    @Inject
    DatabaseUtils dbUtils;

    @ConfigProperty(name = "GENNY_CLIENT_ID")
    String realm;

    void onStart(@Observes StartupEvent ev) {
        log.info("Gar service is starting with realm: " + realm);
        setupQuestionData();
    }

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

    public void setupQuestionData() {
        // setup attribute dataType
        DataType dataType = new DataType(String.class);
        dataType.setTypeName("Event");
        dataType.setComponent("button");
        dataType.setDttCode("DTT_EVENT");

        // create attribute
        //Attribute attribute = new Attribute("EVT_TEST_GARDIARY", "Gardiary Test Event", dataType);
        //attribute = qwandaUtils.saveAttribute(realm, attribute);
        String attributeCode = "EVT_TEST_GARDIARY";
        Attribute attribute = findAttributeByCode(realm, attributeCode);
        if (attribute == null) {
            attribute = new Attribute("EVT_TEST_GARDIARY", "Gardiary Test Event", dataType);
            attribute.setRealm(realm);
            dbUtils.saveAttribute(attribute);
            attribute = findAttributeByCode(realm, attributeCode);
        }
        log.info("Attribute: " + attribute);

        // create question
        Question question = new Question("QUE_TEST_GARDIARY", "Gardiary Question", attribute);
        question.setRealm(realm);
        question = dbUtils.saveQuestion(question);
        log.info("Question: " + question);

        // create question_question (sourceCode: QUE_SIDEBAR)
        Question queSidebar = dbUtils.findQuestionByCode(realm, "QUE_SIDEBAR"); // this shouldn't be null

        QuestionQuestion questionQuestion = new QuestionQuestion(queSidebar, question, 5.0);
        questionQuestion.setFormTrigger(Boolean.FALSE);
        questionQuestion.setCreateOnTrigger(Boolean.FALSE);
        questionQuestion.setRealm(realm);
        dbUtils.saveQuestionQuestion(questionQuestion);
        log.info("QuestionQuestion 1: " + questionQuestion);

        // create question_question (sourceCode: QUE_TEST_GARDIARY)
        questionQuestion = createQuestionQuestion(realm, question, "QUE_DOB", 1.0, false);
        log.info("QuestionQuestion 2: " + questionQuestion);

        questionQuestion = createQuestionQuestion(realm, question, "QUE_EMAIL", 2.0, true);
        log.info("QuestionQuestion 3: " + questionQuestion);

        questionQuestion = createQuestionQuestion(realm, question, "QUE_FIRSTNAME", 3.0, true);
        log.info("QuestionQuestion 4: " + questionQuestion);

        questionQuestion = createQuestionQuestion(realm, question, "QUE_LASTNAME", 4.0, false);
        log.info("QuestionQuestion 5: " + questionQuestion);

        questionQuestion = createQuestionQuestion(realm, question, "QUE_DOB", 5.0, true);
        log.info("QuestionQuestion 6: " + questionQuestion);
    }

    private QuestionQuestion createQuestionQuestion(String realm, Question sourceQuestion, String targetQuestionCode, Double weight, Boolean mandatory) {
        Question targetQuestion = dbUtils.findQuestionByCode(realm, targetQuestionCode);
        log.info("targetQuestion: " + targetQuestion);
        QuestionQuestion questionQuestion = new QuestionQuestion(sourceQuestion, targetQuestion, weight);
        questionQuestion.setMandatory(mandatory);
        questionQuestion.setFormTrigger(Boolean.FALSE);
        questionQuestion.setCreateOnTrigger(Boolean.FALSE);
        questionQuestion.setRealm(realm);
        return dbUtils.saveQuestionQuestion(questionQuestion);
    }

    public Attribute findAttributeByCode(String realm, String code) {
        try {
            return dbUtils.findAttributeByCode(realm, code);
        } catch (Exception e) {
            log.error("Error:", e);
            return null;
        }
    }
}
