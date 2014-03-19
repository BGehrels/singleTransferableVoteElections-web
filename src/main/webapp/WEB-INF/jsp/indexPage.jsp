<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page pageEncoding="UTF-8"%>
<jsp:useBean id="numberOfCastBallots" scope="request" type="info.gehrels.voting.web.IndexPageController.NumberOfCastBallotsBean"/>
<!DOCTYPE HTML>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Startseite - Wahlauszählungen nach der Weighted Inclusive Gregory Method</title>
</head>
<body>
<header>
    <h1>Was möchten Sie tun?</h1>
</header>
<main>
    <ul>
        <li><a href="/administrateBallotLayout">Stimmzettellayout erstellen/bearbeiten (<strong>löscht eventuell bereits einegebene Stimmen</strong>)</a></li>
        <li><a href="/castVote?firstOrSecondTry=FIRST">Stimmen eingeben (Ersteingabe)</a></li>
        <li><a href="/castVote?firstOrSecondTry=SECOND">Stimmen eingeben (Kontrolleingabe)</a></li>
        <li><a href="/listElectionCalculations">Ergebnisberechnung</a></li>
    </ul>
</main>
<aside>
    <dl>
        <h1>Anzahl eingegebener Stimmzettel</h1>
        <dt>Ersteingabe</dt>
        <dd><c:out value="${numberOfCastBallots.firstTry}" /></dd>
        <dt>Kontrolleingabe</dt>
        <dd><c:out value="${numberOfCastBallots.secondTry}" /></dd>
    </dl>
</aside>
</body>
</html>