<%--
 Copyright © 2014 Benjamin Gehrels

 This file is part of The Single Transferable Vote Elections Web Interface.

 The Single Transferable Vote Elections Web Interface is free software: you can redistribute it and/or modify it under
 the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.

 The Single Transferable Vote Elections Web Interface is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License along with The Single Transferable Vote
 Elections Web Interface. If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
    <h1>Status</h1>
    <c:out value="${electionCalculation.state.description}"/>
    <c:if test="${electionCalculation.state == 'MANUAL_AMBIGUITY_RESOLUTION_NECCESSARY'}">
        <h1>Manuelle Auswahl von gewinnenden/verlierenden Kandidierenden</h1>
        <c:url var="postUrl" value="/resolveAmbiguity">
            <c:param name="dateTimeTheCalculationStarted" value="${electionCalculation.startDateTime}"/>
        </c:url>
        <form:form method="POST" action="${postUrl}" commandName="ambiguityResolverResultBuilder">
            <c:forEach items="${electionCalculation.ambiguityResulutionTask.candidatesToChooseFrom}" var="candidate">
                <form:radiobutton path="candidateName" value="${candidate.name}" label="${candidate.name}" required="required" /><br />
            </c:forEach>
            <form:errors path="candidateName" cssClass="error" />
            <form:label path="comment">Kommentar:</form:label> <form:input path="comment" required="required" /> <form:errors path="comment" cssClass="error" /><br />
            <input type="submit" />
        </form:form>
        <h2>Bisheriges Ablaufprotokoll</h2>
        <pre><c:out value="${electionCalculation.ambiguityResulutionTask.currentLog}"/></pre>
    </c:if>
    <c:if test="${not empty electionCalculation.resultsOfFinishedCalculations}">
        <h1>Bereits abgeschlossene Berechnungen</h1>
        <c:forEach var="calculationResult" items="${electionCalculation.resultsOfFinishedCalculations}">
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
    </c:if>
</main>
<footer><a href="/">Zurück zur Startseite</a></footer>
</body>
</html>