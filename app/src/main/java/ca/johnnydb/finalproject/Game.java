package ca.johnnydb.finalproject;

import java.io.Serializable;

///Game is the class which defines a game
public class Game implements Serializable
{
    String title;
    String platform;
    String image;
    String developer;
    String description;
    String releaseDate;

    public Game(String title, String platform, String image)
    {
        this.title = title;
        this.platform = platform;
        this.image = image;
    }

    public Game()
    {

    }

    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title) { this.title = title; }

    public String getPlatform()
    {
        return platform;
    }
    public void setPlatform(String platform)
    {
        this.platform = platform;
    }

    public String getDeveloper()
    {
        return developer;
    }
    public void setDeveloper(String developer)
    {
        this.description = developer;
    }

    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getReleaseDate()
    {
        return releaseDate;
    }
    public void setReleaseDate(String releaseDate)
    {
        this.releaseDate = releaseDate;
    }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

}
