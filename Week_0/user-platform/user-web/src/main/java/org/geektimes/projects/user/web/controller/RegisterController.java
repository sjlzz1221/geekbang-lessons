package org.geektimes.projects.user.web.controller;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.service.DefaultUserService;
import org.geektimes.projects.user.service.UserService;
import org.geektimes.web.mvc.controller.PageController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Random;
import java.util.UUID;

/**
 * @author: cola
 */
@Path("/register")
public class RegisterController implements PageController {

    @Resource(name = "bean/UserService")
    private UserService userService;

    @POST
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String name = request.getParameter("inputName");
        String password = request.getParameter("inputPassword");
        String email = request.getParameter("inputEmail");
        String phone = request.getParameter("inputPhone");

        User user = new User();
        user.setName(name);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhoneNumber(phone);

        if(userService.register(user)) {
            return "register_ok.jsp";
        } else {
            return "register_error.jsp";
        }

    }
}
