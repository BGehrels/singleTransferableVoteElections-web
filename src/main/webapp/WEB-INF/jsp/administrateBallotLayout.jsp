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

		form .error {
			color: red;
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

				<form:label path="elections[${electionsStatus.index}].officeName" cssErrorClass="error">Name des Amtes</form:label>
				<form:input path="elections[${electionsStatus.index}].officeName" required="required"  size="60" />
				<form:errors path="elections[${electionsStatus.index}].officeName" cssClass="error" /><br />
				<form:label path="elections[${electionsStatus.index}].numberOfFemaleExclusivePositions" cssErrorClass="error">Anzahl Frauenplätze</form:label>
				<form:input path="elections[${electionsStatus.index}].numberOfFemaleExclusivePositions" type="number" required="required" min="0" size="3"/>
				<form:errors path="elections[${electionsStatus.index}].numberOfFemaleExclusivePositions" cssClass="error" /><br />
				<form:label path="elections[${electionsStatus.index}].numberOfNonFemaleExclusivePositions" cssErrorClass="error">Anzahl offene Plätze</form:label>
				<form:input path="elections[${electionsStatus.index}].numberOfNonFemaleExclusivePositions" type="number" required="required" min="0" size="3"/>
				<form:errors path="elections[${electionsStatus.index}].numberOfNonFemaleExclusivePositions" cssClass="error" /><br />

				<h2>Kandidat*innen</h2>
				<c:forEach items="${election.candidates}" varStatus="candidatesStatus">
					<form:label path="elections[${electionsStatus.index}].candidates[${candidatesStatus.index}].name" cssErrorClass="error">Name</form:label>
					<form:input path="elections[${electionsStatus.index}].candidates[${candidatesStatus.index}].name" required="required" size="60" />
					<label>weiblich</label>
					<form:radiobutton path="elections[${electionsStatus.index}].candidates[${candidatesStatus.index}].female" value="true" /> Ja
					<form:radiobutton path="elections[${electionsStatus.index}].candidates[${candidatesStatus.index}].female" value="false" /> Nein
					<form:errors path="elections[${electionsStatus.index}].candidates[${candidatesStatus.index}].name" cssClass="error" />
					<form:errors path="elections[${electionsStatus.index}].candidates[${candidatesStatus.index}].female" cssClass="error" /><br />
					<br/>
				</c:forEach>
				<form:button name="addNewCandidate" value="${electionsStatus.index}">Weitere_n Kandidat_in hinzufügen</form:button>
			</section>
		</c:forEach>
		<section>
			<form:button type="submit" name="addNewElection">Weiteres Amt hinzufügen</form:button>
			<input type="submit" value="Stimmzettel vollständig eingegeben"/>
		</section>
	</form:form>
</main>
<footer><a href="/">Zurück zur Startseite</a></footer>
</body>
</html>