package org.geektimes.projects.user.web.controller;

import org.geektimes.web.mvc.controller.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * @author: cola
 * 通过自研 Web MVC 框架实现（可以自己实现）一个用户注册，forward 到一个成功的页面（JSP 用法）/register
 */
@Path("/registerForm")
public class RegisterFormController implements PageController {

    @POST
    @GET
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        return "register-form.jsp";
    }
}
