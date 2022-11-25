package life.genny.gadatron.service;

import io.quarkus.runtime.StartupEvent;
import life.genny.qwandaq.Answer;
import life.genny.qwandaq.EEntityStatus;
import life.genny.qwandaq.Question;
import life.genny.qwandaq.QuestionQuestion;
import life.genny.qwandaq.attribute.Attribute;
import life.genny.qwandaq.attribute.EntityAttribute;
import life.genny.qwandaq.datatype.DataType;
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
import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.HashSet;
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
        String isReplace = System.getenv("REPLACE_OWN_QUESTION");
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
            qq = new QuestionQuestion(source, target.getCode(),  weight, target.getMandatory(), false, false, false);
            qq.setRealm(productCode);
            qq.setFormTrigger(false);
            qq.setVersion(1L);
            qq.setCapabilityRequirements(new HashSet<>());
            qq.setUpdated(LocalDateTime.now());
            qq.setCreateOnTrigger(true);
            qq.setDependency("Something");
            qq.setIcon(null);
            qq.setMandatory(mandatory);
            databaseUtils.saveQuestionQuestion(qq);
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
            String isReplace = System.getenv("REPLACE_OWN_QUESTION");
            if (isReplace != null && isReplace.equalsIgnoreCase("TRUE")) {
                queChild = null;
                databaseUtils.deleteQuestion(productCode, code);
            }
        }catch (Exception e) {
            log.error("Questino not found: "+code, e);
        }
        if (queChild == null) {
            Attribute attribute = findAttributeByCode(attributeCode);
            if (attribute == null) {
                attribute = new Attribute();
                attribute.setDataType(DataType.getInstance(valueTypeClass));
                attribute.setCode(attributeCode);
                attribute.setName(attributeCode.toLowerCase().replace("_", " "));
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
            databaseUtils.saveQuestion(queChild);
            queChild = databaseUtils.findQuestionByCode(productCode, code);
        }
        return queChild;
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
            person = beUtils.addValue(person, "PRI_UUID" , person.getCode().substring("PER_".length()));
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
