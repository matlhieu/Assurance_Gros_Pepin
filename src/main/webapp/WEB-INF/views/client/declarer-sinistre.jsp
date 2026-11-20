<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Déclarer un sinistre – Assurance Gros Pépin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=2">
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>
    
    <main class="accueil" style="grid-template-columns: 1fr; max-width: 600px; margin: 40px auto;">
        <section class="carte-connexion">
            <div class="carte-chair">
                <h2 class="carte-titre">Déclarer un sinistre</h2>
                
                <%-- Affichage des erreurs --%>
                <c:if test="${not empty erreur}">
                    <p class="alerte">${erreur}</p>
                </c:if>

                <form method="POST" action="${pageContext.request.contextPath}/client/sinistres/declarer" enctype="multipart/form-data" class="formulaire">
                    
                    <div class="champ">
                        <label for="contratId">Contrat concerné (ID)</label>
                        <input type="number" id="contratId" name="contratId" required placeholder="Ex: 1" value="${param.contratId}">
                    </div>
                    
                    <div class="champ">
                        <label for="typeSinistre">Type de sinistre</label>
                        <select id="typeSinistre" name="typeSinistre" style="padding: 12px; border-radius: 10px; border: 2px solid var(--bord); width: 100%;">
                            <option value="Accident">Accident</option>
                            <option value="Vol">Vol</option>
                            <option value="Incendie">Incendie</option>
                            <option value="Dégât des eaux">Dégât des eaux</option>
                        </select>
                    </div>

                    <div class="champ">
                        <label for="montantEstime">Montant estimé (€)</label>
                        <input type="number" step="0.01" id="montantEstime" name="montantEstime" required value="${param.montantEstime}">
                    </div>

                    <div class="champ">
                        <label for="description">Description</label>
                        <textarea id="description" name="description" required style="width: 100%; padding: 12px; border-radius: 10px; border: 2px solid var(--bord); font-family: inherit;">${param.description}</textarea>
                    </div>

                    <div class="champ">
                        <label for="pieces">Pièces justificatives (PDF, JPG)</label>
                        <input type="file" id="pieces" name="pieces" multiple>
                    </div>

                    <input type="submit" value="Envoyer la déclaration" class="btn-principal">
                </form>
            </div>
            <div class="carte-ecorce"></div>
        </section>
    </main>
</body>
</html>