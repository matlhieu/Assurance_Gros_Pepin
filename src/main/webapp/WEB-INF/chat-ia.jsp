<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Assistant IA</title>
</head>
<body>
<h2>Assistant Assurance IA</h2>

<form method="post" action="${pageContext.request.contextPath}/client/assistant">
    <label>Pose ta question sur tes contrats ou garanties :</label><br><br>
    <textarea name="question" rows="4" cols="50" required>${question}</textarea><br><br>
    <button type="submit">Envoyer</button>
</form>

<c:if test="${not empty answer}">
    <hr>
    <h3>Réponse de l'assistant :</h3>
    <p style="white-space: pre-line;">${answer}</p>
</c:if>
</body>
</html>