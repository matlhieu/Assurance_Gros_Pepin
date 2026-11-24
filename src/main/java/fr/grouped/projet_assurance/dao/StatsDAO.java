package fr.grouped.projet_assurance.dao;

import java.math.BigDecimal;
import java.util.List;

public interface StatsDAO {
    BigDecimal getTotalRevenue();
    BigDecimal getTotalPendingPayments();
    BigDecimal getTotalIndemnities();
    List<Object[]> getContractsByStatus();
}
