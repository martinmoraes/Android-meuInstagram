package com.blogspot.escolaarcadia;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Created by Martin on 04/09/2015.
 */
public class Comunicacao {
    public static final String urlConsulta = "http://appviral.com.br/app/";
    public static final String urlEnviaPOST = urlConsulta + "inserir.php";
    public static final String urlExcluiPOST = urlConsulta + "excluir.php";
    public static final String urlListarPOST = urlConsulta + "listar.php";

    public static final String DIRECAO_ANTIGOS = "DIRECAO_ANTIGOS";
    public static final String DIRECAO_NOVOS = "DIRECAO_NOVOS";
    public static final String DIRECAO_ULTIMOS = "DIRECAO_ULTIMOS";
    
    
    /*
    Parâmetros de listar
      origem, emJSON
      id, idFilmeInicial
      qtde, Comunicacao.limiteElementos
      direcao, direcao

     Parâmetros de inserir
        arq, myInputStream, "image.png"
        titulo, editText.getText().toString()

     Parâmetros de excluir
        id, idPost

     */


    public static int ultimoPostGetView = 0;
    public static int limiteElementos = 12;
    public static int limiteParaRequestAntigos = limiteElementos; // Quando chegar no último fará uma chamada para os mais antigos
    public static int limiteParaRequestNovos = 0; // Quando chegar no primeiro fará uma chamada para os mais novos

    public static boolean novoPost = false;

    //novoPOST
    public static void setNovoPost() {
        Comunicacao.novoPost = true;
    }
    public static void setPostConsumido() {
        Comunicacao.novoPost = true;
    }
    public static boolean isNovoPost(){
        return novoPost;
    }






    /*
     BIBLIOTECA DE TERCEIROS
     AsyncHttpClient: https://github.com/AsyncHttpClient/async-http-client
     */


    public static boolean isConectado(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return  cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected() ||
                    cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
        } catch (Exception e) {
            Log.e("MEU_APP", "Comunicacao: ERRO ao verificar conectividade");
        }
        return false;
            /*	ConnectivityManager
    *.TYPE_MOBILE	0
    *.TYPE_WIFI	1
    *.TYPE_WIMAX	6
    *.TYPE_ETHERNET 9
    */
    }


}
