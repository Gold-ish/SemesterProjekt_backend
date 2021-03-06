package facades;

import dto.MovieDTO;
import dto.MovieListDTO;
import dto.RatingDTO;
import dto.ReviewDTO;
import dto.SpecificMovieDTO;
import dto.TopTenMoviesDTO;
import errorhandling.MovieNotFoundException;
import errorhandling.NotFoundException;
import errorhandling.UserException;
import java.io.IOException;
import java.util.List;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author carol
 */
public class MovieFacade {

    private static MovieFacade instance;
    private static EntityManagerFactory emf;
    private final FetchFacade fetchFacade = FetchFacade.getFetchFacade();
    private final RatingFacade ratingFacade = RatingFacade.getRatingFacade(emf);
    private final ReviewFacade reviewFacade = ReviewFacade.getReviewFacade(emf);

    //Private Constructor to ensure Singleton
    private MovieFacade() {
    }

    public static MovieFacade getMovieFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new MovieFacade();
        }
        return instance;
    }

    public SpecificMovieDTO getMovieById(String id) throws IOException, MovieNotFoundException {
        SpecificMovieDTO mdto = fetchFacade.getMovieByIdSpecific(id);
        mdto.setAvgRating(ratingFacade.getRatingAvgUser(mdto.getImdbID()));
        mdto.setAvgRatingCritic(ratingFacade.getRatingAvgCritic(mdto.getImdbID()));
        mdto.setReviews(reviewFacade.getReviews(mdto.getImdbID()));
        mdto.setRatings(ratingFacade.getRatingsWithMovieID(mdto.getImdbID()));
        return mdto;
    }

    public MovieListDTO getMoviesByTitle(String searchString, int page) throws IOException,
            MovieNotFoundException,
            InterruptedException {
        MovieListDTO mdtoList = fetchFacade.getMoviesByTitle(searchString, page);
        mdtoList.getMovieDTOs().forEach((movie) -> {
            movie.setAvgRating(ratingFacade.getRatingAvg(movie.getImdbID()));
        });
        return mdtoList;
    }

    public RatingDTO addRating(RatingDTO ratingDTO) {
        return ratingFacade.addRating(ratingDTO);
    }

    public RatingDTO editRating(RatingDTO ratingDTO) throws NotFoundException {
        return ratingFacade.editRating(ratingDTO);
    }

    public RatingDTO deleteRating(RatingDTO ratingDTO) throws NotFoundException, UserException {
        return ratingFacade.deleteRating(ratingDTO);
    }

    public ReviewDTO addReview(ReviewDTO reviewDTO) {
        return reviewFacade.addReview(reviewDTO);
    }

    public ReviewDTO editReview(ReviewDTO reviewDTO) throws NotFoundException {
        return reviewFacade.editReview(reviewDTO);
    }

    public ReviewDTO deleteReview(ReviewDTO review) throws NotFoundException, UserException {
        return reviewFacade.deleteReview(review);
    }

    public TopTenMoviesDTO getTopTenMovies() throws InterruptedException {
        List<MovieDTO> mdtoList = fetchFacade.fetchParallel(ratingFacade.getTopTenMovies());
        mdtoList.forEach((movie) -> {
            movie.setAvgRating(ratingFacade.getRatingAvg(movie.getImdbID()));
        });
        return new TopTenMoviesDTO(mdtoList);
    }
}
