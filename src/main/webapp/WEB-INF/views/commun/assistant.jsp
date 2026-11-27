<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="fr.grouped.projet_assurance.util.WebUtil" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Assistant – Assurance Gros Pépin</title>
    <%@ include file="/WEB-INF/views/commun/head.jspf" %>
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>

    <main class="gp-page">
        <section class="gp-bandeau">
            <div class="gp-conteneur">
                <ol class="gp-fil">
                    <c:choose>
                        <c:when test="${empty utilisateur}">
                            <li><a href="${pageContext.request.contextPath}/index.jsp">Accueil</a></li>
                        </c:when>
                        <c:otherwise>
                            <li><a href="${pageContext.request.contextPath}/${fn:toLowerCase(utilisateur.role)}/dashboard">Mon espace</a></li>
                        </c:otherwise>
                    </c:choose>
                    <li>Assistant</li>
                </ol>
                <div class="gp-bandeau-ligne">
                    <div>
                        <h1 class="gp-titre">Votre assistant</h1>
                        <c:choose>
                            <c:when test="${empty utilisateur}">
                                <p class="gp-sous-titre">Une question sur nos assurances, les garanties ou la déclaration d'un sinistre ? L'assistant vous répond tout de suite. Connectez-vous pour qu'il connaisse aussi votre dossier.</p>
                            </c:when>
                            <c:otherwise>
                                <p class="gp-sous-titre">Une question sur vos contrats, vos garanties ou un sinistre ? L'assistant connaît votre dossier et vous répond tout de suite.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <c:if test="${not empty conversation}">
                        <form method="post" action="${pageContext.request.contextPath}/assistant" class="gp-bandeau-actions">
                            <input type="hidden" name="action" value="effacer">
                            <button type="submit" class="gp-bouton-contour">
                                <svg viewBox="0 0 24 24"><path d="M12 5v14M5 12h14"/></svg>
                                Nouvelle conversation
                            </button>
                        </form>
                    </c:if>
                </div>
            </div>
        </section>

        <div class="gp-conteneur gp-contenu">
            <div class="gp-chat gp-carte">
                <%-- Le fil suit le flux de la page : seule la fenêtre du navigateur défile, pas de barre interne --%>
                <div class="gp-chat-fil" id="fil">
                    <%-- Message d'accueil --%>
                    <div class="gp-bulle gp-bulle-ia">
                        <span class="gp-bulle-avatar"><img src="${pageContext.request.contextPath}/image/logo.png" alt=""></span>
                        <div class="gp-bulle-texte">
                            <c:choose>
                                <c:when test="${empty utilisateur}">
                                    <p>Bonjour ! Je suis l'assistant Gros Pépin. Je peux vous présenter nos assurances et leurs garanties, expliquer comment souscrire ou déclarer un sinistre. Créez votre compte pour que je connaisse aussi votre dossier !</p>
                                </c:when>
                                <c:otherwise>
                                    <p>Bonjour <c:out value="${utilisateur.prenom}"/> ! Je suis l'assistant Gros Pépin.
                                       Je peux vous renseigner sur vos contrats, vos garanties, vos paiements et le suivi de vos sinistres.</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <c:forEach var="e" items="${conversation}">
                        <div class="gp-bulle gp-bulle-client">
                            <div class="gp-bulle-texte"><c:out value="${e.question}"/></div>
                        </div>
                        <div class="gp-bulle gp-bulle-ia">
                            <span class="gp-bulle-avatar"><img src="${pageContext.request.contextPath}/image/logo.png" alt=""></span>
                            <%-- texteIa échappe tout le HTML avant de mettre en forme : pas d'injection possible --%>
                            <div class="gp-bulle-texte">${WebUtil.texteIa(e.reponse)}</div>
                        </div>
                    </c:forEach>

                    <div class="gp-bulle gp-bulle-ia gp-chat-attente" id="attente" hidden>
                        <span class="gp-bulle-avatar"><img src="${pageContext.request.contextPath}/image/logo.png" alt=""></span>
                        <div class="gp-bulle-texte"><span class="gp-points"><i></i><i></i><i></i></span></div>
                    </div>
                    <span id="fin"></span>
                </div>

                <form method="post" action="${pageContext.request.contextPath}/assistant" class="gp-chat-saisie" id="formChat">
                    <div class="gp-suggestions" aria-label="Questions fréquentes">
                        <%-- Liste fournie par la servlet selon le profil : ne pas écrire de listes EL
                             ici, les apostrophes françaises les cassent à la compilation JSP --%>
                        <c:forEach var="q" items="${suggestions}">
                            <button type="button" class="gp-suggestion"><c:out value="${q}"/></button>
                        </c:forEach>
                    </div>
                    <div class="gp-chat-ligne">
                        <label for="question" class="gp-visuellement-cache">Votre question</label>
                        <textarea id="question" name="question" rows="1" maxlength="500" placeholder="Écrivez votre question…" required></textarea>
                        <button type="submit" class="gp-bouton" aria-label="Envoyer">
                            <svg viewBox="0 0 24 24"><path d="M5 12h14M13 6l6 6-6 6"/></svg>
                        </button>
                    </div>
                    <p class="gp-aide" style="margin:8px 0 0">Entrée pour envoyer, Maj + Entrée pour aller à la ligne. Les réponses sont générées par une IA<c:if test="${not empty utilisateur}"> à partir de votre dossier</c:if>.</p>
                </form>
            </div>
        </div>
    </main>

    <%@ include file="/WEB-INF/footer.jspf" %>

    <script>
        (function () {
            var form = document.getElementById('formChat');
            var champ = document.getElementById('question');
            var fin = document.getElementById('fin');

            function allerEnBas() {
                if (fin) fin.scrollIntoView({ block: 'end' });
            }
            // S'il y a déjà des messages, on arrive en bas de la conversation
            if (document.querySelector('.gp-bulle-client')) allerEnBas();

            // Entrée = envoyer, Maj+Entrée = nouvelle ligne
            champ.addEventListener('keydown', function (e) {
                if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    if (champ.value.trim()) form.requestSubmit();
                }
            });
            // Le champ grandit avec le texte
            champ.addEventListener('input', function () {
                champ.style.height = 'auto';
                champ.style.height = Math.min(champ.scrollHeight, 160) + 'px';
            });
            // Un clic sur une suggestion remplit le champ et envoie le formulaire
            document.querySelectorAll('.gp-suggestion').forEach(function (b) {
                b.addEventListener('click', function () {
                    champ.value = b.textContent.trim();
                    form.requestSubmit();
                });
            });
            // Indicateur "l'assistant écrit…" pendant l'appel à l'IA : la page elle-même défile
            form.addEventListener('submit', function () {
                document.getElementById('attente').hidden = false;
                allerEnBas();
                form.querySelectorAll('button').forEach(function (b) { b.disabled = true; });
            });
        })();
    </script>
</body>
</html>
