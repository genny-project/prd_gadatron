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
    public static final String personEntityCode = "PER_D0E77A02-8D40-4C37-9C54-9DB9EC99";

    @Inject
    BaseEntityUtils beUtils;

    @Inject
    protected QwandaUtils qwandaUtils;

    @Inject
    DatabaseUtils dbUtils;

    @ConfigProperty(name = "quarkus.application.name", defaultValue = "gadatron")
    public String productCode;

    void onStart(@Observes StartupEvent ev) {
        log.info("Gar service is starting with productCode: " + productCode);
        setupQuestionData();
        log.info("Completed GarService init startup");
    }

    public String createPerson(String defCode, String content) {
        String firstname = content;
        log.info("Firstname: " + firstname);
        String lastname = "" + new Date().getTime();

        BaseEntity defBE = beUtils.getBaseEntityOrNull(productCode, defCode);
        BaseEntity be = beUtils.create(defBE, firstname + " " + lastname);
        log.info("BE: " + be);

        be = beUtils.addValue(be, "PRI_EMAIL", firstname.toLowerCase() + "_" + lastname + "@gmail.com");
        be = beUtils.addValue(be, "PRI_NAME", firstname + " " + lastname);
        be = beUtils.addValue(be, "PRI_FIRSTNAME", firstname);
        be = beUtils.addValue(be, "PRI_LASTNAME", lastname);

        beUtils.updateBaseEntity(be);

        return be.getCode();
    }

    public void setupQuestionData() {
        BaseEntity be = beUtils.getBaseEntityOrNull(productCode, personEntityCode);
        log.info("BE: " + be);

        if (be == null) {
            String firstname = "Michael";
            String lastname = "Brown";
            BaseEntity defBE = beUtils.getBaseEntityOrNull(productCode, "DEF_PERSON");
            be = beUtils.create(defBE, firstname + " " + lastname, personEntityCode);
            log.info("BE: " + be);

            be = beUtils.addValue(be, "PRI_DOB", "");
            be = beUtils.addValue(be, "PRI_EMAIL",
                    firstname.toLowerCase() + "_" + lastname.toLowerCase() + "@gmail.com");
            be = beUtils.addValue(be, "PRI_NAME", firstname + " " + lastname);
            be = beUtils.addValue(be, "PRI_FIRSTNAME", firstname);
            be = beUtils.addValue(be, "PRI_LASTNAME", lastname);
            be = beUtils.addValue(be, "PRI_MOBILE", "");

            beUtils.updateBaseEntity(be);
        }

        // setup attribute dataType
        DataType dataType = new DataType(String.class);
        dataType.setTypeName("Event");
        dataType.setComponent("button");
        dataType.setDttCode("DTT_EVENT");

        // create attribute
        // Attribute attribute = new Attribute("EVT_TEST_GARDIARY", "Gardiary Test
        // Event", dataType);
        // attribute = qwandaUtils.saveAttribute(productCode, attribute);
        String attributeCode = "EVT_TEST_GARDIARY";
        Attribute attribute = findAttributeByCode(productCode, attributeCode);
        if (attribute == null) {
            attribute = new Attribute("EVT_TEST_GARDIARY", "Gardiary Test Event", dataType);
            attribute.setRealm(productCode);
            dbUtils.saveAttribute(attribute);
            attribute = findAttributeByCode(productCode, attributeCode);
        }
        log.info("Attribute: " + attribute);

        // create question (for sidebar)
        String questionCode = "QUE_TEST_GARDIARY";
        Question question = findQuestionByCode(productCode, questionCode);
        if (question == null) {
            question = new Question(questionCode, "Gardiary Question", attribute);
            question.setRealm(productCode);
            question = dbUtils.saveQuestion(question);
        }
        log.info("Question: " + question);

        // create question (for form)
        String questionGroupCode = "QUE_TEST_GARDIARY_GRP";
        Question questionGroup = findQuestionByCode(productCode, questionGroupCode);
        if (questionGroup == null) {
            Attribute attQueGroup = findAttributeByCode(productCode, "QQQ_QUESTION_GROUP"); // should exist
            questionGroup = new Question(questionGroupCode, "Gardiary Question Group", attQueGroup);
            questionGroup.setRealm(productCode);
            questionGroup = dbUtils.saveQuestion(questionGroup);
        }
        log.info("Question Group: " + questionGroup);

        // create question_question (sourceCode: QUE_SIDEBAR)
        Question queSidebar = findQuestionByCode(productCode, "QUE_SIDEBAR"); // this shouldn't be null, no need to
                                                                              // check

        QuestionQuestion questionQuestion = new QuestionQuestion(queSidebar, question, 5.0);
        questionQuestion.setFormTrigger(Boolean.FALSE);
        questionQuestion.setCreateOnTrigger(Boolean.FALSE);
        questionQuestion.setRealm(productCode);
        dbUtils.saveQuestionQuestion(questionQuestion);
        log.info("QuestionQuestion 1: " + questionQuestion);

        // create question_question (sourceCode: QUE_TEST_GARDIARY_GRP)
        questionQuestion = createQuestionQuestion(productCode, questionGroup, "QUE_DOB", 1.0, false);
        log.info("QuestionQuestion 2: " + questionQuestion);

        questionQuestion = createQuestionQuestion(productCode, questionGroup, "QUE_EMAIL", 2.0, true);
        log.info("QuestionQuestion 3: " + questionQuestion);

        questionQuestion = createQuestionQuestion(productCode, questionGroup, "QUE_NAME", 3.0, true);
        log.info("QuestionQuestion 4: " + questionQuestion);

        questionQuestion = createQuestionQuestion(productCode, questionGroup, "QUE_FIRSTNAME", 4.0, true);
        log.info("QuestionQuestion 5: " + questionQuestion);

        questionQuestion = createQuestionQuestion(productCode, questionGroup, "QUE_LASTNAME", 5.0, false);
        log.info("QuestionQuestion 6: " + questionQuestion);

        questionQuestion = createQuestionQuestion(productCode, questionGroup, "QUE_MOBILE", 6.0, true);
        log.info("QuestionQuestion 7: " + questionQuestion);
    }

    private QuestionQuestion createQuestionQuestion(String productCode, Question sourceQuestion,
            String targetQuestionCode,
            Double weight, Boolean mandatory) {
        Question targetQuestion = dbUtils.findQuestionByCode(productCode, targetQuestionCode);
        log.info("targetQuestion: " + targetQuestion);
        QuestionQuestion questionQuestion = new QuestionQuestion(sourceQuestion, targetQuestion, weight);
        questionQuestion.setMandatory(mandatory);
        questionQuestion.setFormTrigger(Boolean.FALSE);
        questionQuestion.setCreateOnTrigger(Boolean.FALSE);
        questionQuestion.setRealm(productCode);
        return dbUtils.saveQuestionQuestion(questionQuestion);
    }

    public Attribute findAttributeByCode(String productCode, String code) {
        try {
            return dbUtils.findAttributeByCode(productCode, code);
        } catch (Exception e) {
            log.error("Error:", e);
            return null;
        }
    }

    public Question findQuestionByCode(String productCode, String code) {
        try {
            return dbUtils.findQuestionByCode(productCode, code);
        } catch (Exception e) {
            log.error("Error:", e);
            return null;
        }
    }
}
