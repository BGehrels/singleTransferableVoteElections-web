<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="numberOfElections" scope="request" type="java.lang.Integer"/>
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
	<form action="/administrateBallotLayout" method="POST">
		<c:forEach begin="0" end="${numberOfElections-1}" var="i">
			<section>
				<h1>${i+1}. Amt</h1>

				<label>Name des Amtes</label>
				<input name="elections[${i}].officeName" type="text" required/><br/>
				<label>Anzahl Frauenplätze</label>
				<input name="elections[${i}].numberOfFemaleExclusivePositions" type="number" required/><br/>
				<label>Anzahl offene Plätze</label>
				<input name="elections[${i}].numberOfNonFemaleExclusivePositions" type="number" required/><br/>

				<h2>Kandidat*innen</h2>
				<c:forEach begin="0" end="${numberOfCandidatesPerElection-1}" var="j">
					<label>Name</label>
					<input name="elections[${i}].candidates[${j}].name"/>
					<label>weiblich</label>
					<input name="elections[${i}].candidates[${j}].female"/>
					<br/>
				</c:forEach>
			</section>
		</c:forEach>
		<section>
			<input type="submit" value="Speichern"/>
		</section>
	</form>
</main>
</body>
</html>