package views.helpers.grid.queryForms;

import play.data.DynamicForm;
import play.mvc.Http;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static play.data.Form.form;

public class QueryParams {
    private static final String ROWS_PER_PAGE = "10";
    private static final String DEFAULT_PAGE_NUMBER = "0";
    private static final String DEFAULT_SORT_DIRECTION = "ASC";
    private Map<String, String> params = new HashMap<String, String>();

    public QueryParams() {
        params.put("rowsPerPage", ROWS_PER_PAGE);
        params.put("page", DEFAULT_PAGE_NUMBER);
        params.put("sortBy", "");
        params.put("sortDir", DEFAULT_SORT_DIRECTION);
    }

    public QueryParams bindFromRequest(Http.Request request) {
        DynamicForm dynForm = form().bindFromRequest(request);
        final Set<Map.Entry<String,String>> entries = dynForm.data().entrySet();
        for (Map.Entry<String, String> entry : entries) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            params.put(key, value);
        }
        return this;
    }

    public String get(String paramName) {
        if(params.containsKey(paramName))
            return params.get(paramName);
        else
            return "";
    }

    public Iterable<? extends Map.Entry<String, String>> entrySet() {
        return params.entrySet();
    }
}
