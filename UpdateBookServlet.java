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

@WebServlet("/UpdateBookServlet")
public class UpdateBookServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession s = request.getSession(false);
        if (s == null || !"ADMIN".equals(s.getAttribute("role"))) {
            response.sendRedirect("login.html");
            return;
        }

        String sql = "UPDATE books SET book_name=?,author=?,category=?,publisher=?,isbn=?,"
                   + "quantity=?,available_quantity=?,price=? WHERE book_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, request.getParameter("book_name"));
            ps.setString(2, request.getParameter("author"));
            ps.setString(3, request.getParameter("category"));
            ps.setString(4, request.getParameter("publisher"));
            ps.setString(5, request.getParameter("isbn"));
            ps.setInt(6, Integer.parseInt(request.getParameter("quantity")));
            ps.setInt(7, Integer.parseInt(request.getParameter("available_quantity")));
            ps.setDouble(8, Double.parseDouble(request.getParameter("price")));
            ps.setInt(9, Integer.parseInt(request.getParameter("book_id")));
            ps.executeUpdate();
            response.sendRedirect("manage-books.jsp?msg=updated");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}
