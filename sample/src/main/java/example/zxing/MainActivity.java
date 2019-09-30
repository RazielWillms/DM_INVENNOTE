package example.zxing;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Camera;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mikepenz.aboutlibraries.LibsBuilder;


public class MainActivity extends Template {

    public final int CUSTOMIZED_REQUEST_CODE = 0x0000ffff;
    private SQLiteDatabase b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b = this.openOrCreateDatabase("AMOX", Context.MODE_PRIVATE, null);
        b.execSQL("CREATE table if not exists  objeto (id_objeto INTEGER PRIMARY KEY AUTOINCREMENT, nome varchar(60), categoria varchar(60), cod varchar(60), local varchar(60))");

    }

    public void cadastrarcnec(View view) {
        setContentView(R.layout.activity_cadastrar_objeto);

        TextView textView = findViewById(R.id.local);
        textView.setText("CNEC");
        carregarTabela();
    }

    public void cadastraruri(View view) {
        setContentView(R.layout.activity_cadastrar_objeto);

        TextView textView = findViewById(R.id.local);
        textView.setText("URI");
        carregarTabela();
    }

    public void cadastrarfasa(View view) {
        setContentView(R.layout.activity_cadastrar_objeto);

        TextView textView = findViewById(R.id.local);
        textView.setText("FASA");
        carregarTabela();
    }

    public void listar(View view) {
        setContentView(R.layout.activity_listar_objeto);
        carregarTabelacompleta();
    }
    public void local(View view) {
        setContentView(R.layout.activity_main);
    }

    public void Adicionar(View v) {
        EditText et = findViewById(R.id.nome);
        EditText cat = findViewById(R.id.categoria);
        TextView cod = findViewById(R.id.codebar);
        TextView loc = findViewById(R.id.local);

        b.execSQL("INSERT INTO objeto(nome, categoria, cod, local) values('" + et.getText().toString() + "','"+ cat.getText().toString() + "','" + cod.getText().toString() + "','" + loc.getText().toString() +"')");

        Toast.makeText(this, "SALVO: " + et.getText().toString(), Toast.LENGTH_LONG).show();

        carregarTabela();
    }

    public void carregarTabela() {
        Cursor c = b.rawQuery("SELECT id_objeto, nome, categoria, cod FROM objeto;", new String[]{});

        LinearLayout linearLayout = findViewById(R.id.resultado);
        linearLayout.removeAllViews();
        while (c.moveToNext()) {

            TextView textView = new TextView(this);
            textView.setText("Nome: " + c.getString(1) + " | " + "Código: "+ c.getString(3));
            linearLayout.addView(textView);
        }
        c.close();
    }

    public void carregarTabelacompleta() {
        Cursor c = b.rawQuery("SELECT id_objeto, nome, categoria, cod, local FROM objeto;", new String[]{});

        LinearLayout linearLayout = findViewById(R.id.result);
        linearLayout.removeAllViews();
        while (c.moveToNext()) {

            TextView textView = new TextView(this);
            textView.setText("Nome: " + c.getString(1) + " | " + "Categoria: "+ c.getString(2) + " | " + "Código: "+ c.getString(3)+ " | " + "Local: "+ c.getString(4));
            linearLayout.addView(textView);
        }
        c.close();
    }

    public void scanBarcode(View view) {
        new IntentIntegrator(this).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != CUSTOMIZED_REQUEST_CODE && requestCode != IntentIntegrator.REQUEST_CODE) {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        switch (requestCode) {
            case CUSTOMIZED_REQUEST_CODE: {
                Toast.makeText(this, "REQUEST_CODE = " + requestCode, Toast.LENGTH_LONG).show();
                break;
            }
            default:
                break;
        }

        IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);

        if(result.getContents() == null) {
            Log.d("MainActivity", "Cancelled scan");
            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
        } else {
            Log.d("MainActivity", "Scanned");
            Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            TextView textView = findViewById(R.id.codebar);
            textView.setText(result.getContents());
        }
    }

    /**
     * Sample of scanning from a Fragment
     */
    public static class ScanFragment extends Fragment {
        private String toast;

        public ScanFragment() {
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            displayToast();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_scan, container, false);
            Button scan = (Button) view.findViewById(R.id.scan_from_fragment);
            scan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scanFromFragment();
                }
            });
            return view;
        }

        public void scanFromFragment() {
            IntentIntegrator.forSupportFragment(this).initiateScan();
        }

        private void displayToast() {
            if(getActivity() != null && toast != null) {
                Toast.makeText(getActivity(), toast, Toast.LENGTH_LONG).show();
                toast = null;
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(result != null) {
                if(result.getContents() == null) {
                    toast = "Cancelled from fragment";
                } else {
                    toast = "Scanned from fragment: " + result.getContents();
                }

                // At this point we may or may not have a reference to the activity
                displayToast();
            }
        }
    }
}
