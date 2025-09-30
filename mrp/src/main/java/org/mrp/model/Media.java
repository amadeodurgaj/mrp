package org.mrp.model;

abstract class Media {
    protected String title;
    protected Integer releaseYear;

    public abstract String getType();

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer year) {
        this.releaseYear = year;
    }
}