package com.library.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import com.library.dao.DBConnection;

@WebServlet("/RegisterServlet")
@MultipartConfig
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fullname = request.getParameter("fullname");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String age = request.getParameter("age");
        String gender = request.getParameter("gender");
        String department = request.getParameter("department");
        String[] interestArray = request.getParameterValues("interests");
        String interests = "";

        if (interestArray != null) {
            interests = String.join(",", interestArray);
        }

        String address = request.getParameter("address");
        Part photo = request.getPart("profile_photo");
        String photoName = "";

        if (photo != null && photo.getSubmittedFileName() != null) {
            photoName = photo.getSubmittedFileName();
        }

        String sql = "INSERT INTO users "
                + "(fullname,username,email,password,age,gender,department,interests,address,profile_photo,role) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,'USER')";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fullname);
            ps.setString(2, username);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.setInt(5, Integer.parseInt(age));
            ps.setString(6, gender);
            ps.setString(7, department);
            ps.setString(8, interests);
            ps.setString(9, address);
            ps.setString(10, photoName);

            ps.executeUpdate();
            response.sendRedirect("login.html?registered=1");

        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("text/html");
            response.getWriter().println("<h3>Registration failed: " + e.getMessage() + "</h3>");
            response.getWriter().println("<a href='registration.html'>Back</a>");
        }
    }
}
