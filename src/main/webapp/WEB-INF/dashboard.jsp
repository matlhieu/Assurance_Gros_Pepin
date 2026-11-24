<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Tableau de Bord Administrateur – Assurance Gros Pépin</title>
    <link rel="icon" href="${pageContext.request.contextPath}/image/logo.png">
    <link href="https://fonts.googleapis.com/css2?family=Nunito+Sans:wght@400;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        .page-container {
            max-width: 1100px;
            margin: 40px auto;
            padding: 0 24px;
        }
        .page-header {
            margin-bottom: 28px;
        }
        .page-title {
            font-size: 2rem;
            font-weight: 800;
            margin: 0 0 8px;
            color: var(--texte);
        }
        .page-subtitle {
            color: var(--texte-doux);
            font-size: 1.05rem;
            margin: 0;
        }
        .kpi-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 20px;
            margin-bottom: 36px;
        }
        .kpi-card {
            background: #fff;
            border: 1px solid var(--bordure);
            border-radius: 8px;
            padding: 24px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.04);
            border-top: 4px solid var(--orchidee);
        }
        .kpi-card.warning {
            border-top-color: #f57f17;
        }
        .kpi-card.info {
            border-top-color: var(--pervenche);
        }
        .kpi-title {
            margin: 0 0 10px;
            font-size: 0.95rem;
            font-weight: 700;
            color: var(--texte-doux);
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        .kpi-value {
            font-size: 2rem;
            font-weight: 800;
            color: var(--texte);
        }
        .stats-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 24px;
        }
        @media (max-width: 850px) {
            .stats-grid {
                grid-template-columns: 1fr;
            }
        }
        .panel-card {
            background: #fff;
            border: 1px solid var(--bordure);
            border-radius: 8px;
            padding: 24px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.04);
        }
        .panel-title {
            margin: 0 0 20px;
            font-size: 1.25rem;
            font-weight: 800;
            color: var(--texte);
        }
        .chart-wrapper {
            position: relative;
            max-width: 380px;
            margin: 0 auto;
        }
        table.stats-table {
            width: 100%;
            border-collapse: collapse;
            text-align: left;
        }
        table.stats-table th {
            background: var(--gris-fond);
            color: var(--texte);
            font-weight: 700;
            padding: 12px 14px;
            font-size: 0.9rem;
            border-bottom: 1px solid var(--bordure);
        }
        table.stats-table td {
            padding: 12px 14px;
            border-bottom: 1px solid var(--bordure);
            font-size: 0.95rem;
        }
        table.stats-table tr:last-child td {
            border-bottom: none;
        }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>

    <main class="page-container">
        <div class="page-header">
            <h1 class="page-title">Tableau de Bord Administrateur</h1>
            <p class="page-subtitle">Indicateurs clés de performance financière et portefeuille de contrats.</p>
        </div>

        <!-- KPI Cards -->
        <div class="kpi-grid">
            <div class="kpi-card">
                <div class="kpi-title">Chiffre d'affaires encaissé</div>
                <div class="kpi-value">${totalRevenue != null ? totalRevenue : "0.00"} €</div>
            </div>
            <div class="kpi-card warning">
                <div class="kpi-title">Cotisations en attente</div>
                <div class="kpi-value">${pendingPayments != null ? pendingPayments : "0.00"} €</div>
            </div>
            <div class="kpi-card info">
                <div class="kpi-title">Indemnisations versées</div>
                <div class="kpi-value">${totalIndemnities != null ? totalIndemnities : "0.00"} €</div>
            </div>
        </div>

        <!-- Section Graphique & Tableau Contrats -->
        <div class="stats-grid">
            <div class="panel-card">
                <h2 class="panel-title">Répartition des contrats par statut</h2>
                <div class="chart-wrapper">
                    <canvas id="contractsChart"></canvas>
                </div>
            </div>

            <div class="panel-card">
                <h2 class="panel-title">Détail par statut</h2>
                <table class="stats-table">
                    <thead>
                        <tr>
                            <th>Statut du contrat</th>
                            <th>Nombre de contrats</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty contractsByStatus}">
                                <c:forEach var="row" items="${contractsByStatus}">
                                    <tr>
                                        <td><strong>${row[0]}</strong></td>
                                        <td>${row[1]}</td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="2" style="text-align: center; color: var(--texte-doux); padding: 20px;">
                                        Aucun contrat enregistré.
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </main>

    <script>
        const chartLabels = [
            <c:forEach var="row" items="${contractsByStatus}">
                "${row[0]}",
            </c:forEach>
        ];
        const chartData = [
            <c:forEach var="row" items="${contractsByStatus}">
                ${row[1]},
            </c:forEach>
        ];

        const ctx = document.getElementById('contractsChart');
        if (ctx && chartLabels.length > 0) {
            new Chart(ctx, {
                type: 'doughnut',
                data: {
                    labels: chartLabels,
                    datasets: [{
                        data: chartData,
                        backgroundColor: [
                            '#9AD5CA', // Pearl Aqua
                            '#ACDDE7', // Frosted Blue
                            '#ADB9E3', // Periwinkle
                            '#A379C9', // Bright Lavender
                            '#B744B8'  // Vivid Orchid
                        ],
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: true,
                    plugins: {
                        legend: {
                            position: 'bottom'
                        }
                    }
                }
            });
        }
    </script>

    <%@ include file="/WEB-INF/footer.jspf" %>
</body>
</html>
