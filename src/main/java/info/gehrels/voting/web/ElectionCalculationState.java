package info.gehrels.voting.web;

public enum ElectionCalculationState {
	NOT_YET_STARTED("Die Ergebnisberechnung wurde noch nicht gestartet."),
	RUNNING("Die Wahlergebnisse werden momentan berechnet. Bitte laden Sie diese Seite in ein paar Sekunden neu."),
	MANUAL_AMBIGUITY_RESOLUTION_NECCESSARY(
		"Mehrere Kandidierende haben die selbe Stimmzahl. Eine Manuelle Auswahl ist notwendig."),
	FINISHED("Die Berechnung der Wahlergebnisse wurde erfolgreich abgeschlossen."),
	AMBIGUITY_RESOLVED("Das Stimmenpatt wurde durch einen externen Eingriff aufgelöst, die Berechnung wird in Kürze fortgesetzt.");

	private final String description;

	ElectionCalculationState(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
