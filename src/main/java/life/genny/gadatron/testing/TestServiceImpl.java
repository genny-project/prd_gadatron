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
                BaseEntity be = beUtils.getBaseEntityOrNull("MSG_BUCKET1");
                beUtils.addValue(be, "PRI_MILESTONE", "[\"BUCKET1\"]");

                // ATT_PRI_RECIPIENT_LNK

                beUtils.addValue(be, "PRI_RECIPIENT_LNK", "LNK_COMPANY"); // other examples LNK_COMPANY:LNK_ADMIN
                // ATT_PRI_SENDER_LNK
                BaseEntity sender = getBaseEntityByEmail("adamcrow63@gmail.com");

                beUtils.addValue(be, "PRI_SENDER_LNK", userToken.getUserCode());
                // Save this target
                beUtils.updateBaseEntity(be);
        }

        public BaseEntity getBaseEntityByEmail(String email) {

                SearchEntity searchEntity = new SearchEntity("SBE_EMAIL", "Fetch BE associated with Email")
                                .addFilter("PRI_CODE", SearchEntity.StringFilter.LIKE, "PER_%")
                                .addFilter("PRI_EMAIL", SearchEntity.StringFilter.LIKE, "%" + email + "%")
                                .setPageStart(0)
                                .setPageSize(1);

                searchEntity.setRealm(productCode);

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
                applicationBE.setName("Bob Console Internship");
                applicationBE.setStatus(EEntityStatus.ACTIVE);

                applicationBE = beUtils.addValue(applicationBE, "LNK_DAYS_PER_WEEK", "[\"SEL_NO_OF_DAYS_FIVE\"]");
                applicationBE = beUtils.addValue(applicationBE, "LNK_DRESS_CODE", "");
                applicationBE = beUtils.addValue(applicationBE, "LNK_AGENT",
                                "[\"PER_5909D01F-2518-44F9-900A-FB880BF10FAF\"]");
                applicationBE = beUtils.addValue(applicationBE, "LNK_ALL_EMAILS",
                                "[\"SEL_YES\"]");
                applicationBE = beUtils.addValue(applicationBE, "LNK_BUSINESS_HOURS", "");
                applicationBE = beUtils.addValue(applicationBE, "LNK_COMP_INTERNSHIP", "[\"SEL_DIGITAL_JOBS\"]");

                applicationBE = beUtils.addValue(applicationBE, "LNK_DAYS_PER_WEEK", "[\"SEL_NO_OF_DAYS_FIVE\"]");
                applicationBE = beUtils.addValue(applicationBE, "LNK_DRESS_CODE", "");
                applicationBE = beUtils.addValue(applicationBE, "LNK_EDU_PROVIDER",
                                "[\"CPY_64642C16-7775-4419-AC0B-08CB70BF\"]");
                applicationBE = beUtils.addValue(applicationBE, "LNK_HOST_COMPANY",
                                "[\"CPY_4D49B06B-6BB7-45FD-9C7E-44CBBB7E\"]");
                applicationBE = beUtils.addValue(applicationBE, "LNK_INDUSTRY",
                                "[\"SEL_INDUSTRY_INFORMATION_COMMUNICATION_TECHNOLOGY\"]");
                applicationBE = beUtils.addValue(applicationBE, "LNK_INTERN",
                                "[\"PER_1A51A5C9-6A18-4525-B82B-50479EDC52C6\"]");

                applicationBE = beUtils
                                .addValue(applicationBE, "LNK_INTERN_SUPERVISOR",
                                                "[\"PER_62CC08F4-B707-412D-B406-C4B4F64BD4FB\"]");
                applicationBE = beUtils.addValue(applicationBE, "LNK_INTERNSHIP",
                                "[\"BEG_57CC84E4-2BD4-405D-94B8-7E6F93CA\"]");
                applicationBE = beUtils.addValue(applicationBE, "LNK_INTERNSHIP_DURATION",
                                "[\"SEL_DURATION_12_WEEKS\"]");
                applicationBE = beUtils.addValue(applicationBE, "LNK_NO_OF_INTERNS", "[\"SEL_NO_OF_INTERNS_ONE\"]");
                applicationBE = beUtils.addValue(applicationBE, "LNK_OCCUPATION",
                                "[\"SEL_OCCUPATION_DEVELOPERS_PROGRAMMERS\"]");
                applicationBE = beUtils.addValue(applicationBE, "LNK_SOFTWARE", "");

                applicationBE = beUtils.addValue(applicationBE, "LNK_TRANSPORT", "[\"SEL_TRANSPORT_PT\"]");
                applicationBE = beUtils.addValue(applicationBE, "LNK_WHICH_DAYS", "[\"SEL_WHICH_DAYS_ANY\"]");
                applicationBE = beUtils.addValue(applicationBE, "LNK_WORKSITE_SELECT", "[\"SEL_WORKSITE_EITHER\"]");
                applicationBE = beUtils.addValue(applicationBE, "PRI_ABN", "91074444018");
                applicationBE = beUtils.addValue(applicationBE, "PRI_ADDRESS_FULL",
                                "449 Punt Rd, Cremorne VIC 3121, Australia");
                applicationBE = beUtils.addValue(applicationBE, "PRI_AGENT_IMAGE",
                                "ebdd2428-8413-4e5b-8b22-a3030ebdd26c");
                // applicationBE = beUtils.addValue(applicationBE, "PRI_AGENT_NAME ", "Flavio
                // Baronti");

                applicationBE = beUtils.addValue(applicationBE, "PRI_AGR_DOC_HC_SIGNATURE", "https://bit.ly/3A2xdjI");
                applicationBE = beUtils.addValue(applicationBE, "PRI_AGR_DOC_INT_SIGNATURE", "https://bit.ly/3A2xdjI");
                applicationBE = beUtils.addValue(applicationBE, "PRI_AGR_DOC_OUTCOME_SIGNATURE",
                                "https://bit.ly/3A2xdjI");
                applicationBE = beUtils.addValue(applicationBE, "PRI_ANZCO", "NA");
                applicationBE = beUtils.addValue(applicationBE, "PRI_APPLICANT_CODE", "");
                applicationBE = beUtils.addValue(applicationBE, "PRI_APPLIED_BY",
                                "PER_1A51A5C9-6A18-4525-B82B-50479EDC52C6");
                // applicationBE = beUtils.addValue(applicationBE, "PRI_ASSOC_DURATION ", "12");

                applicationBE = beUtils.addValue(applicationBE, "PRI_ASSOC_EP", "Goanna Education");
                applicationBE = beUtils.addValue(applicationBE, "PRI_ASSOC_HC", "Carsales.com");
                applicationBE = beUtils.addValue(applicationBE, "PRI_ASSOC_INDUSTRY",
                                "Information & Communication Technology");
                applicationBE = beUtils.addValue(applicationBE, "PRI_ASSOC_NO_OF_INTERNS", "1");
                applicationBE = beUtils.addValue(applicationBE, "PRI_ASSOC_OCCUPATION", "Developers/Programmers");
                applicationBE = beUtils.addValue(applicationBE, "PRI_ASSOC_SUPER", "Anthea Corridon");
                applicationBE = beUtils.addValue(applicationBE, "PRI_ASSOC_WORKSITE", "Either On-premises or Online");
                applicationBE = beUtils.addValue(applicationBE, "PRI_BASE_LEARNING_OUTCOMES",
                                "<ul><li>Learn employer expectations for workplace ethics, b");
                // applicationBE = beUtils.addValue(applicationBE, "PRI_CODE ", beCode);

                applicationBE = beUtils.addValue(applicationBE, "PRI_DAYS_PER_WEEK", "5");
                // applicationBE = beUtils.addValue(applicationBE, "PRI_END_DATE ",
                // "2022-08-19");
                // applicationBE = beUtils.addValue(applicationBE, "PRI_HOURS_PER_WEEK ", "40");
                applicationBE = beUtils.addValue(applicationBE, "PRI_IMAGE_SECONDARY",
                                "5ffeeab2-0662-4c0f-9e67-8a6ac97c9ec7");
                applicationBE = beUtils.addValue(applicationBE, "PRI_IMAGE_URL",
                                "8fb1beb6-ec6a-4cf4-9fdf-96bcffdccc2b");
                applicationBE = beUtils.addValue(applicationBE, "PRI_INTERN_CODE",
                                "PER_1A51A5C9-6A18-4525-B82B-50479EDC52C6");
                applicationBE = beUtils.addValue(applicationBE, "PRI_INTERN_EMAIL", "adamcrow63+bob@gmail.com");
                applicationBE = beUtils.addValue(applicationBE, "PRI_INTERN_MOBILE", "+61434321230");

                applicationBE = beUtils.addValue(applicationBE, "PRI_INTERN_NAME", "Bob Console");
                applicationBE = beUtils.addValue(applicationBE, "PRI_INTERN_STUDENT_ID", "13921593");
                applicationBE = beUtils.addValue(applicationBE, "PRI_INTERNSHIP_DETAILS", "");
                applicationBE = beUtils.addValue(applicationBE, "PRI_NAME", "Bob Console");
                applicationBE = beUtils.addValue(applicationBE, "PRI_ROLES_AND_RESPONSIBILITIES",
                                "<li>Researching, consulting, analyzing and evaluating s");
                applicationBE = beUtils.addValue(applicationBE, "PRI_SELECT_COUNTRY", "Australia");
                applicationBE = beUtils.addValue(applicationBE, "PRI_SPECIFIC_LEARNING_OUTCOMES",
                                "<li>Researching, consulting, analyzing and evaluating s");
                applicationBE = beUtils.addValue(applicationBE, "PRI_STAGE", "APPLIED");
                applicationBE = beUtils.addValue(applicationBE, "PRI_START_DATE", "2022-08-16");
                applicationBE = beUtils.addValue(applicationBE, "PRI_STATUS", "ACTIVE");
                applicationBE = beUtils.addValue(applicationBE, "PRI_STATUS_COLOR", "#5CB85C");
                applicationBE = beUtils.addValue(applicationBE, "PRI_SUPER_EMAIL", "anthea.corridon@carsales.com.au");
                applicationBE = beUtils.addValue(applicationBE, "PRI_SUPER_MOBILE", "+61");
                applicationBE = beUtils.addValue(applicationBE, "PRI_SUPER_NAME", "Anthea Corridon");
                applicationBE = beUtils.addValue(applicationBE, "PRI_TITLE",
                                "Carsales- programming/Software Development - DJP");
                applicationBE = beUtils.addValue(applicationBE, "PRI_TRANSPORT", "Public Transport");
                applicationBE = beUtils.addValue(applicationBE, "PRI_VIDEO_URL",
                                "9c759a92-d630-4185-a5fd-799c9fda0edb");

                applicationBE = beUtils.addValue(applicationBE, "PRI_WHICH_DAYS_STRIPPED", "Any");
                applicationBE = beUtils.addValue(applicationBE, "PRI_WORD_START_DATE", "16-Aug-22");

                // Save this target
                beUtils.updateBaseEntity(applicationBE);

                return applicationBE.getCode();
        }

}
