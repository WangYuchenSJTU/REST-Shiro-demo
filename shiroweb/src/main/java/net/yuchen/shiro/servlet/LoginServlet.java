package net.yuchen.shiro.servlet;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "LoginServlet",urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("login post");
        String userName =request.getParameter("userName");
        String password =request.getParameter("password");

        Subject currentUser = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(userName,password,"xx");
        try {
            currentUser.login(token);

            response.sendRedirect("success.jsp");
        }catch (UnknownAccountException e){
            e.printStackTrace();
            request.setAttribute("errorInfo", "wrong user name");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }catch (IncorrectCredentialsException e){
            request.setAttribute("errorInfo", "wrong password");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }catch (AuthenticationException e) {
            e.printStackTrace();
            request.setAttribute("errorInfo", "AuthenticationException ");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("login get");
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
}
