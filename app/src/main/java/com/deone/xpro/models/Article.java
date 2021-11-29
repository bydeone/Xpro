package com.deone.xpro.models;

public class Article {
    private String aId;
    private String aDate;
    private String aCategorie;
    private String aTitre;
    private String aDescription;
    private String aUid;

    public Article() {
    }

    public Article(String aId, String aDate, String aCategorie, String aTitre, String aDescription, String aUid) {
        this.aId = aId;
        this.aDate = aDate;
        this.aCategorie = aCategorie;
        this.aTitre = aTitre;
        this.aDescription = aDescription;
        this.aUid = aUid;
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

    public String getaUid() {
        return aUid;
    }

    public void setaUid(String aUid) {
        this.aUid = aUid;
    }
}