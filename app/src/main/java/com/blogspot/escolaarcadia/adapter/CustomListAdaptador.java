package com.blogspot.escolaarcadia.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.blogspot.escolaarcadia.Comunicacao;
import br.com.escolaarcadia.meusfilmes.R;
import cz.msebera.android.httpclient.Header;

import com.blogspot.escolaarcadia.modelo.Filme;
import com.blogspot.escolaarcadia.modelo.Genero;

/**
 * Created by Martin on 25/08/2015.
 */
public class CustomListAdaptador extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Filme> filmeItens;
    private boolean emRequisicao = false;
    private boolean povoaLocal = false;

    public CustomListAdaptador(Context context) {
        this.context = context;
        filmeItens = new ArrayList<Filme>();
    }

    @Override
    public int getCount() {
        return filmeItens.size();
    }

    @Override
    public Object getItem(int location) {
        return filmeItens.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("MEU_APP", "getView - position: " + position);

        if (inflater == null)
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.linha, null);
        TextView tvTitulo = (TextView) convertView.findViewById(R.id.titulo);
        TextView tvId = (TextView) convertView.findViewById(R.id.id);
        TextView tvNota = (TextView) convertView.findViewById(R.id.nota);
        TextView tvGenero = (TextView) convertView.findViewById(R.id.genero);
        TextView tvAno = (TextView) convertView.findViewById(R.id.ano);
        TextView imagemURL = (TextView) convertView.findViewById(R.id.imagemURL);
        ImageView iv = (ImageView) convertView.findViewById(R.id.imagem);

        // Pega o filme para uma linha
        Filme filme = filmeItens.get(position);


        // Imagem
        if (povoaLocal) {
            Context context = convertView.getContext();
            int img = context.getResources().getIdentifier(filme.getImagemUrl(), "mipmap", context.getPackageName());
            iv.setImageResource(img);
            Log.d("MEU_APP", "img: "+ img);
            imagemURL.setText("MEU APP LOCAL:" + img);
        } else {
            String img = Comunicacao.urlConsulta + filme.getImagemUrl();
            imagemURL.setText(img);

            Picasso.with(context)
                    .load(img)
                    .error(R.mipmap.foto)
                    .into(iv);
        }

        // ID
        tvId.setText(filme.getId());

        // Titulo
        tvTitulo.setText(filme.getTitulo());

        // Nota
        tvNota.setText("Nota: " + filme.getNotaSTR());

        // Genero
        tvGenero.setText(filme.getGeneroSTR());

        // Ano
        tvAno.setText(filme.getAnoSTR());

        //
        //Verifica se precisa fazer nova requisição
        //
      /*  if (Comunicacao.limiteParaRequestAntigos == position) {
            this.fazRequisicao(filme.getId(), Comunicacao.DIRECAO_ANTIGOS);
        }
        if (Comunicacao.limiteParaRequestNovos == position) {
            this.fazRequisicao(filme.getId(), Comunicacao.DIRECAO_NOVOS);
        }*/
        return convertView;
    }

    public void fazRequisicao(String idFilmeInicial, final String direcao) {
        if (Comunicacao.isConectado(context)) {
            if (!emRequisicao) {
                this.povoaLocal = false;
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams param = new RequestParams();
                param.put("origem", "emJSON");
                param.put("id", idFilmeInicial);
                param.put("qtde", Comunicacao.limiteElementos);
                param.put("direcao", direcao);
                ProgressDialog progres = ProgressDialog.show(context, "Carregando dados", "Aguarde...");
                client.get(Comunicacao.urlListarPOST,
                        param,
                        new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                                Gson gson = new GsonBuilder().create();
                                int qtdeReg = response.length();
                                if (qtdeReg > 0) {
                                    ArrayList<Filme> novaLista = new ArrayList<Filme>();
                                    for (int i = 0; i < qtdeReg; i++) {
                                        try {
                                            String obj = response.getJSONObject(i).toString();
                                            Filme umFilme = gson.fromJson(obj, Filme.class);
                                            novaLista.add(umFilme);
                                        } catch (JSONException e) {
                                            Log.e("MEU_APP", "onSuccess - JSONException: " + e.toString());
                                        }
                                    }
                                    if (Comunicacao.DIRECAO_ANTIGOS.equals(direcao)) {
                                        filmeItens.addAll(novaLista);
                                    } else if (Comunicacao.DIRECAO_NOVOS.equals(direcao)) {
                                        filmeItens.addAll(0, novaLista);
                                    } else if (Comunicacao.DIRECAO_ULTIMOS.equals(direcao)) {
                                        filmeItens = novaLista;
                                    }
                                    notifyDataSetChanged();
                                } else {
                                    Toast.makeText(context, "Nenhum registro encontrado", Toast.LENGTH_LONG).show();
                                }
                                emRequisicao = false;
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                Log.e("MEU_APP", "onFailure JSONArray: " + statusCode, throwable);
                                emRequisicao = false;
                                povoaLocal();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                Log.e("MEU_APP", "onFailure STRING: " + statusCode, throwable);
                                emRequisicao = false;
                                povoaLocal();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Log.e("MEU_APP", "onFailure JSONObject: " + statusCode, throwable);
                                emRequisicao = false;
                                povoaLocal();
                            }


                        });
                emRequisicao = true;
                progres.dismiss();
            }
        } else {
            this.povoaLocal();
        }
    }

    public void excluiPost(String idFilme) {
        for (Filme oFilme : filmeItens) {
            if (oFilme.getId().equals(idFilme)) {
                filmeItens.remove(oFilme);
                this.notifyDataSetChanged();
                break;
            }
        }
    }

    private final void povoaLocal() {
        this.povoaLocal = true;
        ProgressDialog progres = ProgressDialog.show(context, "Carregando dados", "Abrindo localmente...");
        Toast.makeText(context, "Não alcançou o servidor mostrando versão exemplo.", Toast.LENGTH_LONG).show();

        ArrayList<Genero> genero = new ArrayList<Genero>();
        genero.add(new Genero("MEU APP LOCAL"));
        filmeItens.add(new Filme("1", "camera", "camera", 2015, 8.5, genero));
        filmeItens.add(new Filme("2", "enviarbranco", "enviarbranco", 2015, 8.5, genero));
        filmeItens.add(new Filme("3", "foto", "foto", 2015, 8.5, genero));
        filmeItens.add(new Filme("4", "fotobranco", "fotobranco", 2015, 8.5, genero));
        filmeItens.add(new Filme("5", "galeriabranco", "galeriabranco", 2015, 8.5, genero));
        filmeItens.add(new Filme("6", "ico", "ico", 2015, 8.5, genero));
        filmeItens.add(new Filme("7", "filmeItens", "lista", 2015, 8.5, genero));
        filmeItens.add(new Filme("8", "perfil", "perfil", 2015, 8.5, genero));
        progres.dismiss();
        Log.d("MEU_APP", "povoaLocal() - Povoada a filmeItens local");
        notifyDataSetChanged();
    }
}













/*
          /*RoundedImageView riv = new RoundedImageView(context);
            riv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            riv.setCornerRadius((float) 10);
            riv.setBorderWidth((float) 2);
            riv.setBorderColor(Color.DKGRAY);
            riv.mutateBackground(true);
            riv.setImageDrawable(drawable);
            riv.setBackground(backgroundDrawable);
            riv.setOval(true);
            riv.setTileModeX(Shader.TileMode.REPEAT);
            riv.setTileModeY(Shader.TileMode.REPEAT);
*/
//            Transformation transformation = new RoundedTransformationBuilder()
//                    .borderColor(Color.BLACK)
//                    .borderWidthDp(3)
//                    .cornerRadiusDp(30)
//                    .oval(false)
//                    .build();
//
//            Picasso.with(context)
//                    .load(url)
//                    .fit()
//                    .transform(transformation)
//                    .into(imageView);

            /*

            BIBLIOTECA DE TERCEIROS
            RoundedImageView: https://github.com/vinc3m1/RoundedImageView
            Picasso: https://github.com/square/picasso



class RoundedTransformation implements com.squareup.picasso.Transformation {

    private final int radius;
    private final int margin;  // dp

    // radius is corner radii in dp
    // margin is the board in dp
    public RoundedTransformation(final int radius, final int margin) {
        this.radius = radius;
        this.margin = margin;
    }

    @Override
    public Bitmap transform(final Bitmap source) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawRoundRect(new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin), radius, radius, paint);

        if (source != output) {
            source.recycle();
        }

        return output;
    }

    @Override
    public String key() {
        return "rounded";
    }

    */
