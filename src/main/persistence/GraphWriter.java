// Originally from JsonSerializationDemo

package persistence;

import model.*;
import org.json.JSONObject;

import java.io.*;

// Represents a writer that writes JSON representation of workroom to file
public class GraphWriter {
    private static final int TAB = 4;
    private PrintWriter writer;
    private String destination;

    // EFFECTS: constructs writer to write to destination file
    public GraphWriter(String destination) {
        this.destination = destination;
    }

    // EFFECTS: constructs writer to write to destination file
    public GraphWriter(File file) throws IOException {
        this.destination = file.getCanonicalPath();
    }

    // MODIFIES: this
    // EFFECTS: opens writer; throws FileNotFoundException if destination file
    // cannot
    // be opened for writing
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(new File(destination));
    }

    // MODIFIES: the file whose path is destination
    // EFFECTS: writes JSON representation of workroom to file
    public void write(Graph g) {
        JSONObject json = g.toJson();
        saveToFile(json.toString(TAB));
    }

    // MODIFIES: this
    // EFFECTS: closes writer
    public void close() {
        writer.close();
    }

    // MODIFIES: this
    // EFFECTS: writes string to file
    private void saveToFile(String json) {
        writer.print(json);
    }
}
