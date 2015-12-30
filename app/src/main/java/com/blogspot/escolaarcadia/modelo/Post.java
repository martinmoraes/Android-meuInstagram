package com.blogspot.escolaarcadia.modelo;

import java.util.ArrayList;

public class Post {
    private String id;
    private String texto;
    private String imagemUrl;

    public Post() {
    }

    public Post(String id, String texto, String imagemUrl) {
        this.id = id;
        this.texto = texto;
        this.imagemUrl = imagemUrl;
    }

    public String getId() {
        return id;
    }

    public int getId_INT() {
        return Integer.parseInt(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setId(int id) {
        this.id = String.valueOf(id);
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }
}
