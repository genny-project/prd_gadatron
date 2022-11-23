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
        log.info("Starting my own service");
        setupQuestionForm();
    }

    @Transactional
    void setupQuestionForm() {
        Question createQuestion = null;
        try {
            createQuestion = databaseUtils.findQuestionByCode(productCode, "QUE_WAYAN_CREATE_QUESTION_GRP");
        }catch (Exception e){
            log.error(e);
        }
        if (createQuestion == null) {
            createQuestion = new Question();
            createQuestion.setCode("QUE_WAYAN_CREATE_QUESTION_GRP");
            Attribute attribute = findAttributeByCode("QQQ_QUESTION_GROUP");
            if (attribute != null) {
                createQuestion.setAttribute(attribute);
                createQuestion.setAttributeCode(attribute.getCode());
            }
            createQuestion.setName("Create a Question Group Form");
            createQuestion.setRealm(productCode);

            Question queChild = createAQuestion("QUE_WAYAN_QUESTION_NAME", "ATT_QUESTION_WAYAN_NAME", "java.lang.String", "Question name", "Name of the question");
            QuestionQuestion qq = createQuestionQuestion(createQuestion, queChild);
            createQuestion.addChildQuestion(qq);

            queChild = createAQuestion("QUE_WAYAN_QUESTION_ATTRIBUTE_CODE", "ATT_QUESTION_WAYAN_ATTRIBUTE_CODE", "java.lang.String","Question attribute code", "Attribute code of the question");
            qq = new QuestionQuestion(createQuestion, queChild, 1.0);
            qq.setRealm(productCode);
            databaseUtils.saveQuestionQuestion(qq);
            qq = databaseUtils.findQuestionQuestionBySourceAndTarget(productCode, createQuestion.getCode(), queChild.getCode());
            createQuestion.addChildQuestion(qq);

            queChild = createAQuestion("QUE_WAYAN_QUESTION_CODE", "ATT_QUESTION_WAYAN_CODE", "java.lang.String","Question code", "Code of the question");
            qq = new QuestionQuestion(createQuestion, queChild, 1.0);
            qq.setRealm(productCode);
            databaseUtils.saveQuestionQuestion(qq);
            qq = databaseUtils.findQuestionQuestionBySourceAndTarget(productCode, createQuestion.getCode(), queChild.getCode());
            createQuestion.addChildQuestion(qq);

            queChild = createAQuestion("QUE_WAYAN_QUESTION_MANDATORY", "ATT_QUESTION_WAYAN_MANDATORY", "java.lang.Boolean", "Question code", "Code of the question");
            qq = new QuestionQuestion(createQuestion, queChild, 1.0);
            qq.setRealm(productCode);
            databaseUtils.saveQuestionQuestion(qq);
            qq = databaseUtils.findQuestionQuestionBySourceAndTarget(productCode, createQuestion.getCode(), queChild.getCode());
            createQuestion.addChildQuestion(qq);

            databaseUtils.saveQuestion(createQuestion);
        }
    }

    private QuestionQuestion createQuestionQuestion(Question source, Question target) {
        QuestionQuestion qq = null;
        try {
            qq = databaseUtils.findQuestionQuestionBySourceAndTarget(productCode, source.getCode(), target.getCode());
        }catch (Exception e){
            log.error("qq not found:"+e.getMessage());
        }
        if (qq == null) {
            qq = new QuestionQuestion(source, target.getCode(), 1.0, target.getMandatory(), false, false, false);
            qq.setRealm(productCode);
            qq.setFormTrigger(false);
            qq.setVersion(1L);
            qq.setCapabilityRequirements(new HashSet<>());
            qq.setUpdated(LocalDateTime.now());
            qq.setCreateOnTrigger(true);
            qq.setDependency("Something");
            qq.setIcon(null);
            databaseUtils.saveQuestionQuestion(qq);
        }
        qq = databaseUtils.findQuestionQuestionBySourceAndTarget(productCode, source.getCode(), target.getCode());
        return qq;
    }


    private Question createAQuestion(String code, String attributeCode, String valueTypeClass, String name, String placeholder) {
        Attribute attribute = findAttributeByCode( attributeCode);
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
        Question queChild = new Question();
        queChild.setCode(code);
        queChild.setName(name);
        queChild.setMandatory(true);
        queChild.setPlaceholder(placeholder);
        queChild.setHtml(null);
        queChild.setIcon(null);
        queChild.setRealm(productCode);
        queChild.setAttribute(attribute);
        databaseUtils.saveQuestion(queChild);
        queChild = databaseUtils.findQuestionByCode(productCode, code);
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
        if (content != null && !content.isEmpty()) {
            final String name = content;
            BaseEntity personDef = beUtils.getBaseEntity(entityDefinition);
            BaseEntity person = beUtils.create(personDef, name);
            String[] names = name.split(" ");
            if (names.length > 0) {
                person = beUtils.addValue(person, getCorrectAttribute(person, "FIRSTNAME"), names[0]);
                StringBuilder lastName = new StringBuilder();
                for (int i = 1; i < names.length; i++) {
                    if (lastName.length() > 0) {
                        lastName.append(" ");
                    }
                    lastName.append(names[i]);
                }
                person = beUtils.addValue(person, getCorrectAttribute(person, "LASTNAME"), lastName.toString());
            } else {
                person = beUtils.addValue(person, getCorrectAttribute(person, "FIRSTNAME"), name);
            }
            person = beUtils.addValue(person, getCorrectAttribute(person, "UUID") , person.getCode().substring("PER_".length()));
            person = beUtils.addValue(person,getCorrectAttribute(person, "PRI_EMAIL"), String.join(".", names)+"@gada.io");
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
