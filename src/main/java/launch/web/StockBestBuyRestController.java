package launch.web;

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

import launch.service.TradingFacade;

@MultipartConfig
@WebServlet(urlPatterns = "/stockExchange")
public class StockBestBuyRestController extends HttpServlet {

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        Part file = request.getPart("file");
        if (file == null) {
            response.setStatus(400);
            return;
        }
        InputStream inputStream = file.getInputStream();
        TradingFacade tradingFacade = new TradingFacade();
        String jsonResponse = tradingFacade.getBestBuysFor(inputStream);
        response.setContentType("application/json");
        try (PrintWriter writer = response.getWriter()) {
            writer.println(jsonResponse);
        }
        response.setStatus(200);
    }
}
