package life.genny.gadatron.service;

import life.genny.gadatron.Constants;
import life.genny.gadatron.live.data.InternalConsumer;
import life.genny.gadatron.search.SearchCaching;
import life.genny.kogito.common.service.SearchService;
import life.genny.qwandaq.entity.SearchEntity;
import life.genny.qwandaq.message.QDataAnswerMessage;
import life.genny.qwandaq.utils.CacheUtils;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class SearchTextServiceImpl implements SearchTextService {

    private static final Logger log = Logger.getLogger(SearchTextServiceImpl.class);

    private static Jsonb jsonb = JsonbBuilder.create();
    
    @Inject
    SearchService searchService;

    @Inject
    SearchCaching caching;

    public void  searchByPrimaryText(String data){
        List<SearchEntity> entities = new ArrayList<>();
        PrimarySearchCode code = getSearchTextCode(data);

        if (code.targetCode.contains(Constants.COMPANIES)) {
            entities.add(caching.getSBECompanies(Constants.COMPANIES + "_" + code.searchText));
        } else if (code.targetCode.contains(Constants.PERSONS)) {
            entities.add(caching.getSBEPersons(Constants.PERSONS + "_" + code.searchText));
        } else if (code.targetCode.contains(Constants.DOCUMENTS)) {
            entities.add(caching.getSBEDocuments(Constants.DOCUMENTS + "_" + code.searchText));
        } else if (code.targetCode.contains(Constants.MESSAGES)) {
            entities.add(caching.getSBEMessages(Constants.MESSAGES + "_" + code.searchText));
        }

        // save to cache
        entities.stream().forEach(e -> {
            e.setRealm(Constants.PRODUCT_CODE);
            CacheUtils.putObject(Constants.PRODUCT_CODE, e.getCode(), e);
        });

        searchService.sendTable(code.targetCode);
    }

    public PrimarySearchCode getSearchTextCode(String data){
        String attributeCode = "";
        String targetCode = "";
        String searchText = "";

        QDataAnswerMessage msgForCode = jsonb.fromJson(data, QDataAnswerMessage.class);
        if(msgForCode.getItems().length > 0) {
            attributeCode = msgForCode.getItems()[0].getAttributeCode();
            targetCode = msgForCode.getItems()[0].getTargetCode();
            if(msgForCode.getItems()[0].getValue().length() > 0) {
                searchText = msgForCode.getItems()[0].getValue().substring(1);
            }
        }
        return new PrimarySearchCode(attributeCode, targetCode, searchText);
    }

}