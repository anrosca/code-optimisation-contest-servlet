package com.endava.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.endava.service.TradingFacade;

@MultipartConfig
@WebServlet(urlPatterns = "/stockExchange")
public class StockBestBuyServlet extends HttpServlet {

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Serving a request");
        Part file = request.getPart("file");
        if (file == null) {
            response.setStatus(400);
            return;
        }
        TradingFacade tradingFacade = new TradingFacade(file.getInputStream());
        String jsonResponse = tradingFacade.getBestBuys();
        response.setContentType("application/json");
        try (PrintWriter writer = response.getWriter()) {
            writer.println(jsonResponse);
        }
        response.setStatus(200);
    }
}
