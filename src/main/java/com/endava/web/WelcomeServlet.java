package com.endava.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@MultipartConfig
@WebServlet(urlPatterns = "/")
public class WelcomeServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        try (PrintWriter writer = response.getWriter()) {
            writer.println("POST to: " + getServletContext().getContextPath() + "/stockExchange");
        }
        response.setStatus(200);
    }
}