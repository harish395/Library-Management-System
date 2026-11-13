package com.library.controller;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;
import com.library.dao.DBConnection;

@WebServlet("/ReturnBookServlet")
public class ReturnBookServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession s = request.getSession(false);
        if (s == null || !"USER".equals(s.getAttribute("role"))) {
            response.sendRedirect("login.html");
            return;
        }

        int userId = (Integer)s.getAttribute("userId");
        int issueId = Integer.parseInt(request.getParameter("issue_id"));

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            PreparedStatement find = con.prepareStatement(
                "SELECT book_id,due_date FROM issued_books "
              + "WHERE issue_id=? AND user_id=? AND status='ISSUED' FOR UPDATE");
            find.setInt(1, issueId);
            find.setInt(2, userId);
            ResultSet rs = find.executeQuery();

            if (!rs.next()) {
                con.rollback();
                response.sendRedirect("return-book.jsp?msg=invalid");
                return;
            }

            int bookId = rs.getInt("book_id");
            Date dueDate = rs.getDate("due_date");

            long lateDays = ChronoUnit.DAYS.between(
                dueDate.toLocalDate(), LocalDate.now());
            double fine = lateDays > 0 ? lateDays * 5.0 : 0.0;

            PreparedStatement updateIssue = con.prepareStatement(
                "UPDATE issued_books SET return_date=SYSDATE,fine=?,status='RETURNED' "
              + "WHERE issue_id=?");
            updateIssue.setDouble(1, fine);
            updateIssue.setInt(2, issueId);
            updateIssue.executeUpdate();

            PreparedStatement updateBook = con.prepareStatement(
                "UPDATE books SET available_quantity=available_quantity+1 WHERE book_id=?");
            updateBook.setInt(1, bookId);
            updateBook.executeUpdate();

            PreparedStatement act = con.prepareStatement(
                "INSERT INTO activities(user_id,activity) VALUES(?,?)");
            act.setInt(1, userId);
            act.setString(2, "Returned book ID " + bookId + " with fine ₹" + fine);
            act.executeUpdate();

            con.commit();
            response.sendRedirect("return-book.jsp?msg=returned");

        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (Exception ignored) {}
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        } finally {
            try { if (con != null) con.close(); } catch (Exception ignored) {}
        }
    }
}
