package data_import.pit.merged;

import java.beans.XMLDecoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import directories.globals.Directories;
import pit.data_objects.PitMethod;

public class PitMergedMutationsReader {

	/**	
	 * Reads all objects and returns a List of PitMethods.
	 * The PitTestCase and PitMutation objects are referenced by the methods.
	 */
	public static List<PitMethod> readPitMergedMethods(String dir) throws IOException{
		String outputFile = dir + Directories.MERGED_METHODS_FILE_NAME;
		FileInputStream fis = new FileInputStream(new File(outputFile));
		XMLDecoder decoder = new XMLDecoder(fis);
		@SuppressWarnings("unchecked")
		List<PitMethod> methods = (List<PitMethod>) decoder.readObject();
		decoder.close();
		fis.close();
		return methods;
	}
}
