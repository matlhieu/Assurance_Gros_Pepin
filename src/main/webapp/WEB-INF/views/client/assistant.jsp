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
                    <li><a href="${pageContext.request.contextPath}/client/dashboard">Mon espace</a></li>
                    <li>Assistant</li>
                </ol>
                <div class="gp-bandeau-ligne">
                    <div>
                        <h1 class="gp-titre">Votre assistant</h1>
                        <p class="gp-sous-titre">Une question sur vos contrats, vos garanties ou un sinistre ? L'assistant connaît votre dossier et vous répond tout de suite.</p>
                    </div>
                    <c:if test="${not empty conversation}">
                        <form method="post" action="${pageContext.request.contextPath}/client/assistant" class="gp-bandeau-actions">
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
                <div class="gp-chat-fil" id="fil">
                    <%-- Message d'accueil --%>
                    <div class="gp-bulle gp-bulle-ia">
                        <span class="gp-bulle-avatar"><img src="${pageContext.request.contextPath}/image/logo.png" alt=""></span>
                        <div class="gp-bulle-texte">
                            <p>Bonjour <c:out value="${utilisateur.prenom}"/> ! Je suis l'assistant Gros Pépin.
                               Je peux vous renseigner sur vos contrats, vos garanties, vos paiements et le suivi de vos sinistres.</p>
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

                <form method="post" action="${pageContext.request.contextPath}/client/assistant" class="gp-chat-saisie" id="formChat">
                    <div class="gp-suggestions" aria-label="Questions fréquentes">
                        <c:forEach var="q" items="${['Quels sont mes contrats actifs ?', 'Quelles garanties possède mon assurance automobile ?', 'Où en est mon dernier sinistre ?', 'Comment déclarer un sinistre ?', 'Quels documents dois-je fournir pour mon sinistre ?', 'Quelle est la différence entre la garantie vol et la responsabilité civile ?']}">
                            <button type="submit" name="question" value="${q}" class="gp-suggestion">${q}</button>
                        </c:forEach>
                    </div>
                    <div class="gp-chat-ligne">
                        <label for="question" class="gp-visuellement-cache">Votre question</label>
                        <textarea id="question" name="question" rows="1" maxlength="500" placeholder="Écrivez votre question…" required></textarea>
                        <button type="submit" class="gp-bouton" aria-label="Envoyer">
                            <svg viewBox="0 0 24 24"><path d="M5 12h14M13 6l6 6-6 6"/></svg>
                        </button>
                    </div>
                    <p class="gp-aide" style="margin:8px 0 0">Entrée pour envoyer, Maj + Entrée pour aller à la ligne. Les réponses sont générées par une IA à partir de votre dossier.</p>
                </form>
            </div>
        </div>
    </main>

    <%@ include file="/WEB-INF/footer.jspf" %>

    <script>
        (function () {
            var form = document.getElementById('formChat');
            var champ = document.getElementById('question');
            var fil = document.getElementById('fil');
            fil.scrollTop = fil.scrollHeight;

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
            // Les boutons de suggestion ne doivent pas être bloqués par le "required" du champ
            document.querySelectorAll('.gp-suggestion').forEach(function (b) {
                b.addEventListener('click', function () { champ.required = false; });
            });
            // Indicateur "l'assistant écrit…" pendant l'appel à l'IA
            form.addEventListener('submit', function () {
                document.getElementById('attente').hidden = false;
                fil.scrollTop = fil.scrollHeight;
                form.querySelectorAll('button').forEach(function (b) { b.disabled = true; });
            });
        })();
    </script>
</body>
</html>
