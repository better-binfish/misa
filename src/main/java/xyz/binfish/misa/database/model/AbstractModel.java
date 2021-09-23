package xyz.binfish.misa.database.model;

import javax.annotation.Nullable;
import java.util.Map;

import xyz.binfish.misa.database.collection.DataRow;

public abstract class AbstractModel {

	/*
	 * The main data object, used for retrieving all the data
	 * for the model.
	 */
	protected DataRow data;

	/*
	 * Determines if the model has data or not.
	 */
	protected boolean hasData;

	/*
	 * Determines if the model has been checked if it has any data or not.
	 */
	private boolean hasBeenChecked;

	/*
	 * Create a new model instance using
	 * the given data row object.
	 *
	 * @param data the data row object that should be used
	 *             for creating the model instance.
	 */
	public AbstractModel(DataRow data) {
		this.data = data;
		this.hasBeenChecked = false;
	}

	/*
	 * Get the raw data row object instance for the model, or the data has been
	 * reset since creating the model, the raw data may be NULL.
	 *
	 * @return the raw data row object instance, or NULL.
	 */
	@Nullable
	public DataRow getRawData() {
		return data;
	}

	/*
	 * Check if the model instance has data or not.
	 *
	 * @return true if the model has data, flase otherwise.
	 */
	public final boolean hasData() {
		if(!hasBeenChecked) {
			hasData = checkIfModelHasData();
			hasBeenChecked = true;
		}

		return hasData;
	}

	/*
	 * Check if the model has any data.
	 *
	 * @return true if the model has data, false otherwise.
	 */
	protected boolean checkIfModelHasData() {
		return data != null;
	}

	@Override
	public String toString() {
		if(data != null) {
			return data.toString();
		}

		return super.toString();
	}

	/*
	 * Reset the raw data row instance, setting it to NULL.
	 */
	protected void reset() {
		this.data = null;
	}

}
