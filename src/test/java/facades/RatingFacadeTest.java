package facades;

import entities.Rating;
import errorhandling.NotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

/**
 *
 * @author rando
 */
public class RatingFacadeTest {

    private static EntityManagerFactory EMF;
    private static RatingFacade FACADE;
    private static Rating r1, r2, r3, r4, r5, r6;

    @BeforeAll
    public static void setUpClass() {
        EMF = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.TEST,
                EMF_Creator.Strategy.DROP_AND_CREATE);
        FACADE = RatingFacade.getRatingFacade(EMF);
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = EMF.createEntityManager();
        r1 = new Rating("MovieID1", 8);
        r2 = new Rating("MovieID2", 8);
        r3 = new Rating("MovieID3", 8);
        r4 = new Rating("MovieID1", 3);
        r5 = new Rating("MovieID2", 7);
        r6 = new Rating("MovieID3", 5);
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Rating.deleteAllRows").executeUpdate();
            em.persist(r1);
            em.persist(r2);
            em.persist(r3);
            em.persist(r4);
            em.persist(r5);
            em.persist(r6);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void testGetRatingAvg_ReturnsTheAvgRating_EqualResults() {
        System.out.println("testGetRatingAvg_ReturnsTheAvgRating_EqualResults");
        String movieID = "MovieID1";
        double avgRating = FACADE.getRatingAvg(movieID);
        double expectedRating = ((double) r1.getRating() + (double) r4.getRating()) / 2;
        assertEquals(expectedRating, avgRating);
    }

    @Test
    public void testGetRatingAvg_ReturnsTheAvgRatingOfNonExistingRating_EqualResults() throws Exception {
        System.out.println("testGetRatingAvg_ReturnsTheAvgRatingOfNonExistingRating_EqualResults");
        String movieID = "NonExistingID";
        double avgRating = FACADE.getRatingAvg(movieID);
        double expectedRating = -1.0;
        assertEquals(expectedRating, avgRating);
    }

    @Test
    public void testAddRating_ReturnsTheRating_EqualResults() {
        System.out.println("testAddRating_ReturnsTheRating_EqualResults");
        Rating dummyRating = new Rating("DummyRating", 5);
        double addRatingReturn = FACADE.addRating(dummyRating.getMovieID(), dummyRating.getRating());
        double expectedRatingReturned = dummyRating.getRating();
        assertEquals(expectedRatingReturned, addRatingReturn);
    }

    //@Test (Nothing we can test yet. After user implementation then mby)
    public void testAddRating_CantThinkOfNegativeTest_ThrowSomeException() throws Exception {
        System.out.println("test-negative");
    }

    @Test
    public void testEditRating_ReturnsTheNewRating_EqualResults() throws NotFoundException {
        System.out.println("testEditRating_ReturnsTheNewRating_EqualResults");
        String editReviewReturn = FACADE.editRating(r1.getId(), r1.getMovieID(), 10);
        assertEquals("Review for movie: " + r1.getMovieID() + " has been updated to rating: " + 10, editReviewReturn);
    }

    @Test
    public void testDeleteReview_ReturnsTheDeletedRating_EqualResults() throws NotFoundException {
        System.out.println("testDeleteReview_ReturnsTheDeletedRating_EqualResults");
        String deleteReviewReturn = FACADE.deleteRating(r1.getId());
        assertEquals("Rating " + r1.getId() + " deleted", deleteReviewReturn);
    }
}
