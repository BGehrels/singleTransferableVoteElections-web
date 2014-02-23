package info.gehrels.voting.web;

public enum ElectionCalculationState {
	NOT_YET_STARTED("Die Ergebnisberechnung wurde noch nicht gestartet."),
	RUNNING("Die Wahlergebnisse werden momentan berechnet. Bitte laden Sie diese Seite in ein paar Sekunden neu."),
	MANUAL_AMBIGUITY_RESOLUTION_NECCESSARY(
		"Mehrere Kandidierende haben die selbe Stimmzahl. Eine Manuelle Auswahl ist notwendig."),
	FINISHED("Die Berechnung der Wahlergebnisse wurde erfolgreich abgeschlossen.");

	private final String description;

	ElectionCalculationState(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
