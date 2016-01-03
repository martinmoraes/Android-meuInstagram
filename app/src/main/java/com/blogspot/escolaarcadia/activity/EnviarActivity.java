package com.blogspot.escolaarcadia.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
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
        UserPicture up = null;
        if (resultCode == RESULT_OK) {
            if (requestCode == FOTO_CAMERA) {
//                imagemBitmap = (Bitmap) intent.getExtras().get("data");
                up = new UserPicture(fotoURI, getContentResolver());
            } else if (requestCode == GALERIA_FOTO) {
                up = new UserPicture(intent.getData(), getContentResolver());
            }
            try {
                imagemView.setPadding(0, 0, 0, 0);
                imagemBitmap = up.getBitmap();
                imagemView.setImageBitmap(imagemBitmap);
            } catch (IOException e) {
                Log.e("GALERIA", "Falha ao carregar a imagem.", e);
            }
        } else {
            Toast.makeText(this, "Operação cancelada.", Toast.LENGTH_SHORT).show();
        }
    }


    public void enviaMenssagem(View view) {
        if (imagemBitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imagemBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            InputStream myInputStream = new ByteArrayInputStream(stream.toByteArray());
            RequestParams params = new RequestParams();
            params.setForceMultipartEntityContentType(true);
            params.put("arq", myInputStream, "image.png");

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
                            Toast.makeText(getApplication(), "Falha de comunicação!!!\n" + error.toString(), Toast.LENGTH_LONG).show();
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


}
