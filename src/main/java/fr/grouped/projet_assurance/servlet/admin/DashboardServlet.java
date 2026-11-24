package fr.grouped.projet_assurance.servlet.admin;

import fr.grouped.projet_assurance.dao.StatsDAO;
import fr.grouped.projet_assurance.dao.impl.StatsDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/admin/dashboard")
public class DashboardServlet extends HttpServlet {

    private final StatsDAO statsDAO = new StatsDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("totalRevenue", statsDAO.getTotalRevenue());
        req.setAttribute("pendingPayments", statsDAO.getTotalPendingPayments());
        req.setAttribute("totalIndemnities", statsDAO.getTotalIndemnities());
        req.setAttribute("contractsByStatus", statsDAO.getContractsByStatus());

        req.getRequestDispatcher("/WEB-INF/dashboard.jsp").forward(req, resp);
    }
}
