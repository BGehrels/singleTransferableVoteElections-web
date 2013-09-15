<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="ballotLayout" scope="request" type="info.gehrels.voting.web.BallotLayout"/>
<!DOCTYPE HTML>
<html>
<head>
	<meta charset="UTF-8"/>
	<title>Stimmzetteleingabe - Wahlauszählungen nach der Weighted Inclusive Gregory Method</title>
</head>
<body>
<header>
	<h1>Stimmzettel eingeben</h1>
</header>
<main>
	<form method="POST">
		<section>
			<label for="ballotId">Stimmzettelnummer</label>
			<input id="ballotId" name="ballotId" type="number" required="required"/>
		</section>
		<c:forEach items="${ballotLayout.elections}" var="election" varStatus="electionStatus">
			<h2><c:out value="${election.officeName}"/></h2>
			<ol>
				<c:forEach items="${election.candidates}" var="candidate">
					<li><c:out value="${candidate.name}" /></li>
				</c:forEach>
			</ol>
			<label for="votesByElectionId[${electionStatus.index}]" >Präferenz</label><input type="text" id="votesByElectionId[${electionStatus.index}]" name="votesByElectionId[${electionStatus.index}]" />
		</c:forEach>
		<section>
			<input type="submit" value="Hinzufügen &amp; nächsten Stimmzettel ausfüllen"/>
		</section>
	</form>
</main>
</body>
</html>