<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Traiter le sinistre ${sinistre.numeroSinistre}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=2">
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>
    
    <main class="accueil" style="grid-template-columns: 1fr; max-width: 800px; margin: 40px auto;">
        <section class="carte-connexion">
            <div class="carte-chair">
                <h2 class="carte-titre">Sinistre ${sinistre.numeroSinistre}</h2>
                <p class="carte-sous-titre">Statut actuel : <strong>${sinistre.statut}</strong></p>
                
                <div style="background: var(--fond); padding: 15px; border-radius: 8px; margin-bottom: 20px;">
                    <p><strong>Description :</strong> ${sinistre.description}</p>
                    <p><strong>Montant estimé :</strong> ${sinistre.montantEstime} €</p>
                    <p><strong>Date de déclaration :</strong> ${sinistre.dateDeclaration}</p>
                    <c:if test="${not empty sinistre.commentaireAgent}">
                        <p><strong>Commentaire agent :</strong> ${sinistre.commentaireAgent}</p>
                    </c:if>
                </div>

                <c:choose>
                    <c:when test="${sinistre.statut == 'DECLARE'}">
                        <form method="POST" action="${pageContext.request.contextPath}/agent/sinistres/traiter" class="formulaire">
                            <input type="hidden" name="sinistreId" value="${sinistre.idSinistre}">
                            <input type="hidden" name="action" value="PRENDRE_EN_CHARGE">
                            <div class="champ">
                                <label>Commentaire (optionnel)</label>
                                <textarea name="commentaire" style="width: 100%; padding: 10px; border-radius: 8px; border: 2px solid var(--bord);"></textarea>
                            </div>
                            <button type="submit" class="btn-principal">Prendre en charge</button>
                        </form>
                    </c:when>

                    <c:when test="${sinistre.statut == 'EN_ANALYSE'}">
                        <form method="POST" action="${pageContext.request.contextPath}/agent/sinistres/traiter" class="formulaire">
                            <input type="hidden" name="sinistreId" value="${sinistre.idSinistre}">
                            <div class="champ">
                                <label>Commentaire</label>
                                <textarea name="commentaire" required style="width: 100%; padding: 10px; border-radius: 8px; border: 2px solid var(--bord);"></textarea>
                            </div>
                            <div style="display: flex; gap: 10px;">
                                <button type="submit" name="action" value="ACCEPTER" class="btn-principal" style="flex: 1; background: #4CAF50;">Accepter</button>
                                <button type="submit" name="action" value="REFUSER" class="btn-principal" style="flex: 1; background: #F44336;">Refuser</button>
                            </div>
                        </form>
                    </c:when>

                    <c:when test="${sinistre.statut == 'ACCEPTE'}">
                        <form method="POST" action="${pageContext.request.contextPath}/agent/sinistres/traiter" class="formulaire">
                            <input type="hidden" name="sinistreId" value="${sinistre.idSinistre}">
                            <input type="hidden" name="action" value="INDEMNISER">
                            <div class="champ">
                                <label>Montant de l'indemnisation (€)</label>
                                <input type="number" step="0.01" name="montantIndemnise" required>
                            </div>
                            <button type="submit" class="btn-principal" style="background: #FF9800;">Enregistrer l'indemnisation</button>
                        </form>
                    </c:when>
                </c:choose>

                <p style="margin-top: 20px;">
                    <a href="${pageContext.request.contextPath}/agent/sinistres">← Retour à la liste</a>
                </p>
            </div>
            <div class="carte-ecorce"></div>
        </section>
    </main>
</body>
</html>