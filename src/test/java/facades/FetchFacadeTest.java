package facades;

import dto.MovieListDTO;
import dto.SpecificMovieDTO;
import errorhandling.MovieNotFoundException;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author allan
 */
public class FetchFacadeTest {

    private static final FetchFacade FACADE = FetchFacade.getFetchFacade();

    public FetchFacadeTest() {
    }

    /**
     * Test of getMovieById method, of class FetchFacade.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetMovieById_ReturnsMovie_EqualResults() throws Exception {
        System.out.println("testGetMovieById_ReturnsMovie_EqualResults");
        SpecificMovieDTO expResult = new SpecificMovieDTO(
                "Star Wars: Episode IV - A New Hope",
                "1977",
                "PG",
                "25 May 1977",
                "121 min",
                "Action, Adventure, Fantasy, Sci-Fi",
                "George Lucas",
                "Mark Hamill, Harrison Ford, Carrie Fisher, Peter Cushing",
                "Luke Skywalker joins forces with a Jedi Knight, a cocky pilot, "
                        + "a Wookiee and two droids to save the galaxy from the "
                        + "Empire's world-destroying battle station, while also "
                        + "attempting to rescue Princess Leia from the mysterious Darth Vader.",
                "English",
                "Won 6 Oscars. Another 52 wins & 28 nominations.",
                "https://m.media-amazon.com/images/M/MV5BNzVlY2MwMjktM2E4OS00Y2Y3LWE3ZjctYzhkZGM3YzA1ZWM2XkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_SX300.jpg",
                "tt0076759",
                "movie",
                "21 Sep 2004",
                "20th Century Fox");
        SpecificMovieDTO result = FACADE.getMovieById("tt0076759");
        assertEquals(expResult, result);
    }

    @Test
    public void testGetMovieByID_NonExistentMovieID_ThrowMovieNotFoundException() {
        System.out.println("testGetMovieByID_NonExistentMovieID_ThrowMovieNotFoundException");
        Exception exception = assertThrows(MovieNotFoundException.class, () -> {
            FACADE.getMovieById("tt00767555555559");
        });

        String expectedMessage = "No movie found with id: tt00767555555559";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testGetMoviesByTitle_ReturnsListOf10Movies_EqualResults() throws IOException, MovieNotFoundException {
        System.out.println("testGetMoviesByTitle_ReturnsListOf10Movies_EqualResults");
        String searchString = "star";
        int pageNumber = 1;
        MovieListDTO actualDto = FACADE.getMoviesByTitle(searchString, pageNumber);
        assertEquals(10, actualDto.getMovieDTOs().size());
        assertEquals(2936, actualDto.getTotalResults());
    }

    @Test
    public void testGetMoviesByTitle_SearchDoesNotExist_ThrowMovieNotFoundException() {
        System.out.println("testGetMoviesByTitle_SearchDoesNotExist_ThrowMovieNotFoundException");
        String searchString = "aejclkejelo";
        int pageNumber = 1;

        Exception exception = assertThrows(MovieNotFoundException.class, () -> {
            FACADE.getMoviesByTitle(searchString, pageNumber);
        });
        String expectedMessage = "No movie found with the search result: aejclkejelo";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testGetMoviesByTitle_SearchTooUnspecific_ThrowIllegalArgumentException() {
        System.out.println("testGetMoviesByTitle_SearchTooUnspecific_ThrowIllegalArgumentException");
        String searchString = "a";
        int pageNumber = 1;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            FACADE.getMoviesByTitle(searchString, pageNumber);
        });
        String expectedMessage = "Too many search results, not specific enough search: a";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
