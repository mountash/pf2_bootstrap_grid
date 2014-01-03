package views.helpers.grid.pagination;

import play.api.templates.Html;
import scala.collection.mutable.StringBuilder;

public class PagingItem {
    private String cssClass;
    private String action;
    private String text;

    public PagingItem setClass(String cssClass) {
        this.cssClass = cssClass;
        return this;
    }

    public String getCssClass() {
        return cssClass;
    }

    public PagingItem setAction(String action) {
        this.action = action;
        return this;
    }

    public String getAction() {
        return action;
    }

    public PagingItem setText(String text) {
        this.text = text;
        return this;
    }

    public PagingItem setText(int text) {
        this.text = String.valueOf(text);
        return this;
    }

    public Html getText() {
        return new Html(new StringBuilder(text));
    }
}
