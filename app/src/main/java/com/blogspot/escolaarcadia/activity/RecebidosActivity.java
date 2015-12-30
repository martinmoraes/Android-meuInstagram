package com.blogspot.escolaarcadia.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import com.blogspot.escolaarcadia.Comunicacao;
import com.blogspot.escolaarcadia.adapter.CustomListAdaptador;
import br.com.escolaarcadia.meusfilmes.R;
import cz.msebera.android.httpclient.Header;

public class RecebidosActivity extends Activity {
    private static CustomListAdaptador adaptador;
    private Context context = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityrecebidos);


        ListView listView = (ListView) findViewById(R.id.lv_lista_de_filmes);
        context = listView.getContext();
        adaptador = new CustomListAdaptador(this);
        listView.setAdapter(adaptador);
        adaptador.fazRequisicao("0", Comunicacao.DIRECAO_ULTIMOS);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                String titulo = ((TextView) view.findViewById(R.id.titulo)).getText().toString();
                final String idFilme = ((TextView) view.findViewById(R.id.id)).getText().toString();

                AlertDialog.Builder alertaBuilder = new AlertDialog.Builder(context);
                alertaBuilder.setTitle("Faça sua Escolha");
                alertaBuilder.setMessage(titulo);

                alertaBuilder.setPositiveButton("Excluir",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {

                                if (Comunicacao.isConectado(context)) {
                                    AsyncHttpClient client = new AsyncHttpClient();
                                    RequestParams param = new RequestParams();
                                    param.put("id", idFilme);
                                    client.get(Comunicacao.urlExcluiPOST,
                                            param,
                                            new AsyncHttpResponseHandler() {
                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                    Toast.makeText(context, (new String(responseBody)), Toast.LENGTH_LONG).show();
                                                    view.animate().setDuration(1500).alpha(0)
                                                            .withEndAction(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    view.setAlpha(1);
                                                                    adaptador.excluiPost(idFilme);
                                                                }
                                                            });
                                                }

                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                    Toast.makeText(context, "Falha de comunicação!!!", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(context, "Sem conexão externa", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                alertaBuilder.setNegativeButton("ABRIR",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                String msg =
                                        ((TextView) view.findViewById(R.id.titulo)).getText().toString() + " Nota:" +
                                        ((TextView) view.findViewById(R.id.nota)).getText().toString() + "\n Genero:" +
                                        ((TextView) view.findViewById(R.id.genero)).getText().toString() + " Ano:" +
                                        ((TextView) view.findViewById(R.id.ano)).getText().toString();
                                String url = (((TextView) view.findViewById(R.id.imagemURL)).getText().toString());
                                Intent intent = new Intent(context, MostraPostActivity.class);
                                intent.putExtra("MSG", msg);
                                intent.putExtra("URL", url);
                                startActivity(intent);
                            }
                        });

                alertaBuilder.setNeutralButton("CANCELAR",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                Log.d("MEU_APP", "Entoru no onClick do setNeutralButton do RecebidosActivity - CANCELAR");
                                Toast.makeText(context, "Operação cancelada", Toast.LENGTH_SHORT).show();
                            }
                        });

                AlertDialog alertDialog = alertaBuilder.create();
                alertDialog.show();
            }
        });

    }


    @Override
    public void onResume(){
        super.onResume();
        adaptador.fazRequisicao("0", Comunicacao.DIRECAO_ULTIMOS);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}