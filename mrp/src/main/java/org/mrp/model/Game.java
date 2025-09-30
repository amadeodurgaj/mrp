package org.mrp.model;

public class Game extends Media {
    @Override
    public void setReleaseYear(int year) {
        throw new UnsupportedOperationException("Games don’t use release year!");
    }
}