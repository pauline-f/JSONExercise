package com.example.pauline.jsonexercise;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.view.View;
import android.widget.TextView;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Color> colors;
    TextView textView;

    final String jsonData = "{\n" +
            "  \"colors\": [\n" +
            "    {\n" +
            "      \"color\": \"black\",\n" +
            "      \"category\": \"hue\",\n" +
            "      \"type\": \"primary\",\n" +
            "      \"code\": {\n" +
            "        \"rgba\": [255,255,255,1],\n" +
            "        \"hex\": \"#000\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"color\": \"white\",\n" +
            "      \"category\": \"value\",\n" +
            "      \"code\": {\n" +
            "        \"rgba\": [0,0,0,1],\n" +
            "        \"hex\": \"#FFF\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"color\": \"red\",\n" +
            "      \"category\": \"hue\",\n" +
            "      \"type\": \"primary\",\n" +
            "      \"code\": {\n" +
            "        \"rgba\": [255,0,0,1],\n" +
            "        \"hex\": \"#FF0\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"color\": \"blue\",\n" +
            "      \"category\": \"hue\",\n" +
            "      \"type\": \"primary\",\n" +
            "      \"code\": {\n" +
            "        \"rgba\": [0,0,255,1],\n" +
            "        \"hex\": \"#00F\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"color\": \"yellow\",\n" +
            "      \"category\": \"hue\",\n" +
            "      \"type\": \"primary\",\n" +
            "      \"code\": {\n" +
            "        \"rgba\": [255,255,0,1],\n" +
            "        \"hex\": \"#FF0\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"color\": \"green\",\n" +
            "      \"category\": \"hue\",\n" +
            "      \"type\": \"secondary\",\n" +
            "      \"code\": {\n" +
            "        \"rgba\": [0,255,0,1],\n" +
            "        \"hex\": \"#0F0\"\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());
    }


    public void readJSON(View view) {
        try {
            colors = parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected List<Color> parse() throws IOException {
        JsonReader reader = new JsonReader(new StringReader(jsonData));

        colors = new ArrayList<>();

        reader.beginObject();
        if (reader.hasNext() && reader.nextName().equals("colors")) {
            reader.beginArray();
            while (reader.hasNext()) {
                colors.add(readColor(reader));
            }
            reader.endArray();
        }
        reader.endObject();
        reader.close();
        return colors;
    }

    private Color readColor(JsonReader reader) throws IOException {
        Color color = new Color();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("color")) {
                color.colorName = reader.nextString();
            } else if (name.equals("category")) {
                color.category = reader.nextString();
            } else if (name.equals("type")) {
                color.type = reader.nextString();
            } else if (name.equals("code")) {
                color.code = readCode(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return color;
    }

    private int[] readIntegerArray(JsonReader reader) throws IOException {
        List<Integer> rgbaValue = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            rgbaValue.add(reader.nextInt());
        }
        reader.endArray();
        int[] rgba = new int[rgbaValue.size()];
        for (int i=0; i<rgbaValue.size(); i++) {
            rgba[i] = rgbaValue.get(i);
        }
        return rgba;
    }

    private Code readCode(JsonReader reader) throws IOException {
        Code code = new Code();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("rgba")) {
                code.rgba = readIntegerArray(reader);
            } else if (name.equals("hex")) {
                code.hex = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return code;
    }

    public void addColor() {
        try {
            List<Color> colors = parse();

            Color color = new Color();
            color.colorName = "orange";
            color.category = "hue";

            Code code = new Code();
            code.rgba = new int[]{255,165,0,1};
            code.hex = "#FA0";

            color.code = code;
            colors.add(color);

            StringWriter stringWriter = new StringWriter();
            JsonWriter writer = new JsonWriter(stringWriter);
            writer.setIndent("  ");
            writeColorsArray(writer, colors);
            writer.close();

            textView.setText(stringWriter.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeColorsArray(JsonWriter writer, List<Color> colors) throws IOException {
        writer.beginArray();
        for (Color color : colors) {
            writeColor(writer, color);
        }
        writer.endArray();
    }

    private void writeColor(JsonWriter writer, Color color) throws IOException {
        writer.beginObject();
        writer.name("color").value(color.colorName);
        writer.name("category").value(color.category);
        if (color.type != null) {
            writer.name("type").value(color.type);
        }
        writer.name("code");
        writeCode(writer, color.code);
        writer.endObject();
    }

    private void writeCode(JsonWriter writer, Code code) throws IOException {
        writer.beginObject();
        writer.name("rgba");
        writeIntegerArray(writer, code.rgba);
        writer.name("hex").value(code.hex);
        writer.endObject();
    }

    private void writeIntegerArray(JsonWriter writer, int[] rgba) throws IOException {
        writer.beginArray();
        for (int value : rgba) {
            writer.value(value);
        }
        writer.endArray();
    }

    public void count(View view) {
        readJSON(view);
        int nb = 0;

        for (Color color: colors) {
            if (color.code.rgba[1] == 255) {
                nb++;
            }
        }
        textView.setText(String.valueOf(nb));
    }

    public void list(View view) {
        readJSON(view);
        StringBuilder green = new StringBuilder();

        for (Color color: colors) {
            if (color.code.rgba[1] == 255) {
                green.append(color.colorName).append(" ");
            }
        }
        textView.setText(green.toString());
    }

    public void modify(View view) {
        addColor();
    }
}
