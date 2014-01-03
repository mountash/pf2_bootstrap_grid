package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

import com.avaje.ebean.*;
import views.helpers.grid.queryForms.QueryParams;

/**
 * Computer entity managed by Ebean
 */
@Entity 
public class Computer extends Model {

    @Id
    public Long id;
    
    @Constraints.Required
    public String name;
    
    @Formats.DateTime(pattern="yyyy/mm/dd")
    public Date introduced;
    
    @Formats.DateTime(pattern="yyyy/mm/dd")
    public Date discontinued;
    
    @ManyToOne
    public Company company;
    
    /**
     * Generic query helper for entity Computer with id Long
     */
    public static Finder<Long,Computer> find = new Finder<Long,Computer>(Long.class, Computer.class); 
    
    /**
     * Return a page of computer
     *
     * @param compQueryParams params
     */
    public static Page<Computer> page(QueryParams compQueryParams) {
        String filter = compQueryParams.get("searchFilter");
        String dateStr  = compQueryParams.get("introducedDate");
        String sortBy = compQueryParams.get("sortBy");
        if(sortBy.isEmpty())
            sortBy = "name";
        String sortDir = compQueryParams.get("sortDir");
        int rowsPerPage = Integer.parseInt(compQueryParams .get("rowsPerPage"));
        int page = Integer.parseInt(compQueryParams.get("page"));
        Expression nameExpr = Expr.ilike("name", "%" + filter + "%");
        Expression searchExpr = nameExpr;
        if(!dateStr.isEmpty()) {
            String[] split = dateStr.split("/");
            split[1] = String.valueOf(Integer.parseInt(split[1])-1);
            dateStr = split[0]+"/"+split[1]+"/"+split[2];
            try{
                Date date = new SimpleDateFormat("yyyy/mm/dd").parse(dateStr);
                Expression dateExpr = Expr.eq("introduced", date);
                searchExpr = Expr.and(nameExpr,dateExpr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return
            find.where(searchExpr)
                .orderBy(sortBy + " " + sortDir)
                .fetch("company")
                .findPagingList(rowsPerPage)
                .getPage(page);
    }
    
}

