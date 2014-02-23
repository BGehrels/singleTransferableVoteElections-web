<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="electionCalculation" scope="request" type="info.gehrels.voting.web.AsyncElectionCalculation.Snapshot"/>
<!DOCTYPE HTML>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Ergebnisberechnungen vom <c:out value="${electionCalculation.startDateTime}"/> - Wahlauszählungen nach der
        Weighted Inclusive Gregory Method</title>
</head>
<body>
<header>
    <h1>Ergebnisberechnungen vom <c:out value="${electionCalculation.startDateTime}"/></h1>
</header>
<main>
    <h1>Bereits abgeschlossene Berechnungen</h1>
    <c:forEach var="calculationResult" items="${resultModel}">
        <section>
            <h1><c:out value="${calculationResult.election.officeName}"/></h1>

            <h2>Auf die <c:out value="${calculationResult.election.numberOfFemaleExclusivePositions}"/> Frauenplätze
                wurden gewählt:</h2>
            <ul>
                <c:forEach var="electedCandidate"
                           items="${calculationResult.electionResult.candidatesElectedInFemaleOnlyRun}">
                    <li><c:out value="${electedCandidate.name}"/></li>
                </c:forEach>
            </ul>
            <h2>Auf die <c:out value="${calculationResult.election.numberOfNotFemaleExclusivePositions}"/> offenen
                Plätze wurden gewählt:</h2>
            <ul>
                <c:forEach var="electedCandidate"
                           items="${calculationResult.electionResult.candidatesElectedInNonFemaleOnlyRun}">
                    <li><c:out value="${electedCandidate.name}"/></li>
                </c:forEach>
            </ul>
            <h2>Detailiertes Ablaufprotokoll</h2>
            <pre><c:out value="${calculationResult.auditLog}"/></pre>
        </section>
    </c:forEach>
</main>
<footer><a href="/">Zurück zur Startseite</a></footer>
</body>
</html>