package com.library.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.library.dao.DBConnection;

@WebServlet("/DeleteBookServlet")
public class DeleteBookServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession s = request.getSession(false);
        if (s == null || !"ADMIN".equals(s.getAttribute("role"))) {
            response.sendRedirect("login.html");
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM books WHERE book_id=?")) {
            ps.setInt(1, Integer.parseInt(request.getParameter("id")));
            ps.executeUpdate();
            response.sendRedirect("manage-books.jsp?msg=deleted");
        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("text/html");
            response.getWriter().println("<h3>Cannot delete this book if it has related issue records.</h3>");
            response.getWriter().println("<a href='manage-books.jsp'>Back</a>");
        }
    }
}
