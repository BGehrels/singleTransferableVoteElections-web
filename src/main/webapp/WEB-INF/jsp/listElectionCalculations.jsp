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
<jsp:useBean id="electionCalculations" scope="request"
             type="java.util.Map<org.joda.time.DateTime, info.gehrels.voting.web.AsyncElectionCalculation>"/>
<!DOCTYPE HTML>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Ergebnisberechnungen - Wahlauszählungen nach der Weighted Inclusive Gregory Method</title>
</head>
<body>
<header>
    <h1>Ergebnisberechnung</h1>
</header>
<main>
    <h2>Neue Berechnung starten</h2>

    <p>
        Startet eine neue Wahlergebnisberechnung mit den bereits eingegebenen Stimmzetteln. Sollten die Stimmzettel aus
        der Erst- und Kontrolleingabe nich identisch sein, wird die Gelegenheit zur Korrektur gegeben. Mit dem Starten
        der Ergebnisberechnung gehen die eingegebenen Stimmzettel nicht verloren, die Berechnung kann beliebig oft
        gestartet werden.
    </p>

    <form action="/startElectionCalculation" method="POST">
        <input type="submit" value="Ergebnisberechnung jetzt starten"/>
    </form>
    <c:if test="${not empty electionCalculations}">
        <h2>Laufende und Abgeschlossene Berechnungen</h2>
        <ul>
            <c:forEach var="electionCalculation" items="${electionCalculations}">
                <c:url value="/showElectionCalculation" var="url">
                    <c:param name="dateTimeTheCalculationStarted" value="${electionCalculation.key}" />
                </c:url>
                <li><a href="${url}">Berechnung
                    vom <c:out value="${electionCalculation.key}"/></a></li>
            </c:forEach>
        </ul>
    </c:if>
</main>
<footer><a href="/">Zurück zur Startseite</a></footer>
</body>
</html>