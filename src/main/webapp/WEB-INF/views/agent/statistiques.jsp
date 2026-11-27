<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="fr.grouped.projet_assurance.util.WebUtil" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Statistiques – Assurance Gros Pépin</title>
    <%@ include file="/WEB-INF/views/commun/head.jspf" %>
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>

    <main class="gp-page">
        <section class="gp-bandeau">
            <div class="gp-conteneur">
                <div class="gp-bandeau-ligne">
                    <div>
                        <p class="gp-surtitre">Pilotage</p>
                        <h1 class="gp-titre">Statistiques</h1>
                        <p class="gp-sous-titre">${nbContratsActifs} contrat(s) actif(s) sur ${nbContrats}, ${nbSinistres} sinistre(s) déclaré(s).</p>
                    </div>
                </div>
            </div>
        </section>

        <div class="gp-conteneur gp-contenu">
            <div class="gp-grille gp-grille-4">
                <div class="gp-carte gp-kpi">
                    <div class="gp-kpi-haut"><span class="gp-kpi-libelle">Cotisations encaissées</span>
                        <span class="gp-icone gp-icone-ok"><svg viewBox="0 0 24 24"><path d="m5 12.5 4.5 4.5L19 7.5"/></svg></span></div>
                    <div><div class="gp-kpi-valeur">${WebUtil.euros(encaisse)}</div><div class="gp-kpi-note">${WebUtil.euros(enAttente)} en attente</div></div>
                </div>
                <div class="gp-carte gp-kpi">
                    <div class="gp-kpi-haut"><span class="gp-kpi-libelle">Primes annuelles</span>
                        <span class="gp-icone"><svg viewBox="0 0 24 24"><path d="M12 3 4.5 6v6c0 4.4 3.2 8.2 7.5 9 4.3-.8 7.5-4.6 7.5-9V6z"/></svg></span></div>
                    <div><div class="gp-kpi-valeur">${WebUtil.euros(primesActives)}</div><div class="gp-kpi-note">sur les contrats actifs</div></div>
                </div>
                <div class="gp-carte gp-kpi">
                    <div class="gp-kpi-haut"><span class="gp-kpi-libelle">Indemnisations</span>
                        <span class="gp-icone gp-icone-info"><svg viewBox="0 0 24 24"><path d="M3 6.5h18v11H3zM3 10.5h18M7 14.5h3"/></svg></span></div>
                    <div><div class="gp-kpi-valeur">${WebUtil.euros(indemnise)}</div><div class="gp-kpi-note">versées aux clients</div></div>
                </div>
                <div class="gp-carte gp-kpi">
                    <div class="gp-kpi-haut"><span class="gp-kpi-libelle">Ratio sinistres / primes</span>
                        <span class="gp-icone ${ratio > 70 ? 'gp-icone-erreur' : 'gp-icone-ok'}"><svg viewBox="0 0 24 24"><path d="M4 20 20 4M7 7h.01M17 17h.01"/></svg></span></div>
                    <div><div class="gp-kpi-valeur">${ratio} %</div><div class="gp-kpi-note">indemnités ÷ cotisations encaissées</div></div>
                </div>
            </div>

            <h2 class="gp-section-titre gp-section">Clients</h2>
            <div class="gp-grille gp-grille-2">
                <section class="gp-carte gp-carte-corps">
                    <div class="gp-grille gp-grille-2" style="gap:12px">
                        <div><p class="gp-kpi-libelle" style="margin:0">Clients</p><div class="gp-kpi-valeur" style="margin-top:6px">${nbClients}</div><div class="gp-kpi-note">inscrits</div></div>
                        <div><p class="gp-kpi-libelle" style="margin:0">Clients couverts</p><div class="gp-kpi-valeur" style="margin-top:6px">${nbClientsCouverts}</div><div class="gp-kpi-note">avec au moins un contrat actif</div></div>
                    </div>
                </section>
                <section class="gp-carte gp-carte-corps">
                    <h2 class="gp-aside-titre">Clients par type d'assurance</h2>
                    <c:set var="barres" value="${clientsParType}"/>
                    <%@ include file="/WEB-INF/views/commun/barres.jspf" %>
                </section>
            </div>

            <h2 class="gp-section-titre gp-section">Contrats et finances</h2>
            <section class="gp-carte gp-carte-corps" style="margin-bottom:20px">
                <h2 class="gp-aside-titre">Nouveaux contrats par mois (12 derniers mois)</h2>
                <div class="gp-histo">
                    <c:forEach var="b" items="${evolutionContrats}">
                        <div class="gp-histo-col">
                            <span class="gp-histo-valeur">${b.valeur}</span>
                            <span class="gp-histo-zone"><span class="gp-histo-barre" style="height:${b.pourcentage}%"></span></span>
                            <span class="gp-histo-libelle">${b.libelle}</span>
                        </div>
                    </c:forEach>
                </div>
            </section>

            <div class="gp-grille gp-grille-2">
                <section class="gp-carte gp-carte-corps">
                    <h2 class="gp-aside-titre">Encaissements des 6 derniers mois</h2>
                    <div class="gp-histo">
                        <c:forEach var="b" items="${encaissementsParMois}">
                            <div class="gp-histo-col">
                                <span class="gp-histo-valeur">${b.valeur}</span>
                                <span class="gp-histo-zone"><span class="gp-histo-barre" style="height:${b.pourcentage}%"></span></span>
                                <span class="gp-histo-libelle">${b.libelle}</span>
                            </div>
                        </c:forEach>
                    </div>
                </section>

                <section class="gp-carte gp-carte-corps">
                    <h2 class="gp-aside-titre">Contrats par produit</h2>
                    <c:set var="barres" value="${contratsParType}"/>
                    <%@ include file="/WEB-INF/views/commun/barres.jspf" %>
                </section>

                <section class="gp-carte gp-carte-corps">
                    <h2 class="gp-aside-titre">Contrats par statut</h2>
                    <c:set var="barres" value="${contratsParStatut}"/>
                    <%@ include file="/WEB-INF/views/commun/barres.jspf" %>
                </section>

                <section class="gp-carte gp-carte-corps">
                    <h2 class="gp-aside-titre">Sinistres par nature</h2>
                    <c:set var="barres" value="${sinistresParNature}"/>
                    <%@ include file="/WEB-INF/views/commun/barres.jspf" %>
                </section>
            </div>
        </div>
    </main>

    <%@ include file="/WEB-INF/footer.jspf" %>
</body>
</html>
