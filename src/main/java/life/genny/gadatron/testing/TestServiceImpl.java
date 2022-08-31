package life.genny.gadatron.testing;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.jboss.logging.Logger;

import life.genny.gadatron.Constants;
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

        @Inject
        UserToken userToken;

        @Inject
        QwandaUtils qwandaUtils;

        @Inject
        BaseEntityUtils beUtils;

        @Inject
        SearchUtils searchUtils;

        public BaseEntity getTestBaseEntity(String beCode) {
                return beUtils.getBaseEntityOrNull(beCode);
        }

        public String getTestBaseEntityJson(String beCode) {
                BaseEntity be = beUtils.getBaseEntityOrNull(beCode);
                return jsonb.toJson(be);
        }

        public BaseEntity getBaseEntityByEmail(String email) {

                SearchEntity searchEntity = new SearchEntity(Constants.SBE_EMAIL, "Fetch BE associated with Email")
                                .addFilter(Constants.PRI_CODE, SearchEntity.StringFilter.LIKE, "PER_%")
                                .addFilter(Constants.PRI_EMAIL, SearchEntity.StringFilter.LIKE, "%" + email + "%")
                                .setPageStart(0)
                                .setPageSize(1);

                searchEntity.setRealm(Constants.PRODUCT_CODE);

                List<BaseEntity> personWithEmail = searchUtils.searchBaseEntitys(searchEntity);

                if (!personWithEmail.isEmpty()) {
                        log.info("Found " + personWithEmail.size() + " person with email " + email + " and be "
                                        + personWithEmail.get(0).getCode());
                        return personWithEmail.get(0);
                }
                return null;
        }
}