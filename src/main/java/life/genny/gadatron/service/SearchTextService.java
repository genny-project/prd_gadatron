package life.genny.gadatron.service;

public interface SearchTextService {

    void searchByPrimaryText(String data);

    PrimarySearchCode getSearchTextCode(String data);

    class PrimarySearchCode {
        public String attributeCode;
        public String targetCode;
        public String searchText;

        PrimarySearchCode(String attributeCode,  String targetCode, String searchText){
            this.attributeCode = attributeCode;
            this.targetCode = targetCode;
            this.searchText = searchText;
        }
    }
}