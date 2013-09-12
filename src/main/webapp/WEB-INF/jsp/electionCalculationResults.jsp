<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="resultModel" scope="request" type="com.google.common.collect.ImmutableList<info.gehrels.voting.web.ElectionCalculationResultBean>"/>
<!DOCTYPE HTML>
<html>
<head>
	<meta charset="UTF-8"/>
	<title>Wahlergebnisse - Wahlauszählungen nach der Weighted Inclusive Gregory Method</title>
</head>
<body>
<h1>Wahlergebnisse</h1>
<c:forEach var="calculationResult" items="${resultModel}">
<section>
	<h1><c:out value="${calculationResult.election.officeName}" /></h1>
	<h2>Auf die <c:out value="${calculationResult.election.numberOfFemaleExclusivePositions}" /> Frauenplätze wurden gewählt:</h2>
	<ul>
	<c:forEach var="electedCandidate" items="${calculationResult.electionResult.candidatesElectedInFemaleOnlyRun}">
		<li><c:out value="${electedCandidate.name}" /></li>
	</c:forEach>
	</ul>
	<h2>Auf die <c:out value="${calculationResult.election.numberOfNotFemaleExclusivePositions}" /> offenen Plätze wurden gewählt:</h2>
	<ul>
	<c:forEach var="electedCandidate" items="${calculationResult.electionResult.candidatesElectedInNonFemaleOnlyRun}">
		<li><c:out value="${electedCandidate.name}" /></li>
	</c:forEach>
	</ul>
	<h2>Detailiertes Ablaufprotokoll</h2>
	<pre><c:out value="${calculationResult.auditLog}" /></pre>
</section>
</c:forEach>
</body>
</html>