package views.helpers.grid;

import com.avaje.ebean.Page;
import play.api.templates.Html;
import scala.collection.mutable.StringBuilder;
import views.helpers.grid.pagination.PagingItem;
import views.helpers.grid.queryForms.QueryParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GridUtils {

    public static List<PagingItem> getPagingConfig(Page page, String action, QueryParams queryParams){
        List<PagingItem> pagingConfig = new ArrayList<PagingItem>();
        PagingItem curItem;

        if(page != null){
            int maxPageAvailable = page.getTotalPageCount();
            int curPageIndex = page.getPageIndex();
            int minPageIndex = curPageIndex - 3;
            int maxPageIndex = curPageIndex + 3;
            if (minPageIndex < 0) {
                minPageIndex = 0;
                maxPageIndex = 6;
            }
            if (maxPageIndex > maxPageAvailable)
                maxPageIndex = maxPageAvailable - 1;

            curItem = new PagingItem()
                    .setText("&larr; Previous");
            if (page.hasPrev()) {
                curItem.setClass("prev")
                        .setAction(getPagingQueryString(action, queryParams, curPageIndex - 1));
            } else {
                curItem.setClass("prev disabled")
                        .setAction("#");
            }
            pagingConfig.add(curItem);

            for (int i = minPageIndex; i <= maxPageIndex; i++) {
                curItem = new PagingItem().setText(i+1);
                if (i == curPageIndex) {
                    curItem.setClass("active")
                            .setText(String.format("<a class='grid-popover' href='#' data-placement='left' data-html='true' data-content='Go to page <input type=\"text\" size=\"1\" style=\"border:0;\"/><button type=\"button\" class=\"btn btn-primary\">OK</button>' data-toggle='popover' link='%s' maxPage='%d'>%d</a>", getPagingQueryString(action,queryParams,123), maxPageAvailable, i + 1));
                } else {
                    curItem.setAction(getPagingQueryString(action, queryParams, i));
                }
                pagingConfig.add(curItem);
            }

            curItem = new PagingItem().setText("Next &rarr;");
            if (page.hasNext()) {
                curItem.setClass("next")
                        .setAction(getPagingQueryString(action, queryParams, curPageIndex + 1));
            } else {
                curItem.setClass("next disabled")
                        .setAction("#");
            }
            pagingConfig.add(curItem);
        }else{
            return null;
        }
        return pagingConfig;
    }

    public static String makeHeaderLink(String dbParamName, QueryParams queryParams, String action) {
        String headerQueryString = action+"?";
        if (queryParams.get("sortBy").equalsIgnoreCase(dbParamName)) {
            for (Map.Entry<String, String> curEntry : queryParams.entrySet()) {
                String paramName = curEntry.getKey();
                String paramValue = curEntry.getValue();
                if (paramName.equalsIgnoreCase("sortDir"))
                    paramValue = paramValue.equalsIgnoreCase("ASC") ? "DESC" : "ASC";
                headerQueryString += String.format("%s=%s&", paramName, paramValue);
            }
        } else {
            for (Map.Entry<String, String> curEntry : queryParams.entrySet()) {
                String paramName = curEntry.getKey();
                String paramValue = paramName.equalsIgnoreCase("sortBy") ? dbParamName : curEntry.getValue();
                headerQueryString += String.format("%s=%s&", paramName, paramValue);
            }
        }
        return headerQueryString;
    }

    public static Html getHeaderIcon(String dbParamName,QueryParams queryParams, HeaderType headerType) {
        StringBuilder strBuilder = new StringBuilder();
        String iconClass = "glyphicon-sort-by-alphabet";
        if (headerType.equals(HeaderType.NUMERIC) || headerType.equals(HeaderType.DATE))
            iconClass = "glyphicon-sort-by-order";

        if (queryParams.get("sortBy").equalsIgnoreCase(dbParamName)) {
            strBuilder.append(String.format("<span class=\"glyphicon %s%s\"></span>", iconClass, queryParams.get("sortDir").equalsIgnoreCase("ASC") ? "" : "-alt"));
        }

        return new Html(strBuilder);
    }

    public static String makeCurrentLink(QueryParams queryParams, String action) {
        String currentLinkString = action+"?";
        for (Map.Entry<String, String> curEntry : queryParams.entrySet()) {
            currentLinkString += String.format("%s=%s&", curEntry.getKey(), curEntry.getValue());
        }
        return currentLinkString;
    }

    public static String getPagingQueryString(String action, QueryParams queryParams, int pageIndex) {
        String pagingQueryString = action+"?";
        for (Map.Entry<String, String> curEntry : queryParams.entrySet()) {
            String paramName = curEntry.getKey();
            String paramValue = curEntry.getValue();
            if (paramName.equalsIgnoreCase("page"))
                paramValue = String.valueOf(pageIndex);
            pagingQueryString += String.format("%s=%s&", paramName, paramValue);
        }
        return pagingQueryString;
    }
}
