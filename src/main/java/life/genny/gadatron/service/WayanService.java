package life.genny.gadatron.service;

import io.quarkus.runtime.StartupEvent;
import life.genny.gadatron.constants.WayanConstants;
import life.genny.qwandaq.Answer;
import life.genny.qwandaq.EEntityStatus;
import life.genny.qwandaq.Question;
import life.genny.qwandaq.QuestionQuestion;
import life.genny.qwandaq.attribute.Attribute;
import life.genny.qwandaq.attribute.EntityAttribute;
import life.genny.qwandaq.datatype.DataType;
import life.genny.qwandaq.entity.BaseEntity;
import life.genny.qwandaq.kafka.KafkaTopic;
import life.genny.qwandaq.models.UserToken;
import life.genny.qwandaq.utils.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class WayanService {
    static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass());

    static Jsonb jsonb = JsonbBuilder.create();

    @ConfigProperty(name = "quarkus.application.name", defaultValue = "gadatron")
    public String productCode;

    @Inject
    UserToken userToken;

    @Inject
    QwandaUtils qwandaUtils;

    @Inject
    BaseEntityUtils beUtils;

    @Inject
    DatabaseUtils databaseUtils;

    /**
     * Execute on start up.
     *
     * @param ev The startup event
     */
    void onStart(@Observes StartupEvent ev) {
        log.info("Wayan service is starting");
//        setupQuestionForm();
//        setupSimpleUserDetailsForm();
    }

    /**
     * create and insert a group of question _SIMPLE_USER_DETAILS_GRP
     */
    @Transactional
    void setupSimpleUserDetailsForm() {
        Question questionGroup = null;
        try {
            questionGroup = databaseUtils.findQuestionByCode(productCode, "QUE_WAYAN_SIMPLEUSER_DETAILS_GRP");
            log.info("Question already exists");
        } catch (Exception e) {
            log.error(e);
        }
        if (questionGroup == null) {
            questionGroup = createAQuestion("QUE_WAYAN_SIMPLEUSER_DETAILS_GRP", "QQQ_QUESTION_GROUP", null, true, "Create Question Group", null);

            Question questionChild = createAQuestion("QUE_FIRSTNAME", null, null, false, null, null);
            createQuestionQuestion(questionGroup, questionChild, 1, true);

            questionChild = createAQuestion("QUE_LASTNAME", null, null, true, null, null);
            createQuestionQuestion(questionGroup, questionChild, 2, true);

            questionChild = createAQuestion("QUE_DOB", null, null, true, null, null);
            createQuestionQuestion(questionGroup, questionChild, 3, true);

            questionChild = createAQuestion("QUE_EMAIL", null, null, true, null, null);
            createQuestionQuestion(questionGroup, questionChild, 4, true);

            questionChild = createAQuestion("QUE_MOBILE", null, null, false, null, null);
            createQuestionQuestion(questionGroup, questionChild, 5, false);
        }
    }

    /**
     * create and insert a group of question _CREATE_QUESTION_GRP
     */
    @Transactional
    void setupQuestionForm() {
        Question questionGroup = null;
        try {
            questionGroup = databaseUtils.findQuestionByCode(productCode, "QUE_WAYAN_CREATEQUESTION_GRP");
            log.info("Question already exists");
        }catch (Exception e){
            log.error(e);
        }
        if (questionGroup == null) {
            questionGroup = createAQuestion("QUE_WAYAN_CREATEQUESTION_GRP", "QQQ_QUESTION_GROUP", null, true, "Create Question Group", null);

            Question questionChild = createAQuestion("QUE_WAYAN_NAME", "PRI_WAYAN_NAME", "java.lang.String", true, "Question name", "Name of the question");
            createQuestionQuestion(questionGroup, questionChild, 2, true);

            questionChild = createAQuestion("QUE_WAYAN_ATTRIBUTECODE", "PRI_WAYAN_ATTRIBUTECODE", "java.lang.String", true, "Question attribute code", "Attribute code of the question");
            createQuestionQuestion(questionGroup, questionChild, 3, true);

            questionChild = createAQuestion("QUE_WAYAN_CODE", "PRI_WAYAN_CODE", "java.lang.String", true, "Question code", "Code of the question");
            createQuestionQuestion(questionGroup, questionChild, 1, true);

            questionChild = createAQuestion("QUE_WAYAN_MANDATORY", "PRI_WAYAN_MANDATORY", "java.lang.Boolean", true, "Question Mandatory", "Code of the question");
            createQuestionQuestion(questionGroup, questionChild, 4, true);
        }
    }

    /**
     * Create and insert a question_question data
     *
     * @param source    required
     * @param target    required
     * @param weight    optional if you know the question_question already exists
     * @param mandatory required
     */
    private void createQuestionQuestion(Question source, Question target, double weight, boolean mandatory) {
        QuestionQuestion qq = null;
        try {
            qq = databaseUtils.findQuestionQuestionBySourceAndTarget(productCode, source.getCode(), target.getCode());
        }catch (Exception e){
            log.error("qq not found:"+e.getMessage());
        }
        if (qq == null) {
            qq = new QuestionQuestion(source, target.getCode(),  weight, mandatory, false, false, false);
            qq.setRealm(productCode);
            qq.setFormTrigger(false);
            qq.setVersion(1L);
            qq.setCapabilityRequirements(new HashSet<>());
            qq.setUpdated(LocalDateTime.now());
            qq.setCreateOnTrigger(true);
            qq.setDependency("Something");
            qq.setIcon(null);
            try {
            databaseUtils.saveQuestionQuestion(qq);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                log.error("Error data: "+jsonb.toJson(qq));
            }
        }
    }

    /**
     * Create or get a question data by code
     *
     * @param placeholder,   optional if you know the question already exists
     * @param code           of question, required
     * @param attributeCode  , optional if you know the question already exists
     * @param valueTypeClass must be a class name of the data type, optional if you know the question already exists
     * @param mandatory
     * @param name           of the question, optional if you know the question already exists
     * @return
     */
    private Question createAQuestion(String code, String attributeCode, String valueTypeClass, boolean mandatory, String name, String placeholder) {
        Question queChild = null;
        try {
            queChild = databaseUtils.findQuestionByCode(productCode, code);
        }catch (Exception e) {
            log.error("Questino not found: "+code, e);
        }
        if (queChild == null) {
            Attribute attribute = findAttributeByCode(attributeCode);
            if (attribute == null) {
                attribute = new Attribute(attributeCode, attributeCode.toLowerCase().replace("_", " "), DataType.getInstance(valueTypeClass));
                attribute.setRealm(productCode);
                attribute.setStatus(EEntityStatus.ACTIVE);
                attribute.setDescription(attribute.getName());
                databaseUtils.saveAttribute(attribute);
                attribute = findAttributeByCode(attributeCode);
            }
            assert attribute != null;

            queChild = new Question(code, name, attribute, mandatory, null, placeholder);
            queChild.setIcon(null);
            queChild.setRealm(productCode);
            try {
                queChild = databaseUtils.saveQuestion(queChild);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                log.error("Error data: "+jsonb.toJson(queChild));
            }
        }
        return queChild;
    }

    /**
     * convert DEF_QUESTION to Question data
     * @param entityCode required
     * @return questionID
     */
    public Long createQuestionFromEntity(String entityCode) {
        BaseEntity be = beUtils.getBaseEntity(productCode, entityCode);
        if (be != null) {
            String questionCode = be.getValue(WayanConstants.PRI_WAYAN_QUESTIONCODE, "");
            String questionAttribute = be.getValue(WayanConstants.PRI_WAYAN_QUESTIONATTRIBUTECODE, "PRI_NAME");
            String questionName = be.getValue(WayanConstants.PRI_WAYAN_QUESTIONNAME, "New Test Question");
            String parentCode = be.getValue(WayanConstants.PRI_WAYAN_QUESTIONPARENTCODE, "");
            Integer weight = be.getValue(WayanConstants.PRI_WAYAN_QUESTIONWEIGHT, 1);
            Boolean mandatory = be.getValue(WayanConstants.PRI_WAYAN_QUESTIONMANDATORY, false);
            String typeClassName = be.getValue(WayanConstants.PRI_WAYAN_QUESTION_TYPECLASSNAME, "");
            if (typeClassName == null || typeClassName.isEmpty()) {
                typeClassName = "java.lang.String";
            }
            log.info("code  : "+questionCode);
            log.info("attr  : "+questionAttribute);
            log.info("name  : "+questionName);
            log.info("parent: "+parentCode);
            log.info("weight: "+weight);
            log.info("mndtr : "+mandatory);
            log.info("type  : "+typeClassName);
            Question child = createAQuestion(questionCode, questionAttribute, typeClassName, mandatory, questionName, null);

            if (parentCode != null) {
                Question parent = databaseUtils.findQuestionByCode(productCode, parentCode);
                if (parent == null) {
                    parent = createAQuestion(parentCode, "QQQ_QUESTION_GROUP", "java.lang.String", false, parentCode.replaceAll("_", " ").toLowerCase(), null);
                }
                createQuestionQuestion(parent, child, weight, mandatory);
            }
            return child.getId();
        }
        return null;
    }

    public void sendEvent(String msg) {
        KafkaUtils.writeMsg(
                KafkaTopic.EVENTS,
                msg
        );
    }

    private Attribute findAttributeByCode(String code) {
        try {
            return databaseUtils.findAttributeByCode(productCode, code);
        }catch (Exception e) {
            log.error(code+" not found");
            return null;
        }
    }

    public BaseEntity getTestBaseEntity(String beCode) {
        BaseEntity be = beUtils.getBaseEntityOrNull(beCode);
        return be;
    }

    public String getTestBaseEntityJson(String beCode) {
        BaseEntity be = beUtils.getBaseEntityOrNull(beCode);
        return jsonb.toJson(be);
    }

    private String getCorrectAttribute(BaseEntity be, String search) {
        for (EntityAttribute attr : be.getBaseEntityAttributes()) {
            if (attr.getAttributeCode().toLowerCase().contains(search.toLowerCase())) {
                return attr.getAttributeCode();
            }
        }
        return null;
    }

    public String createAPerson2(String entityDefinition, String content) {
        log.info("Entity Definition is "+entityDefinition);
        if (content != null && !content.isEmpty()) {
            final String name = content;
            BaseEntity personDef = beUtils.getBaseEntity(entityDefinition);
            BaseEntity person = beUtils.create(personDef, name);
            if (entityDefinition.equalsIgnoreCase("DEF_PERSON")) {
                String[] names = name.split(" ");
                if (names.length > 0) {
                    person = beUtils.addValue(person, "PRI_FIRSTNAME", names[0]);
                    person = beUtils.addValue(person, "PRI_NAME", name);

                    StringBuilder lastName = new StringBuilder();
                    for (int i = 1; i < names.length; i++) {
                        if (lastName.length() > 0) {
                            lastName.append(" ");
                        }
                        lastName.append(names[i]);
                    }
                    person = beUtils.addValue(person, "PRI_LASTNAME", lastName.toString());
                } else {
                    person = beUtils.addValue(person, "PRI_FIRSTNAME", name);
                }
                person = beUtils.addValue(person, "PRI_UUID", person.getCode().substring("PER_".length()));
                person = beUtils.addValue(person, "PRI_EMAIL", String.join("_", names) + "@gada.io".toLowerCase().trim());
            } else {
                person = beUtils.addValue(person, "PRI_NAME", name);
            }
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

}
