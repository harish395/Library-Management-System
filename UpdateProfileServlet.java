package com.library.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import com.library.dao.DBConnection;

@WebServlet("/UpdateProfileServlet")
public class UpdateProfileServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession s = request.getSession(false);
        if (s == null || s.getAttribute("userId") == null) {
            response.sendRedirect("login.html");
            return;
        }

        int userId = (Integer)s.getAttribute("userId");

        String sql = "UPDATE users SET fullname=?,email=?,age=?,gender=?,department=?,"
                   + "interests=?,address=? WHERE user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, request.getParameter("fullname"));
            ps.setString(2, request.getParameter("email"));
            ps.setInt(3, Integer.parseInt(request.getParameter("age")));
            ps.setString(4, request.getParameter("gender"));
            ps.setString(5, request.getParameter("department"));
            ps.setString(6, request.getParameter("interests"));
            ps.setString(7, request.getParameter("address"));
            ps.setInt(8, userId);
            ps.executeUpdate();

            s.setAttribute("fullname", request.getParameter("fullname"));
            response.sendRedirect("profile.jsp?msg=updated");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}
