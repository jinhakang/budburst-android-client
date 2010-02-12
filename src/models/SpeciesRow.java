package models;

import java.util.ArrayList;
import java.util.HashMap;

import edu.ucla.cens.budburst.Budburst;
import edu.ucla.cens.budburst.data.Row;

public class SpeciesRow extends Row {

	public String species_name;
	public String common_name;
	private HashMap<String, ArrayList<Row>> phenophases = new HashMap<String, ArrayList<Row>>();

	public ArrayList<Row> phenophases(String type) {
		if (!phenophases.containsKey(type)) {
			phenophases.put(type, hasMany("phenophase", "type='" + type + "'"));
		}

		return phenophases.get(type);
	}

	public String getImagePath() {
		return Budburst.SPECIES_PATH + _id + ".jpg";
	}

}