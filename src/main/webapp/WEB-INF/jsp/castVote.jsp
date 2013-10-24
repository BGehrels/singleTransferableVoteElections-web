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
		label .error {
			color: red;
		}

        input .error {
        			background-color: red;
        		}
	</style>
</head>
<body>
<header>
	<h1>Stimmzettel eingeben</h1>
</header>
<main>
	<form:form method="POST" action="/castVote" commandName="castVoteBuilder">
		<section>
			<form:label path="ballotId" cssErrorClass="error">Stimmzettelnummer</form:label>
			<form:input path="ballotId" cssErrorClass="error" type="number" required="required"/>
			<form:errors path="ballotId" cssClass="error" /><br />
		</section>
		<c:forEach items="${ballotLayout.elections}" var="election" varStatus="electionStatus">
			<h2><c:out value="${election.officeName}"/></h2>
			<ol>
				<c:forEach items="${election.candidates}" var="candidate">
					<li><c:out value="${candidate.name}" /></li>
				</c:forEach>
			</ol>
            <form:radiobutton path="votesByElectionId[${electionStatus.index}].type" cssErrorClass="error" value="PREFERENCE" label="Präferenz"/>
			<form:input path="votesByElectionId[${electionStatus.index}].preferenceString" cssErrorClass="error" type="text" />
			<form:errors path="votesByElectionId[${electionStatus.index}].preferenceString" cssClass="error" /><br />
            <form:radiobutton path="votesByElectionId[${electionStatus.index}].type" cssErrorClass="error" value="NO" label="Nein" /><br/>
            <form:radiobutton path="votesByElectionId[${electionStatus.index}].type" cssErrorClass="error" value="NOT_VOTED" label="Keine Stimmabgabe" />
		</c:forEach>
		<section>
			<input type="submit" value="Hinzufügen &amp; nächsten Stimmzettel ausfüllen"/>
		</section>
	</form:form>
</main>
</body>
</html>