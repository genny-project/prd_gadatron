package life.genny.gadatron.utils;

import life.genny.kogito.common.service.SearchService;
import life.genny.qwandaq.constants.GennyConstants;
import life.genny.qwandaq.entity.search.trait.Operator;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class FilterUtils {
    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass());

    static Jsonb jsonb = JsonbBuilder.create();

    public static final String EVT_QUE_TREE_PREFIX = "QUE_TREE_ITEM_";
    public static final String EVT_QUE_TABLE_PREFIX = "QUE_TABLE_";

    public static final String DATE = "DATE";
    public static final String DATETIME = "DATETIME";
    public static final String TIME = "TIME";

    // select box option
    public static final String PRI_ADDRESS_COUNTRY = "PRI_ADDRESS_COUNTRY";
    public static final String PRI_ASSOC_COMP_INTERNSHIP = "PRI_ASSOC_COMP_INTERNSHIP";
    public static final String PRI_INTERNSHIP_TYPE = "PRI_INTERNSHIP_TYPE";
    public static final String PRI_DJP_AGREE = "PRI_DJP_AGREE";

    // Dropdown links
    public static final String LNK_CORE = "LNK_CORE";
    public static final String COUNTRY = "COUNTRY";
    public static final String COMPLETE_INTERNSHIP = "COMPLETE_INTERNSHIP";
    public static final String YES_NO = "YES_NO";
    public static final String INTERNSHIP_TYPE = "INTERNSHIP_TYPE";

    // Code and Label Academy
    public static final String DIGITAL_JOBS = "DIGITAL_JOBS";
    public static final String DIGITAL_JOBS_LABEL = "VG Digital Jobs Program";
    public static final String OA_WRP = "OA_WRP";
    public static final String OA_WRP_LABEL = "OA - WRP";
    public static final String COURSE_CREDIT = "COURSE_CREDIT";
    public static final String COURSE_CREDIT_LABEL = "Academic Studies";
    public static final String OA_WIL = "OA_WIL";
    public static final String OA_WIL_LABEL = "OA - WIL";
    public static final String OA_CARRERBOX = "OA_CARRERBOX";
    public static final String OA_CARRERBOX_LABEL = "CB";
    public static final String PROFESSIONAL_YEAR = "PROFESSIONAL_YEAR";
    public static final String PROFESSIONAL_YEAR_LABEL = "PYP";

    /**
     * parse string to map object
     * 
     * @param data          json string
     * @param searchOptions PAGINATION or SORT or SEARCH
     * @return return map object
     */
    public Map<String, Object> parseEventMessage(String data, SearchService.SearchOptions searchOptions) {
        Map<String, Object> map = new HashMap<>();

        try {
            JsonObject eventJson = jsonb.fromJson(data, JsonObject.class);
            if (searchOptions.equals(SearchService.SearchOptions.PAGINATION)) {
                JsonObject jsonObject = eventJson.getJsonObject("data");

                map.put(GennyConstants.CODE, jsonObject.getString(GennyConstants.CODE));
                map.put(GennyConstants.TARGETCODE, jsonObject.getString(GennyConstants.TARGETCODE));
                map.put(GennyConstants.TOKEN, eventJson.getString(GennyConstants.TOKEN));

                return map;
            } else if (searchOptions.equals(SearchService.SearchOptions.SEARCH)) { // sorting, searching text
                JsonArray items = eventJson.getJsonArray("items");
                if (items.size() > 0) {
                    JsonObject jsonObject = items.getJsonObject(0);
                    map.put(GennyConstants.CODE, jsonObject.getString(GennyConstants.CODE));
                    map.put(GennyConstants.ATTRIBUTECODE, jsonObject.getString(GennyConstants.ATTRIBUTECODE));
                    String strTargetcode = jsonObject.getString(GennyConstants.TARGETCODE);
                    String[] splitted = strTargetcode.split("\"*\"");
                    List<String> targetCodes = new ArrayList();
                    for (String str : splitted) {
                        if (str.startsWith("SBE_")) {
                            targetCodes.add(str);
                        }
                    }
                    map.put(GennyConstants.TARGETCODE, jsonObject.getString(GennyConstants.TARGETCODE));
                    // It is used for buckets
                    map.put(GennyConstants.TARGETCODES, targetCodes);

                    map.put(GennyConstants.VALUE, jsonObject.getString(GennyConstants.VALUE));
                    map.put(GennyConstants.TOKEN, eventJson.getString(GennyConstants.TOKEN));

                    return map;
                }
            } else if (searchOptions.equals(SearchService.SearchOptions.FILTER)) {
                JsonObject jsonObject = eventJson.getJsonObject("data");

                map.put(GennyConstants.CODE, jsonObject.getString(GennyConstants.CODE));
                map.put(GennyConstants.TARGETCODE, jsonObject.getString(GennyConstants.TARGETCODE));
                map.put(GennyConstants.TOKEN, eventJson.getString(GennyConstants.TOKEN));
                if (jsonObject.containsKey(GennyConstants.VALUE)) {
                    map.put(GennyConstants.VALUE, jsonObject.getString(GennyConstants.VALUE));
                }
                return map;
            }
        } catch (Exception ex) {
        }
        return null;
    }

    /**
     * Return search base entity by message code
     * 
     * @param code Message Code
     * @return Return search base entity
     */
    public String getSearchEntityCodeByMsgCode(String code) {
        String sbeCode = code.replaceFirst(EVT_QUE_TREE_PREFIX, "");
        sbeCode = GennyConstants.CACHING_SBE + sbeCode.replaceFirst(EVT_QUE_TABLE_PREFIX, "");

        return sbeCode;
    }

    /**
     * Check code whether is filter option or not
     * 
     * @param code Message Code
     * @return Being filter option
     */
    public boolean isFilterQuestion(String code) {
        boolean result = false;
        if (code.startsWith(GennyConstants.QUE_FILTER_OPTION))
            return true;

        return result;
    }

    /**
     * Check code whether is filter select question or not
     * 
     * @param filterValue Filter Value
     * @return Being filter option
     */
    public boolean isFilterSelectQuestion(String filterValue) {
        boolean result = false;
        if (filterValue.indexOf(PRI_ADDRESS_COUNTRY) > -1
                || filterValue.indexOf(PRI_ASSOC_COMP_INTERNSHIP) > -1
                || filterValue.indexOf(PRI_DJP_AGREE) > -1
                || filterValue.indexOf(PRI_INTERNSHIP_TYPE) > -1)
            return true;

        return result;
    }

    /**
     * Check code whether is filter column or not
     * 
     * @param code Message Code
     * @return Being filter column
     */
    public boolean isFilterColumn(String code) {
        boolean result = false;
        if (code.startsWith(GennyConstants.QUE_FILTER_COLUMN))
            return true;

        return result;
    }

    /**
     * Check code whether is filter option or not
     * 
     * @param code Message Code
     * @return Being filter option
     */
    public boolean isFilterOption(String code) {
        boolean result = false;
        if (code.startsWith(GennyConstants.QUE_FILTER_OPTION))
            return true;

        return result;
    }

    /**
     * Check code whether is filter value or not
     * 
     * @param code Message Code
     * @return Being filter value
     */
    public boolean isFilterValue(String code) {
        boolean result = false;
        if (code.startsWith(GennyConstants.QUE_FILTER_VALUE))
            return true;

        return result;
    }

    /**
     * Check code whether is filter submit or not
     * 
     * @param code Message Code
     * @return Being filter submit
     */
    public boolean isFilterSubmit(String code) {
        boolean result = false;
        if (code.equalsIgnoreCase(GennyConstants.QUE_SUBMIT))
            return true;

        return result;
    }

    /**
     * Check code whether is quesion showing filter box or not
     * 
     * @param code Message Code
     * @return Being question showing filter box
     */
    public boolean isValidFilterBox(String code) {
        boolean result = false;

        if (code.startsWith(EVT_QUE_TREE_PREFIX) || code.startsWith(EVT_QUE_TABLE_PREFIX))
            return true;

        return result;
    }

    /**
     * Set filter code and filter value
     * 
     * @param code          Message Code
     * @param value         Message value
     * @param searchService Search Service
     */
    public void setFilterParams(String code, String value, SearchService searchService) {
        if (code.startsWith(GennyConstants.QUE_FILTER_COLUMN)) {
            searchService.getFilterParams().put(GennyConstants.QUE_FILTER_COLUMN, value);
        }

        if (code.startsWith(GennyConstants.QUE_FILTER_OPTION)) {
            searchService.getFilterParams().put(GennyConstants.QUE_FILTER_OPTION, value);
        }

        if (code.startsWith(GennyConstants.QUE_FILTER_VALUE_TEXT)) {
            searchService.getFilterParams().put(GennyConstants.QUE_FILTER_VALUE_TEXT, value);
        }

        log.info(searchService.getFilterParams());
    }

    /**
     * Return value by event code in safe way
     * 
     * @param attrMap   Attribute Map
     * @param eventCode Event code
     * @return Return value by event code
     */
    public String getSafeValueByCode(Map<String, Object> attrMap, String eventCode) {
        String value = "";
        if (attrMap == null)
            return value;
        if (attrMap.containsKey(eventCode)) {
            value = attrMap.get(eventCode).toString();
        }
        return value;
    }

    /**
     * Being filter optio whether selected or not
     * 
     * @param eventCode     Event Code
     * @param attributeCode Attribute Code
     * @return filter option selected
     */
    public boolean isFilerColumnSelected(String eventCode, String attributeCode) {
        boolean result = false;
        if (eventCode.startsWith(GennyConstants.QUE_FILTER_COLUMN)
                && attributeCode.startsWith(GennyConstants.LNK_FILTER_COLUMN))
            return true;

        return result;
    }

    /**
     * Being filter optio whether selected or not
     * 
     * @param eventCode     Event Code
     * @param attributeCode Attribute Code
     * @return filter option selected
     */
    public boolean isFilerOptionSelected(String eventCode, String attributeCode) {
        boolean result = false;
        if (eventCode.startsWith(GennyConstants.QUE_FILTER_OPTION)
                && attributeCode.startsWith(GennyConstants.LNK_FILTER_OPTION))
            return true;

        return result;
    }

    /**
     * Strip search base entity code without jti
     * 
     * @param orgSbe Original search base entity code
     * @return Search base entity code without jti
     */
    public String getCleanSBECode(String orgSbe) {
        String sbe = "";

        if (orgSbe.indexOf("-") > -1) {
            int index = orgSbe.lastIndexOf("_");
            sbe = orgSbe.substring(0, index);

            return sbe;
        }

        return orgSbe;
    }

    /**
     * Return question code by filter code
     * 
     * @param filterVal Filter Code
     * @return Return question code by filter code
     */
    public String getQuestionCodeByFilterValue(String filterVal) {
        String questionCode = "";
        String suffix = getLastSuffixCodeByFilterValue(filterVal);

        if (filterVal.indexOf(PRI_ADDRESS_COUNTRY) > -1) {
            return GennyConstants.QUE_FILTER_VALUE_COUNTRY;
        } else if (filterVal.indexOf(PRI_ASSOC_COMP_INTERNSHIP) > -1) {
            return GennyConstants.QUE_FILTER_VALUE_ACADEMY;
        } else if (filterVal.indexOf(PRI_DJP_AGREE) > -1) {
            return GennyConstants.QUE_FILTER_VALUE_DJP_HC;
        } else if (filterVal.indexOf(PRI_INTERNSHIP_TYPE) > -1) {
            return GennyConstants.QUE_FILTER_VALUE_INTERNSHIP_TYPE;
        }
        // date,time
        if (suffix.equalsIgnoreCase(DATE)) {
            return GennyConstants.QUE_FILTER_VALUE_DATE;
        } else if (suffix.equalsIgnoreCase(DATETIME)) {
            return GennyConstants.QUE_FILTER_VALUE_DATETIME;
        } else if (suffix.equalsIgnoreCase(TIME)) {
            return GennyConstants.QUE_FILTER_VALUE_TIME;
        }

        // text
        if (!filterVal.isEmpty()) {
            return GennyConstants.QUE_FILTER_VALUE_TEXT;
        }
        return questionCode;
    }

    /**
     * Return link value based on event value
     * 
     * @param value Event Value
     * @return Return link value based on event value
     */
    public String getLinkVal(String value) {
        String lnkVal = "";
        if (value.indexOf(PRI_ADDRESS_COUNTRY) > -1) {
            lnkVal = COUNTRY;
        } else if (value.indexOf(PRI_ASSOC_COMP_INTERNSHIP) > -1) {
            lnkVal = COMPLETE_INTERNSHIP;
        } else if (value.indexOf(PRI_DJP_AGREE) > -1) {
            lnkVal = YES_NO;
        } else if (value.indexOf(PRI_INTERNSHIP_TYPE) > -1) {
            lnkVal = INTERNSHIP_TYPE;
        }

        return lnkVal;
    }

    /**
     * Return whether being select box or not
     * 
     * @param filterVal Filter Value
     * @return Return whether being select box or not
     */
    public boolean isSelectBoxByFilterValue(String filterVal) {
        boolean isSelectBox = false;

        String questionCode = getQuestionCodeByFilterValue(filterVal);
        if (!questionCode.isEmpty())
            return true;
        return isSelectBox;
    }

    /**
     * Return the last suffix code
     * 
     * @param filterVal Filter Value
     * @return Return the last suffix code
     */
    public String getLastSuffixCodeByFilterValue(String filterVal) {
        String lastSuffix = "";
        int lastIndex = filterVal.lastIndexOf("_");
        if (lastIndex > -1) {
            lastSuffix = filterVal.substring(lastIndex, filterVal.length());
            lastSuffix = lastSuffix.replaceFirst("_", "");
            lastSuffix = lastSuffix.replaceFirst("\"]", "");
        }
        return lastSuffix;
    }

    /**
     * Get Seach String operator by filter value
     * 
     * @param filterVal Filter value
     * @return Get String filter by filter value
     */
    public Operator getStringOpertorByFilterVal(String filterVal) {
        if (filterVal.equalsIgnoreCase(GennyConstants.SEL_EQUAL_TO)) {
            return Operator.EQUALS;
        }

        if (filterVal.equalsIgnoreCase(GennyConstants.SEL_NOT_EQUAL_TO)) {
            return Operator.NOT_EQUALS;
        }

        if (filterVal.equalsIgnoreCase(GennyConstants.SEL_LIKE)) {
            return Operator.LIKE;
        }

        if (filterVal.equalsIgnoreCase(GennyConstants.SEL_NOT_LIKE)) {
            return Operator.NOT_LIKE;
        }

        return Operator.EQUALS;
    }

    /**
     * Get Search filter by filter value
     * 
     * @param filterVal Filter value
     * @return Get Search filter by filter value
     */
    public Operator getFilterOperatorByFilterVal(String filterVal) {
        if (filterVal.equalsIgnoreCase(GennyConstants.SEL_GREATER_THAN)) {
            return Operator.GREATER_THAN;
        }

        if (filterVal.equalsIgnoreCase(GennyConstants.SEL_GREATER_THAN_OR_EQUAL_TO)) {
            return Operator.GREATER_THAN_OR_EQUAL;
        }

        if (filterVal.equalsIgnoreCase(GennyConstants.SEL_LESS_THAN)) {
            return Operator.LESS_THAN;
        }

        if (filterVal.equalsIgnoreCase(GennyConstants.SEL_LESS_THAN_OR_EQUAL_TO)) {
            return Operator.LESS_THAN_OR_EQUAL;
        }

        if (filterVal.equalsIgnoreCase(GennyConstants.SEL_EQUAL_TO)) {
            return Operator.EQUALS;
        }

        if (filterVal.equalsIgnoreCase(GennyConstants.SEL_NOT_EQUAL_TO)) {
            return Operator.NOT_EQUALS;
        }

        return Operator.EQUALS;
    }

    /**
     * Being whether date time is selected or not
     * 
     * @param questionCode Question code
     * @return Being whether date time is selected or not
     */
    public boolean isDateTimeSelected(String questionCode) {
        boolean isDateTime = false;

        // date,time
        if (questionCode.equalsIgnoreCase(GennyConstants.QUE_FILTER_VALUE_DATE)) {
            return true;
        } else if (questionCode.equalsIgnoreCase(GennyConstants.QUE_FILTER_VALUE_DATETIME)) {
            return true;
        } else if (questionCode.equalsIgnoreCase(GennyConstants.QUE_FILTER_VALUE_TIME)) {
            return true;
        }

        return isDateTime;
    }

    /**
     * Return attribute cod
     * 
     * @param filterVal Filter value
     * @return Return attribute code
     */
    public String getAttributeCodeByFilterValue(String filterVal) {
        String attCode = filterVal.replaceFirst(GennyConstants.SEL_FILTER_COLUMN_FLC, "");
        return attCode;
    }

    /**
     * return value by question and option code of academy
     * 
     * @param questionCode Question code
     * @param code         Option code
     * @return Value by question and option code of academy
     */
    public String getLabelByValueCode(String questionCode, String code) {
        if (questionCode.equalsIgnoreCase(GennyConstants.QUE_FILTER_VALUE_ACADEMY) && code.startsWith(DIGITAL_JOBS)) {
            return DIGITAL_JOBS_LABEL;
        } else if (questionCode.equalsIgnoreCase(GennyConstants.QUE_FILTER_VALUE_ACADEMY) && code.startsWith(OA_WRP)) {
            return OA_WRP_LABEL;
        } else if (questionCode.equalsIgnoreCase(GennyConstants.QUE_FILTER_VALUE_ACADEMY)
                && code.startsWith(COURSE_CREDIT)) {
            return COURSE_CREDIT_LABEL;
        } else if (questionCode.equalsIgnoreCase(GennyConstants.QUE_FILTER_VALUE_ACADEMY) && code.startsWith(OA_WIL)) {
            return OA_WIL_LABEL;
        } else if (questionCode.equalsIgnoreCase(GennyConstants.QUE_FILTER_VALUE_ACADEMY)
                && code.startsWith(OA_CARRERBOX)) {
            return OA_CARRERBOX_LABEL;
        } else if (questionCode.equalsIgnoreCase(GennyConstants.QUE_FILTER_VALUE_ACADEMY)
                && code.startsWith(PROFESSIONAL_YEAR)) {
            return PROFESSIONAL_YEAR_LABEL;
        }

        return code;
    }

    /**
     * Parse string to local date time
     * 
     * @param strDate Date String
     * @return Return local date time
     */
    public LocalDateTime parseStringToDate(String strDate) {
        LocalDateTime localDateTime = null;
        try {
            ZonedDateTime zdt = ZonedDateTime.parse(strDate);
            localDateTime = zdt.toLocalDateTime();
        } catch (Exception ex) {
            log.info(ex);
        }

        return localDateTime;
    }

    /**
     * Being whether event is pagination event or not
     * 
     * @param attrs Attribute Map
     * @return Being whether event is pagination event or not
     */
    public boolean isPaginationEvent(Map<String, Object> attrs) {
        if (attrs != null && (attrs.get(GennyConstants.CODE).toString().equalsIgnoreCase(GennyConstants.PAGINATION_NEXT)
                || attrs.get(GennyConstants.CODE).toString().equalsIgnoreCase(GennyConstants.PAGINATION_PREV))) {
            return true;
        }

        return false;
    }

}