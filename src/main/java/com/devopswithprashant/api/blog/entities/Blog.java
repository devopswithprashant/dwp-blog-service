package com.devopswithprashant.api.blog.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="book_id")
    private int id;

    private String title;
    private String body;
    private String author;
    private String publishdate;
    private String lastupdatedate;
    
    public Blog(int id, String title, String body, String author, String publishdate, String lastupdatedate) {
        super();
        this.id = id;
        this.title = title;
        this.body = body;
        this.author = author;
        this.publishdate = publishdate;
        this.lastupdatedate = lastupdatedate;
    }

    public Blog() {
        super();
    }

    @Override
    public String toString() {
        return "Blog [id=" + id + ", title=" + title + ", body=" + body + ", author=" + author + ", publishdate="
                + publishdate + ", lastupdatedate=" + lastupdatedate + "]";
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getPublishdate() {
        return publishdate;
    }
    public void setPublishdate(String publishdate) {
        this.publishdate = publishdate;
    }
    public String getLastupdatedate() {
        return lastupdatedate;
    }
    public void setLastupdatedate(String lastupdatedate) {
        this.lastupdatedate = lastupdatedate;
    }
    



}
