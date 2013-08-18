<%@ page pageEncoding="UTF-8" %>
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
	<form method="POST">
		<section>
			<h2>Weiteres Amt hinzufügen</h2>

			<label>Name des Amtes</label>
			<input name="elections[0].officeName" type="text" required /><br/>
			<label>Anzahl Frauenplätze</label>
			<input name="elections[0].numberOfFemaleExclusivePositions" type="number" required /><br/>
			<label>Anzahl offene Plätze</label>
			<input name="elections[0].numberOfNonFemaleExclusivePositions" type="number" required /><br/>

			<h3>Kandidat*innen</h3>
			<label>Name</label>
			<input name="elections[0].candidates[0].name"/>
			<label>weiblich</label>
			<input name="elections[0].candidates[0].female"/>
			<br/>
			<label>Name</label>
			<input name="elections[0].candidates[1].name"/>
			<label>weiblich</label>
			<input name="elections[0].candidates[1].female"/>
		</section>
		<section>
			<h2>Weiteres Amt hinzufügen</h2>

			<label>Name des Amtes</label>
			<input name="elections[1].officeName"/><br/>
			<label>Anzahl Frauenplätze</label>
			<input name="elections[1].numberOfFemaleExclusivePositions"/><br/>
			<label>Anzahl offene Plätze</label>
			<input name="elections[1].numberOfNonFemaleExclusivePositions"/><br/>

			<h3>Kandidat*innen</h3>
			<label>Name</label>
			<input name="elections[1].candidates[0].name"/>
			<label>weiblich</label>
			<input name="elections[1].candidates[0].female"/>
			<br/>
			<label>Name</label>
			<input name="elections[1].candidates[1].name"/>
			<label>weiblich</label>
			<input name="elections[1].candidates[1].female"/>
		</section>
		<section>
			<h2>Weiteres Amt hinzufügen</h2>

			<label>Name des Amtes</label>
			<input name="elections[2].officeName"/><br/>
			<label>Anzahl Frauenplätze</label>
			<input name="elections[2].numberOfFemaleExclusivePositions"/><br/>
			<label>Anzahl offene Plätze</label>
			<input name="elections[2].numberOfNonFemaleExclusivePositions"/><br/>

			<h3>Kandidat*innen</h3>
			<label>Name</label>
			<input name="elections[2].candidates[0].name"/>
			<label>weiblich</label>
			<input name="elections[2].candidates[0].female"/>
			<br/>
			<label>Name</label>
			<input name="elections[2].candidates[1].name"/>
			<label>weiblich</label>
			<input name="elections[2].candidates[1].female"/>
		</section>
		<section>
			<h2>Weiteres Amt hinzufügen</h2>

			<label>Name des Amtes</label>
			<input name="elections[3].officeName"/><br/>
			<label>Anzahl Frauenplätze</label>
			<input name="elections[3].numberOfFemaleExclusivePositions"/><br/>
			<label>Anzahl offene Plätze</label>
			<input name="elections[3].numberOfNonFemaleExclusivePositions"/><br/>

			<h3>Kandidat*innen</h3>
			<label>Name</label>
			<input name="elections[3].candidates[0].name"/>
			<label>weiblich</label>
			<input name="elections[3].candidates[0].female"/>
			<br/>
			<label>Name</label>
			<input name="elections[3].candidates[1].name"/>
			<label>weiblich</label>
			<input name="elections[3].candidates[1].female"/>
		</section>
		<section>
			<input type="submit" value="Speichern"/>
		</section>
	</form>
</main>
</body>
</html>