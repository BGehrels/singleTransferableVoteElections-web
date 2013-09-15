<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<jsp:useBean id="ballotLayoutBuilderBean" scope="request" type="info.gehrels.voting.web.BallotLayoutBuilderBean"/>
<!DOCTYPE HTML>
<html>
<head>
	<meta charset="UTF-8"/>
	<title>Stimmzettellayout verwalten - Wahlauszählungen nach der Weighted Inclusive Gregory Method</title>
	<style type="text/css">
		label:after {
			content: ":";
		}
	</style>
</head>
<body>
<header>
	<h1>Stimmzettelmuster</h1>
</header>
<main>
	<form:form method="post" action="/administrateBallotLayout" commandName="ballotLayoutBuilderBean">
		<c:forEach items="${ballotLayoutBuilderBean.elections}" var="election" varStatus="electionsStatus">
			<section>
				<h1>${electionsStatus.index+1}. Amt</h1>

				<label>Name des Amtes</label>
				<form:input path="elections[${electionsStatus.index}].officeName" required="required" /><br />
				<label>Anzahl Frauenplätze</label>
				<form:input path="elections[${electionsStatus.index}].numberOfFemaleExclusivePositions" type="number" required="required" min="0"/><br />
				<label>Anzahl offene Plätze</label>
				<form:input path="elections[${electionsStatus.index}].numberOfNonFemaleExclusivePositions" type="number" required="required" min="0"/><br />

				<h2>Kandidat*innen</h2>
				<c:forEach items="${election.candidates}" varStatus="candidatesStatus">
					<label>Name</label>
					<form:input path="elections[${electionsStatus.index}].candidates[${candidatesStatus.index}].name" required="required" />
					<label>weiblich</label>
					<form:radiobutton path="elections[${electionsStatus.index}].candidates[${candidatesStatus.index}].female" value="true" /> Ja
					<form:radiobutton path="elections[${electionsStatus.index}].candidates[${candidatesStatus.index}].female" value="false" /> Nein
					<br/>
				</c:forEach>
			</section>
		</c:forEach>
		<section>
			<input type="submit" value="Speichern"/>
		</section>
	</form:form>
</main>
</body>
</html>