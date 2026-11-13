package com.library.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.time.LocalDate;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import com.library.dao.DBConnection;

@WebServlet("/IssueBookServlet")
public class IssueBookServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession s = request.getSession(false);
        if (s == null || !"USER".equals(s.getAttribute("role"))) {
            response.sendRedirect("login.html");
            return;
        }

        int userId = (Integer)s.getAttribute("userId");
        int bookId = Integer.parseInt(request.getParameter("book_id"));

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            PreparedStatement check = con.prepareStatement(
                "SELECT available_quantity FROM books WHERE book_id=? FOR UPDATE");
            check.setInt(1, bookId);
            ResultSet rs = check.executeQuery();

            if (!rs.next() || rs.getInt(1) <= 0) {
                con.rollback();
                response.sendRedirect("books.jsp?msg=unavailable");
                return;
            }

            PreparedStatement duplicate = con.prepareStatement(
                "SELECT COUNT(*) FROM issued_books WHERE user_id=? AND book_id=? AND status='ISSUED'");
            duplicate.setInt(1, userId);
            duplicate.setInt(2, bookId);
            ResultSet dr = duplicate.executeQuery();
            dr.next();

            if (dr.getInt(1) > 0) {
                con.rollback();
                response.sendRedirect("books.jsp?msg=already");
                return;
            }

            LocalDate issue = LocalDate.now();
            LocalDate due = issue.plusDays(14);

            PreparedStatement insert = con.prepareStatement(
                "INSERT INTO issued_books(user_id,book_id,issue_date,due_date,status,fine) "
              + "VALUES(?,?,?,?,'ISSUED',0)");
            insert.setInt(1, userId);
            insert.setInt(2, bookId);
            insert.setDate(3, Date.valueOf(issue));
            insert.setDate(4, Date.valueOf(due));
            insert.executeUpdate();

            PreparedStatement update = con.prepareStatement(
                "UPDATE books SET available_quantity=available_quantity-1 WHERE book_id=?");
            update.setInt(1, bookId);
            update.executeUpdate();

            PreparedStatement act = con.prepareStatement(
                "INSERT INTO activities(user_id,activity) VALUES(?,?)");
            act.setInt(1, userId);
            act.setString(2, "Issued book ID " + bookId);
            act.executeUpdate();

            con.commit();
            response.sendRedirect("books.jsp?msg=issued");

        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (Exception ignored) {}
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        } finally {
            try { if (con != null) con.close(); } catch (Exception ignored) {}
        }
    }
}
