package entities;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author rando
 */
@Entity
@NamedQueries(
    @NamedQuery(name = "Rating.deleteAllRows", query = "DELETE FROM Rating"))
public class Rating implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String movieID;
    private String user;
    private int rating;

    public Rating(String movieID, String user, int rating) {
        this.movieID = movieID;
        this.user = user;
        this.rating = rating;
    }
    
    public Rating(String movieID, int rating) {
        this.movieID = movieID;
        this.rating = rating;
    }

    public Rating() {
    }

    public String getMovieID() {
        return movieID;
    }

    public int getRating() {
        return rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + this.id;
        hash = 97 * hash + Objects.hashCode(this.movieID);
        hash = 97 * hash + Objects.hashCode(this.user);
        hash = 97 * hash + this.rating;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Rating other = (Rating) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.rating != other.rating) {
            return false;
        }
        if (!Objects.equals(this.movieID, other.movieID)) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Rating{" + "id=" + id + ", movieID=" + movieID + ", userName=" + user + ", rating=" + rating + '}';
    }
    
    
}
