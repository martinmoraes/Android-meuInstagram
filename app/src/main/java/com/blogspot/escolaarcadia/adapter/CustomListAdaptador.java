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

import com.blogspot.escolaarcadia.modelo.Post;
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


/**
 * Created by Martin on 25/08/2015.
 */
public class CustomListAdaptador extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Post> postItens;
    private boolean emRequisicao = false;
    private boolean povoaLocal = false;

    public CustomListAdaptador(Context context) {
        this.context = context;
        postItens = new ArrayList<Post>();
    }

    @Override
    public int getCount() {
        return postItens.size();
    }

    @Override
    public Object getItem(int location) {
        return postItens.get(location);
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
        TextView tvId = (TextView) convertView.findViewById(R.id.id);
        TextView tvTexto = (TextView) convertView.findViewById(R.id.textView);
        TextView tvImagemURL = (TextView) convertView.findViewById(R.id.imagemURL);
        ImageView iv = (ImageView) convertView.findViewById(R.id.imageView);

        // Pega o post para uma linha
        Post post = postItens.get(position);


        // Imagem
        if (povoaLocal) {
            Context context = convertView.getContext();
            int img = context.getResources().getIdentifier(post.getImagemUrl(), "mipmap", context.getPackageName());
            iv.setImageResource(img);
            Log.d("MEU_APP", "img: "+ img);
        } else {
            String img = Comunicacao.urlConsulta + post.getImagemUrl();
            Picasso.with(context)
                    .load(img)
                    .error(R.mipmap.foto)
                    .into(iv);
        }

        // ID
        tvId.setText(post.getId());

        // Texto
        tvTexto.setText(post.getTexto());

        // imagemURL
        tvImagemURL.setText(post.getImagemUrl());


        //
        //Verifica se precisa fazer nova requisição
        //
      /*  if (Comunicacao.limiteParaRequestAntigos == position) {
            this.fazRequisicao(post.getId(), Comunicacao.DIRECAO_ANTIGOS);
        }
        if (Comunicacao.limiteParaRequestNovos == position) {
            this.fazRequisicao(post.getId(), Comunicacao.DIRECAO_NOVOS);
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
                                    ArrayList<Post> novaLista = new ArrayList<Post>();
                                    for (int i = 0; i < qtdeReg; i++) {
                                        try {
                                            String obj = response.getJSONObject(i).toString();
                                            Post umPost = gson.fromJson(obj, Post.class);
                                            novaLista.add(umPost);
                                        } catch (JSONException e) {
                                            Log.e("MEU_APP", "onSuccess - JSONException: " + e.toString());
                                        }
                                    }
                                    if (Comunicacao.DIRECAO_ANTIGOS.equals(direcao)) {
                                        postItens.addAll(novaLista);
                                    } else if (Comunicacao.DIRECAO_NOVOS.equals(direcao)) {
                                        postItens.addAll(0, novaLista);
                                    } else if (Comunicacao.DIRECAO_ULTIMOS.equals(direcao)) {
                                        postItens = novaLista;
                                    }
                                    notifyDataSetChanged();
                                } else {
                                    Toast.makeText(context, "Nenhum registro encontrado", Toast.LENGTH_SHORT).show();
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
        for (Post oPost : postItens) {
            if (oPost.getId().equals(idFilme)) {
                postItens.remove(oPost);
                this.notifyDataSetChanged();
                break;
            }
        }
    }

    private final void povoaLocal() {
        this.povoaLocal = true;
        ProgressDialog progres = ProgressDialog.show(context, "Carregando dados", "Abrindo localmente...");
        Toast.makeText(context, "Não alcançou o servidor mostrando versão exemplo.", Toast.LENGTH_LONG).show();

        postItens.add(new Post("1", "camera", "camera"));
        postItens.add(new Post("2", "enviarbranco", "enviarbranco"));
        postItens.add(new Post("3", "foto", "foto"));
        postItens.add(new Post("4", "fotobranco", "fotobranco"));
        postItens.add(new Post("5", "galeriabranco", "galeriabranco"));
        postItens.add(new Post("6", "ico", "ico"));
        postItens.add(new Post("7", "postItens", "lista"));
        postItens.add(new Post("8", "perfil", "perfil"));
        progres.dismiss();
        Log.d("MEU_APP", "povoaLocal() - Povoada a postItens local");
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
