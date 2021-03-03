package org.geektimes.projects.user.web.controller;

import org.geektimes.web.mvc.controller.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * @author: cola
 */
@Path("/user")
public class UserController implements RestController {


    @POST
    @GET
    @Path("/save")
    public String save(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        return "register_ok.jsp";
    }

}
