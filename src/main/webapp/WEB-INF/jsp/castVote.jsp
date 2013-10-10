<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<jsp:useBean id="ballotLayout" scope="request" type="info.gehrels.voting.web.BallotLayout"/>
<!DOCTYPE HTML>
<html>
<head>
	<meta charset="UTF-8"/>
	<title>Stimmzetteleingabe - Wahlauszählungen nach der Weighted Inclusive Gregory Method</title>
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
	<h1>Stimmzettel eingeben</h1>
</header>
<main>
	<form:form method="POST" action="/castVote" commandName="castVoteForm">
		<section>
			<form:label path="ballotId" cssErrorClass="error">Stimmzettelnummer</form:label>
			<form:input path="ballotId" type="number" required="required"/>
			<form:errors path="ballotId" cssClass="error" /><br />
		</section>
		<c:forEach items="${ballotLayout.elections}" var="election" varStatus="electionStatus">
			<h2><c:out value="${election.officeName}"/></h2>
			<ol>
				<c:forEach items="${election.candidates}" var="candidate">
					<li><c:out value="${candidate.name}" /></li>
				</c:forEach>
			</ol>
			<form:label path="votesByElectionId[${electionStatus.index}]" cssErrorClass="error">Präferenz</form:label>
			<form:input path="votesByElectionId[${electionStatus.index}]" type="text" />
			<form:errors path="votesByElectionId[${electionStatus.index}]" cssClass="error" /><br />
		</c:forEach>
		<section>
			<input type="submit" value="Hinzufügen &amp; nächsten Stimmzettel ausfüllen"/>
		</section>
	</form:form>
</main>
</body>
</html>