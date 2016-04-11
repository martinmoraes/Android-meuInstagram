package com.blogspot.escolaarcadia.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.blogspot.escolaarcadia.Comunicacao;
import com.blogspot.escolaarcadia.UserPicture;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import br.com.escolaarcadia.meusfilmes.R;
import cz.msebera.android.httpclient.Header;


public class EnviarActivity extends Activity {
    private ImageView imagemView;
    private Bitmap imagemBitmap;
    private EditText editText;
    private Uri fotoURI = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityenviar);
        imagemView = (ImageView) findViewById(R.id.imageView);
        editText = (EditText) findViewById(R.id.editText);
    }

    private final int FOTO_CAMERA = 1;

    public void bateFoto(View view) {
        String fotoArquivo = UUID.randomUUID().toString() + ".jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fotoArquivo);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fotoArquivo);
        values.put(MediaStore.Images.Media.DESCRIPTION, "Uma Foto");
        //values.put(MediaStore.Images.Media.ORIENTATION,0); //degrees 0, 90, 180, 270
        fotoURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent fotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fotoURI);
        startActivityForResult(fotoIntent, FOTO_CAMERA);
    }

    private final int GALERIA_FOTO = 2;

    public void abreGaleria(View view) {
        Intent imagemIntent = new Intent();
        imagemIntent.setType("image/*");
        imagemIntent.setAction(Intent.ACTION_PICK);
        // Intent.ACTION_PICK - Só gerenciadores de imagens
        // Intent.ACTION_GET_CONTENT - Gerenciadores de imagens e de arquivos
        // imagemIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Para ter mais de uma imagem no retorno
        startActivityForResult(Intent.createChooser(imagemIntent, "Selecione..."), GALERIA_FOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            if (requestCode == FOTO_CAMERA) {
//                imagemBitmap = (Bitmap) intent.getExtras().get("data");
//                up = new UserPicture(fotoURI, getContentResolver());
            } else if (requestCode == GALERIA_FOTO) {
                fotoURI = intent.getData();
            }
           mostraImagem();
        } else {
            Toast.makeText(this, "Operação cancelada.", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostraImagem(){
        imagemView.setPadding(0, 0, 0, 0);
        UserPicture up = new UserPicture(fotoURI, getContentResolver());
        try {
            imagemBitmap = up.getBitmap();
        } catch (IOException e) {
            Log.e("MEU_APP", "Falha ao carregar a imagem.", e);
        }
        imagemView.setImageBitmap(imagemBitmap);
    }

    public void enviaMenssagem(View view) {
        if (imagemBitmap != null) {
//            imagemBitmap = getResizedBitmap(imagemBitmap, 100, 100);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imagemBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            InputStream myInputStream = new ByteArrayInputStream(stream.toByteArray());
            RequestParams params = new RequestParams();
            params.setForceMultipartEntityContentType(true);
            params.put("arq", myInputStream, "image.png");
            params.put("origem", "emJSON");

            //params.put("arq", myInputStream);
            params.put("titulo", editText.getText().toString());

            AsyncHttpClient cliente = new AsyncHttpClient();
            cliente.post(
                    Comunicacao.urlEnviaPOST,
                    params,
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            Comunicacao.setNovoPost();
                            Toast.makeText(getApplication(), "Postado", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Log.i("MEU_APP", "statusCode: " + statusCode + "\n error: " + error.toString());
                            Toast.makeText(getApplication(), "Falha de comunicação!!! " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
            Toast.makeText(this, "Enviando.", Toast.LENGTH_SHORT).show();
            imagemBitmap = null;
            editText.setText("");
            imagemView.setPadding(220, 220, 220, 220);
            imagemView.setImageResource(R.mipmap.fotobranco);

        } else {
            Toast.makeText(this, "Selecione uma image.", Toast.LENGTH_SHORT).show();
        }
    }


    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // RECREATE THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(fotoURI != null) {
            Log.d("MEU_APP", "PASSOU em onSaveInstanceState!!!" + fotoURI.toString());
            outState.putString("fotoURI", fotoURI.toString());
        }else {
            Log.d("MEU_APP", "PASSOU em onSaveInstanceState sem URI!!!");
            outState.putString("fotoURI", "null");
        }



        // salvando o texto antes da orientação ocorrer
//        String texto = campoDeTexto.getText().toString();
//        outState.putString("texto", texto);

    }

    @Override
    public void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        String foto_uri = inState.getString("fotoURI");
        if(!foto_uri.equals("null")) {
            fotoURI = Uri.parse(foto_uri);
            mostraImagem();
            Log.d("MEU_APP", "PASSOU em onRestoreInstanceState!!! " + fotoURI.toString());
        }else{
            Log.d("MEU_APP", "PASSOU em onRestoreInstanceState sem URI!!! ");
        }
//         texto recuperado durante a transição de orientação de tela
//        String texto = inState.getString("texto");
//        campoDeTexto.setText(texto);

    }
}
