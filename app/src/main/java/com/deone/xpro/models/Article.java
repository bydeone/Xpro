package com.deone.xpro.models;

public class Article {
    private String aId;
    private String aDate;
    private String aCategorie;
    private String aCover;
    private String aTitre;
    private String aDescription;
    private String anVues;
    private String anLikes;
    private String aUid;
    private String aUnom;
    private String aUphoto;

    public Article() {
    }

    public Article(String aId, String aDate,
                   String aCategorie, String aCover,
                   String aTitre, String aDescription,
                   String anVues, String anLikes,
                   String aUid, String aUnom, String aUphoto) {
        this.aId = aId;
        this.aDate = aDate;
        this.aCategorie = aCategorie;
        this.aCover = aCover;
        this.aTitre = aTitre;
        this.aDescription = aDescription;
        this.anVues = anVues;
        this.anLikes = anLikes;
        this.aUid = aUid;
        this.aUnom = aUnom;
        this.aUphoto = aUphoto;
    }

    public String getaId() {
        return aId;
    }

    public void setaId(String aId) {
        this.aId = aId;
    }

    public String getaDate() {
        return aDate;
    }

    public void setaDate(String aDate) {
        this.aDate = aDate;
    }

    public String getaCategorie() {
        return aCategorie;
    }

    public void setaCategorie(String aCategorie) {
        this.aCategorie = aCategorie;
    }

    public String getaCover() {
        return aCover;
    }

    public void setaCover(String aCover) {
        this.aCover = aCover;
    }

    public String getaTitre() {
        return aTitre;
    }

    public void setaTitre(String aTitre) {
        this.aTitre = aTitre;
    }

    public String getaDescription() {
        return aDescription;
    }

    public void setaDescription(String aDescription) {
        this.aDescription = aDescription;
    }

    public String getAnVues() {
        return anVues;
    }

    public void setAnVues(String anVues) {
        this.anVues = anVues;
    }

    public String getAnLikes() {
        return anLikes;
    }

    public void setAnLikes(String anLikes) {
        this.anLikes = anLikes;
    }

    public String getaUid() {
        return aUid;
    }

    public void setaUid(String aUid) {
        this.aUid = aUid;
    }

    public String getaUnom() {
        return aUnom;
    }

    public void setaUnom(String aUnom) {
        this.aUnom = aUnom;
    }

    public String getaUphoto() {
        return aUphoto;
    }

    public void setaUphoto(String aUphoto) {
        this.aUphoto = aUphoto;
    }
}
