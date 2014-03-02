<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<jsp:useBean id="ballotLayout" scope="request" type="info.gehrels.voting.web.BallotLayout"/>
<jsp:useBean id="firstOrSecondTry" scope="request" type="info.gehrels.voting.web.BallotInputTry"/>
<!DOCTYPE HTML>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Stimmzetteleingabe - Wahlauszählungen nach der Weighted Inclusive Gregory Method</title>
    <script type="text/javascript" src="/jquery-2.1.0.js"></script>
    <script type="text/javascript">
        var wigm = wigm || {};

        wigm.candidatePreferenceStringField = function (options) {
            var $element = $("#votesByElectionId0\\.preferenceString");

            var showPreference = function (preferenceString) {
                $element.val(preferenceString);
            };

            var handleChanges = function() {
                var val = $element.val();
                // TODO: Check for validity: [a-zA-Z]*
                options.onChange(val);
            };

            $element.on("keyup", handleChanges);
            //handleChanges();
            return {"showPreference": showPreference};
        };

        wigm.error = function ($element) {
            wigm.$submitButton.attr("disabled", "disabled");
        };

        wigm.resetError = function() {
            wigm.$submitButton.removeAttr("disabled");
        };

        wigm.candidatePreferenceList = function (options) {
            var $elements = $(options.listSelector).find("input");

            var handleChanges = function () {
                resetError();
                var preferenceArray = new Array($elements.size());
                $elements.each(function (idx, element) {
                    var $element = $(element);
                    var unparsedValue = $element.val();
                    if (unparsedValue.trim() !== "") {
                        // TODO: regexp als Validitätsprüfung [1-9][0-9]*
                        var val = parseInt(unparsedValue.trim());
                        if (isNaN(val) || preferenceArray[val] !== undefined) {
                            error($element);
                        }
                        var candidateIdx = $element.data("candidate-index");
                        preferenceArray[val] = candidateIdxToChar(candidateIdx);
                    }
                });

                var resultString = preferenceArray.join("");
                console.log(resultString);
                options.onChange(resultString);
            };

            var error = function($element) {
                $element.addClass("error");
                options.onError($element);
            };

            var resetError = function () {
                $elements.removeClass("error")
                options.onResetError();
            };

            var showPreference = function(preferenceString) {
                console.log(preferenceString);

                for (var i = 0; i < preferenceString.length; i++) {
                    console.log(preferenceString[i]);

                    getFieldByCandidateIdx(charToCandidateIdx(preferenceString[i])).val(i + 1);
                }
            };

            var candidateIdxToChar = function (candidateIdx) {
                return String.fromCharCode(candidateIdx + 65);
            };

            var charToCandidateIdx = function(candidateChar) {
                return candidateChar.toUpperCase().charCodeAt(0)-65;
            };

            var getFieldByCandidateIdx = function (candidateIdx) {
                console.log("candidateIdx", candidateIdx);
                var find = $elements.filter("[data-candidate-index=\"" + candidateIdx + "\"]");
                // TODO: Prüfen, ob find leer ist
                console.log(find);
                return   find;
            };

            $elements.each(function (idx, $element) {
                $($element).on("keyup", handleChanges).removeAttr("disabled");
            });

            //handleChanges();
            return {
                "resetError": resetError,
                "showPreference": showPreference
            }
        };

        $(document).ready(function () {
            var preferenceList;
            // TODO: Alle möglichen Felder aktivieren
            wigm.$submitButton = $("input[type=\"submit\"]");
            var stringField = wigm.candidatePreferenceStringField({"onChange": function(preferenceString) {
                preferenceList.showPreference(preferenceString);
            }});
            preferenceList = wigm.candidatePreferenceList({"listSelector": "#votesByElectionId0_htmlList",
                                             "onChange": stringField.showPreference,
                                             "onResetError": wigm.resetError,
                                             "onError": wigm.error});
        });


    </script>
    <style type="text/css">
        label.error {
            color: red;
        }

        input.error {
            border-color: red;
        }
    </style>
</head>
<body>
<header>
    <h1>Stimmzettel eingeben</h1>
</header>
<main>
    <form:form method="POST" action="/castVote" commandName="ballotBuilder">
        <input type="hidden" name="firstOrSecondTry" value="${firstOrSecondTry}"/>
        <section>
            <form:label path="ballotId" cssErrorClass="error">Stimmzettelnummer</form:label>
            <form:input path="ballotId" cssErrorClass="error" type="number" required="required" autofocus="autofocus"/>
            <form:errors path="ballotId" cssClass="error"/><br/>
        </section>
        <c:forEach items="${ballotLayout.elections}" var="election" varStatus="electionStatus">
            <h2><c:out value="${election.officeName}"/></h2>
            <ol id="votesByElectionId${electionStatus.index}_htmlList" type="A">
                <c:forEach items="${election.candidates}" varStatus="candidateStatus" var="candidate">
                    <li>
                        <input type="number" data-candidate-index="${candidateStatus.index}" disabled="disabled">
                        <c:out value="${candidate.name}"/>
                    </li>
                </c:forEach>
            </ol>
            <ol type="A">
                <c:forEach items="${election.candidates}" var="candidate">
                    <li><c:out value="${candidate.name}"/></li>
                </c:forEach>
            </ol>
            <form:radiobutton path="votesByElectionId[${electionStatus.index}].type" cssErrorClass="error"
                              value="PREFERENCE" label="Präferenz"/>
            <form:input path="votesByElectionId[${electionStatus.index}].preferenceString" cssErrorClass="error"
                        type="text"/>
            <form:errors path="votesByElectionId[${electionStatus.index}].preferenceString" cssClass="error"/><br/>
            <form:radiobutton path="votesByElectionId[${electionStatus.index}].type" cssErrorClass="error" value="NO"
                              label="Nein"/><br/>
            <form:radiobutton path="votesByElectionId[${electionStatus.index}].type" cssErrorClass="error"
                              value="INVALID" label="Ungültig"/><br/>
            <form:radiobutton path="votesByElectionId[${electionStatus.index}].type" cssErrorClass="error"
                              value="NOT_VOTED" label="Keine Stimmabgabe"/>
        </c:forEach>
        <section>
            <input type="submit" value="Hinzufügen &amp; nächsten Stimmzettel ausfüllen"/>
        </section>
    </form:form>
</main>
<footer><a href="/">Zurück zur Startseite</a></footer>
</body>
</html>