<%@ page pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Startseite - Wahlauszählungen nach der Weighted Inclusive Gregory Method</title>
</head>
<body>
<header>
    <h1>Was möchten Sie tun?</h1>
</header>
<main>
    <ul>
        <li><form method="GET" action="/administrateBallotLayout">Stimmzettellayout für <input type="number" name="numberOfElections" /> Ämter erstellen: <input type="submit"></form></li>
        <li><a href="/castVote">Stimmen eingeben</a></li>
    </ul>
</main>
</body>
</html>