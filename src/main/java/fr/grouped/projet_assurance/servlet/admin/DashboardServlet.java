package fr.grouped.projet_assurance.servlet.admin;

import fr.grouped.projet_assurance.dao.impl.StatsDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/admin/dashboard", "/admin/dahsboard"})
public class DashboardServlet extends HttpServlet {
    private StatsDao statsDao = new StatsDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        double totalRevenue = statsDao.getTotalRevenue();
        double pendingPayments = statsDao.getTotalPendingPayments();

        req.setAttribute("totalRevenue", totalRevenue);
        req.setAttribute("pendingPayments", pendingPayments);


        //need Contrats & Sinistre entities
        // req.setAttribute("totalIndemnities", statsDao.getTotalIndemnities());
        // req.setAttribute("contractsByStatus", statsDao.getContractsByStatus());
        // req.setAttribute("clientsByType", statsDao.getClientsByInsuranceType());

        req.getRequestDispatcher("/WEB-INF/dashboard.jsp").forward(req, resp);
    }



}
