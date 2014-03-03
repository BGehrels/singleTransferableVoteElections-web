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
            var $element = options.$fieldElement;

            var showPreference = function (preferenceString) {
                $element.val(preferenceString);
            };

            var handleChanges = function () {
                var val = $element.val();
                resetError();
                // TODO: Check for validity: [a-zA-Z]*
                try {
                    options.onChange(val);
                } catch (err) {
                    error()
                }
            };

            var resetError = function () {
                $element.removeClass("error");
                options.onResetError();
            };

            var error = function () {
                $element.addClass("error");
                options.onError();
            };

            var deactivate = function () {
                $element.addClass("invisible");
            };

            var activate = function () {
                $element.removeClass("invisible");
            };

            $element.on("keyup", handleChanges);
            return {
                "showPreference": showPreference,
                "init" : handleChanges,
                "activate" : activate,
                "deactivate" : deactivate
            };
        };

        wigm.error = function ($element) {
            wigm.$submitButton.attr("disabled", "disabled");
        };

        wigm.resetError = function () {
            wigm.$submitButton.removeAttr("disabled");
        };

        wigm.candidatePreferenceList = function (options) {
            var $elements = options.$listElement.find("input");

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
                options.onChange(resultString);
            };

            var error = function ($element) {
                $element.addClass("error");
                options.onError($element);
            };

            var resetError = function () {
                $elements.removeClass("error")
                options.onResetError();
            };

            var showPreference = function (preferenceString) {
                $elements.val("");
                for (var i = 0; i < preferenceString.length; i++) {
                    var $candidateInput = getFieldByCandidateIdx(charToCandidateIdx(preferenceString[i]));
                    if ($candidateInput.size() === 0) {
                        throw preferenceString[i];
                    }
                    $candidateInput.val(i + 1);
                }
            };

            var candidateIdxToChar = function (candidateIdx) {
                return String.fromCharCode(candidateIdx + 65);
            };

            var charToCandidateIdx = function (candidateChar) {
                return candidateChar.toUpperCase().charCodeAt(0) - 65;
            };

            var getFieldByCandidateIdx = function (candidateIdx) {
                return $elements.filter("[data-candidate-index=\"" + candidateIdx + "\"]");
            };

            var activate = function () {
                $elements.removeClass("invisible");
            };

            var deactivate = function() {
                $elements.addClass("invisible");
            };

            $elements.each(function (idx, $element) {
                $($element).on("keyup", handleChanges).removeAttr("disabled");
            });

            return {
                "showPreference": showPreference,
                "init" : handleChanges,
                "activate" : activate,
                "deactivate" : deactivate
            }
        };

        wigm.initSingleElection = function(electionElement) {
            var preferenceList;
            var stringField = wigm.candidatePreferenceStringField({
                                                                      "$fieldElement": $(electionElement).find(".preferenceString"),
                                                                      "onChange": function (preferenceString) {
                                                                          preferenceList.showPreference(preferenceString);
                                                                      },
                                                                      "onResetError": wigm.resetError,
                                                                      "onError": wigm.error});
            preferenceList = wigm.candidatePreferenceList({
                                                              "$listElement": $(electionElement).find("ol"),
                                                              "onChange": stringField.showPreference,
                                                              "onResetError": wigm.resetError,
                                                              "onError": wigm.error});
            stringField.init();
            preferenceList.init();

            var toggleInputMode = function (isStringInputModeEnabled) {
                if (isStringInputModeEnabled) {
                    preferenceList.deactivate();
                    stringField.activate();
                } else {
                    preferenceList.activate();
                    stringField.deactivate();
                }
            };

            wigm.$stringInputModeCheckbox.change(function () {
                toggleInputMode($(this).is(':checked'));
            });

            toggleInputMode(wigm.$stringInputModeCheckbox.is(':checked'));
        };


        $(document).ready(function () {
            wigm.$submitButton = $("input[type=\"submit\"]");
            wigm.$stringInputModeCheckbox = $("#stringInputMode1");

            $("section.election").each(function (idx, electionElement) {
                wigm.initSingleElection(electionElement);
            });
        });


    </script>
    <style type="text/css">
        label.error {
            color: red;
        }

        input.error {
            border-color: red;
        }

        .invisible {
            display: none;
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
            <form:label path="stringInputMode" cssErrorClass="error">Schnelleingabemodus</form:label>
            <form:checkbox path="stringInputMode"/>
        </section>
        <c:forEach items="${ballotLayout.elections}" var="election" varStatus="electionStatus">
            <section class="election">
                <h2><c:out value="${election.officeName}"/></h2>
                <ol type="A">
                    <c:forEach items="${election.candidates}" varStatus="candidateStatus" var="candidate">
                        <li>
                            <input type="number" size="2" data-candidate-index="${candidateStatus.index}" disabled="disabled" class="invisible">
                            <c:out value="${candidate.name}"/>
                        </li>
                    </c:forEach>
                </ol>
                <form:radiobutton path="votesByElectionId[${electionStatus.index}].type" cssErrorClass="error"
                                  value="PREFERENCE" label="Präferenz"/>
                <form:input path="votesByElectionId[${electionStatus.index}].preferenceString" cssErrorClass="error"
                            type="text" cssClass="preferenceString"/>
                <form:errors path="votesByElectionId[${electionStatus.index}].preferenceString" cssClass="error"/><br/>
                <form:radiobutton path="votesByElectionId[${electionStatus.index}].type" cssErrorClass="error" value="NO"
                                  label="Nein"/><br/>
                <form:radiobutton path="votesByElectionId[${electionStatus.index}].type" cssErrorClass="error"
                                  value="INVALID" label="Ungültig"/><br/>
                <form:radiobutton path="votesByElectionId[${electionStatus.index}].type" cssErrorClass="error"
                                  value="NOT_VOTED" label="Keine Stimmabgabe"/>
            </section>
        </c:forEach>
        </section>
        <section>
            <input type="submit" value="Hinzufügen &amp; nächsten Stimmzettel ausfüllen"/>
        </section>
    </form:form>
</main>
<footer><a href="/">Zurück zur Startseite</a></footer>
</body>
</html>