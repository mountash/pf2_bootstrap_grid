package controllers;

import java.util.*;

import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import play.*;

import views.helpers.grid.queryForms.QueryParams;
import views.html.*;
import views.html.tags.*;

import models.*;

/**
 * Manage a database of computers
 */
public class Application extends Controller {

    /**
     * This result directly redirect to application home.
     */
    public static Result GO_HOME = redirect(
            routes.Application.index()
    );
    
    public static Result index() {
        QueryParams compQueryParams = new QueryParams().bindFromRequest(request());

        return ok(
                index.render(
                        Computer.page(compQueryParams)
                        ,compQueryParams
                )
        );
    }

    public static Result indexPartial() {
        QueryParams compQueryParams = new QueryParams().bindFromRequest(request());

        return ok(
                _grid.render(
                        Computer.page(compQueryParams)
                        ,compQueryParams
                )
        );
    }

    /**
     * Display the paginated list of computers.
     *
     * @param page Current page number (starts from 0)
     * @param sortBy Column to be sorted
     * @param order Sort order (either asc or desc)
     * @param filter Filter applied on computer names
     */
    public static Result list(int page, String sortBy, String order, String filter) {
        return ok(
            /*list.render(
                Computer.page(page, 10, sortBy, order, filter),
                sortBy, order, filter
            )*/
        );
    }
    
    /**
     * Display the 'edit form' of a existing Computer.
     *
     * @param id Id of the computer to edit
     */
    public static Result edit(Long id) {
        Form<Computer> computerForm = form(Computer.class).fill(
            Computer.find.byId(id)
        );
        if (request().accepts("text/html"))
            return ok(editForm.render(id, computerForm));
        else if (request().accepts("application/javascript")){
            return ok(editFormJS.render(id, computerForm)).as("application/javascript");
        }else
            return badRequest();
    }
    
    /**
     * Handle the 'edit form' submission 
     *
     * @param id Id of the computer to edit
     */
    public static Result update(Long id) {
        Form<Computer> computerForm = form(Computer.class).bindFromRequest();
        if(computerForm.hasErrors()) {
            return badRequest(editForm.render(id, computerForm));
        }
        computerForm.get().update(id);
        flash("success", "Computer " + computerForm.get().name + " has been updated");
        return GO_HOME;
    }
    
    /**
     * Display the 'new computer form'.
     */
    public static Result create() {
        Form<Computer> computerForm = form(Computer.class);
        return ok(
            createForm.render(computerForm)
        );
    }
    
    /**
     * Handle the 'new computer form' submission 
     */
    public static Result save() {
        Form<Computer> computerForm = form(Computer.class).bindFromRequest();
        if(computerForm.hasErrors()) {
            return badRequest(createForm.render(computerForm));
        }
        computerForm.get().save();
        flash("success", "Computer " + computerForm.get().name + " has been created");
        return GO_HOME;
    }
    
    /**
     * Handle computer deletion
     */
    public static Result delete(Long id) {
        Computer.find.ref(id).delete();
        flash("success", "Computer has been deleted");
        return GO_HOME;
    }
    

}
            