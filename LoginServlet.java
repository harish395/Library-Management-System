package com.library.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.library.dao.DBConnection;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        String sql = "SELECT user_id, username, fullname, role FROM users "
                   + "WHERE username=? AND password=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    HttpSession old = request.getSession(false);
                    if (old != null) old.invalidate();

                    HttpSession session = request.getSession(true);
                    session.setAttribute("userId", rs.getInt("user_id"));
                    session.setAttribute("username", rs.getString("username"));
                    session.setAttribute("fullname", rs.getString("fullname"));
                    session.setAttribute("role", rs.getString("role"));
                    session.setMaxInactiveInterval(30 * 60);

                    if ("ADMIN".equalsIgnoreCase(rs.getString("role"))) {
                        response.sendRedirect("admin-dashboard.jsp");
                    } else {
                        response.sendRedirect("home.jsp");
                    }
                } else {
                    response.sendRedirect("login.html?error=1");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("text/html");
            response.getWriter().println("<h3>Login error: " + e.getMessage() + "</h3>");
        }
    }
}
