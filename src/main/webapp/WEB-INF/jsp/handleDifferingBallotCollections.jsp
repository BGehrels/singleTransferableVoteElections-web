<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<jsp:useBean id="ballotIterableDiff" scope="request"
             type="info.gehrels.voting.web.BallotIterableDiffCalculator.BallotIterableDiff"/>
<!DOCTYPE HTML>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Wahlergebnisse - Wahlauszählungen nach der Weighted Inclusive Gregory Method</title>
</head>
<body>
<main>
<h1>Wahlberechnung nicht möglich</h1>

<p>Es gibt noch Abweichungen zwischen der Ersteingabe der Stimmzettel und der Kontrolleingabe. Es besteht nun die
    Möglichkeit, einzelne erfasste Stimmzettel wieder zu löschen, um sie erneut zu erfassen.</p>
<form method="POST" action="/deleteBallot">
<c:if test="${not empty ballotIterableDiff.setAsDuplicateIds}">
    <h2>Bei der Ersteingabe mehrfach eingegebene Stimmzettel</h2>
    <ul>
        <c:forEach var="ballotId" items="${ballotIterableDiff.setAsDuplicateIds}">
            <li>Nr. <c:out value="${ballotId}"/> <button type="submit" name="ballotId" value="${ballotId}">Stimmzettel löschen</button></li>
        </c:forEach>
    </ul>
</c:if>

<c:if test="${not empty ballotIterableDiff.setBsDuplicateIds}">
    <h2>Bei der Kontrolleingabe mehrfach eingegebene Stimmzettel</h2>
    <ul>
        <c:forEach var="ballotId" items="${ballotIterableDiff.setBsDuplicateIds}">
            <li>Nr. <c:out value="${ballotId}"/> <button type="submit" name="ballotId" value="${ballotId}">Stimmzettel löschen</button></li>
        </c:forEach>
    </ul>
</c:if>

<c:if test="${not empty ballotIterableDiff.inAButNotInB}">
    <h2>Stimmen der Ersteingabe, die bei der Kontrolleingabe ausgelassen wurden</h2>
    <ul>
        <c:forEach var="ballotId" items="${ballotIterableDiff.inAButNotInB}">
            <li>Nr. <c:out value="${ballotId}"/> <button type="submit" name="ballotId" value="${ballotId}">Stimmzettel löschen</button></li>
        </c:forEach>
    </ul>
</c:if>

<c:if test="${not empty ballotIterableDiff.inBButNotInA}">
    <h2>Stimmen der Kontrolleingabe, die bei der Ersteingabe ausgelassen wurden</h2>
    <ul>
        <c:forEach var="ballotId" items="${ballotIterableDiff.inBButNotInA}">
            <li>Nr. <c:out value="${ballotId}"/> <button type="submit" name="ballotId" value="${ballotId}">Stimmzettel löschen</button></li>
        </c:forEach>
    </ul>
</c:if>

<c:if test="${not empty ballotIterableDiff.differentBetweenTheTwoSets}">
    <h2>Stimmen bei denen sich die Ersteingabe und die Kontrolleingabe unterscheiden</h2>
    <ul>
        <c:forEach var="ballotId" items="${ballotIterableDiff.differentBetweenTheTwoSets}">
            <li>Nr. <c:out value="${ballotId}"/> <button type="submit" name="ballotId" value="${ballotId}">Stimmzettel löschen</button></li>
        </c:forEach>
    </ul>
</c:if>
</form>
</main>
<footer><a href="/">Zurück zur Startseite</a></footer>
</body>
</html>