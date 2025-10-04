package org.mrp.model;

import java.time.LocalDateTime;
import java.util.List;

public abstract class Media {
    protected int id;
    protected String title;
    protected String description;
    protected int releaseYear;
    protected int ageRestriction;
    protected User creator;
    protected LocalDateTime createdAt;
    protected List<String> genres;
    protected List<Rating> ratings;

    public abstract String getType(); // avoids type misuse, respects LSP
}