/*
 * Copyright © 2014 Benjamin Gehrels
 *
 * This file is part of The Single Transferable Vote Elections Web Interface.
 *
 * The Single Transferable Vote Elections Web Interface is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * The Single Transferable Vote Elections Web Interface is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with The Single Transferable Vote
 * Elections Web Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package info.gehrels.voting.web.resultCalculation;

public enum ElectionCalculationState {
	NOT_YET_STARTED("Die Ergebnisberechnung wurde noch nicht gestartet."),
	RUNNING("Die Wahlergebnisse werden momentan berechnet. Bitte laden Sie diese Seite in ein paar Sekunden neu."),
	MANUAL_AMBIGUITY_RESOLUTION_NECESSARY(
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
