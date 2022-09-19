package life.genny.gadatron.testing;

import life.genny.qwandaq.entity.BaseEntity;

public interface TestService {

    BaseEntity getTestBaseEntity(String beCode);

    String getTestBaseEntityJson(String beCode);

    String getTestBaseEntityApplicationCode(String beCode);

}