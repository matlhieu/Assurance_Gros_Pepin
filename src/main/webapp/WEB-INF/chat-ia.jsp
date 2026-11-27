<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Assistant IA – Assurance Gros Pépin</title>
    <link rel="icon" href="${pageContext.request.contextPath}/image/logo.png">
    <link href="https://fonts.googleapis.com/css2?family=Nunito+Sans:wght@400;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=9">
    <style>
        .page-container {
            max-width: 860px;
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
        .chat-card {
            background: #fff;
            border: 1px solid var(--bordure);
            border-radius: 8px;
            padding: 28px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.04);
            margin-bottom: 24px;
        }
        .chat-form {
            display: flex;
            flex-direction: column;
            gap: 16px;
        }
        .chat-form label {
            font-weight: 700;
            font-size: 1.05rem;
            color: var(--texte);
        }
        .chat-form textarea {
            width: 100%;
            min-height: 110px;
            padding: 14px;
            border: 1px solid var(--bordure);
            border-radius: 6px;
            font-family: inherit;
            font-size: 1rem;
            color: var(--texte);
            resize: vertical;
            background: #fafafc;
            box-sizing: border-box;
        }
        .chat-form textarea:focus {
            outline: none;
            border-color: var(--orchidee);
            box-shadow: 0 0 0 2px rgba(183, 68, 184, 0.2);
            background: #fff;
        }
        .chat-form button {
            align-self: flex-end;
            padding: 12px 28px;
            font-size: 1rem;
            font-weight: 700;
            color: #fff;
            background: var(--orchidee);
            border: none;
            border-radius: 4px;
            cursor: pointer;
            transition: background 0.15s;
        }
        .chat-form button:hover {
            background: var(--orchidee-fonce);
        }
        .response-card {
            background: #fdfafc;
            border: 1px solid rgba(183, 68, 184, 0.25);
            border-left: 4px solid var(--orchidee);
            border-radius: 6px;
            padding: 24px;
            margin-top: 24px;
        }
        .response-header {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 14px;
        }
        .response-title {
            font-weight: 800;
            font-size: 1.1rem;
            color: var(--orchidee);
            margin: 0;
        }
        .response-text {
            color: var(--texte);
            font-size: 1.02rem;
            line-height: 1.6;
            white-space: pre-line;
            margin: 0;
        }
        .user-question-echo {
            font-style: italic;
            color: var(--texte-doux);
            margin-bottom: 12px;
            font-size: 0.95rem;
        }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>

    <main class="page-container">
        <div class="page-header">
            <h1 class="page-title">Assistant Virtuel Gros Pépin</h1>
            <p class="page-subtitle">Posez toutes vos questions sur vos contrats, garanties et échéances de paiement.</p>
        </div>

        <div class="chat-card">
            <form method="post" action="${pageContext.request.contextPath}/client/assistant" class="chat-form">
                <label for="question">Votre question :</label>
                <textarea id="question" name="question" placeholder="Exemple : Quels sont mes contrats actifs ? Quel est le montant de ma prochaine cotisation ?" required>${question}</textarea>
                <button type="submit">Poser ma question</button>
            </form>

            <c:if test="${not empty answer}">
                <div class="response-card">
                    <div class="response-header">
                        <h2 class="response-title">Réponse de l'Assistant :</h2>
                    </div>
                    <c:if test="${not empty question}">
                        <div class="user-question-echo">
                            <strong>Question :</strong> ${question}
                        </div>
                    </c:if>
                    <p class="response-text">${answer}</p>
                </div>
            </c:if>
        </div>
    </main>

    <%@ include file="/WEB-INF/footer.jspf" %>
</body>
</html>
