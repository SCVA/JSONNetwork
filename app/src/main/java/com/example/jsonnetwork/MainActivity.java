package com.example.jsonnetwork;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.jsonnetwork.Datos.Postres;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayAdapter<String> adaptadorPostres;
    ListView listaPostres;
    ArrayList<Postres> listaPostresJSON = new ArrayList<Postres>();
    ArrayList<String> listaPostresJSONTexto = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        listaPostres = (ListView) findViewById( R.id.listaPostres );
        leerJson();
    }

    @SuppressLint("StaticFieldLeak")
    private void leerJson() {
        new AsyncTask<Void, Void, ArrayList<String>>(){
            @Override
            protected ArrayList<String> doInBackground(Void[] params) {
                HttpURLConnection con = null;
                try {
                    // Establecer la conexión
                    URL url = new URL("https://nubecolectiva.com/blog/tutos/demos/leer_json_android_java/datos/postres.json");
                    con = (HttpURLConnection)url.openConnection();
                    con.setConnectTimeout(15000);
                    con.setReadTimeout(10000);
                    // Obtener el estado del recurso
                    int statusCode = con.getResponseCode();
                    if(statusCode!=200) {
                        listaPostresJSON.add(new Postres("Error",null,null,
                                null, null, null, null));
                        listaPostresJSONTexto.add (listaPostresJSON.get( listaPostresJSON.size()-1).getNombre());
                    }
                    else{
                        // Parsear el flujo con formato JSON
                        InputStream in = new BufferedInputStream(con.getInputStream());
                        GsonPostresParser parser = new GsonPostresParser();
                        listaPostresJSON = parser.readJsonStream(in);
                        for(int i = 0; i<listaPostresJSON.size(); i++){
                            listaPostresJSONTexto.add (listaPostresJSON.get( i ).getNombre());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    con.disconnect();
                    return listaPostresJSONTexto;
                }
            }

            protected void onPostExecute(ArrayList<String> resultado) {
                crearAdaptador( resultado );
            }

        }.execute();
    }

    public void crearAdaptador(ArrayList<String> resultado){
        adaptadorPostres = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                resultado
        );
        listaPostres.setAdapter( adaptadorPostres );
    }

    public class GsonPostresParser {
        public ArrayList readJsonStream(InputStream in) throws IOException {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
            ArrayList<Postres> postres = new ArrayList<Postres>();

            reader.beginObject(); //  Comenzar a resolver un nuevo objeto
            reader.skipValue();
            reader.skipValue();
            reader.skipValue();
            reader.skipValue();
            while (reader.hasNext()) {
                reader.nextName();
                reader.beginArray();
                while (reader.hasNext()) {
                    Postres postrecito = gson.fromJson(reader, Postres.class);
                    postres.add(postrecito);
                }
                reader.endArray();
            }
            reader.endObject();
            reader.close();
            return postres;
        }
    }
}