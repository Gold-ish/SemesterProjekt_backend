package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.RatingDTO;
import dto.ReviewDTO;
import errorhandling.GenericExceptionMapper;
import errorhandling.MovieNotFoundException;
import errorhandling.MovieNotFoundExceptionMapper;
import errorhandling.NotFoundException;
import errorhandling.TooUnspecificSearchExceptionMapper;
import errorhandling.UserException;
import errorhandling.UserExceptionMapper;
import facades.MovieFacade;
import java.io.IOException;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import utils.EMF_Creator;

/**
 *
 * @author carol
 */
@Path("movies")
public class MovieResource {

    private static EntityManagerFactory EMF
            = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);
    private static final MovieFacade FACADE = MovieFacade.getMovieFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final GenericExceptionMapper GENERIC_EXCEPTION_MAPPER
            = new GenericExceptionMapper();
    private static final TooUnspecificSearchExceptionMapper TOO_UNSPECIFIC_SEARCH
            = new TooUnspecificSearchExceptionMapper();
    private static final MovieNotFoundExceptionMapper MOVIE_EXCEPTION_MAPPER
            = new MovieNotFoundExceptionMapper();
    private static final UserExceptionMapper USER_EXCEPTION_MAPPER
            = new UserExceptionMapper();

    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() {
        return "{\"msg\":\"Hello World\"}";
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") String id) {
        try {
            String movie = GSON.toJson(FACADE.getMovieById(id));
            return Response.ok(movie).build();
        } catch (IOException ex) {
            return GENERIC_EXCEPTION_MAPPER.toResponse(ex);
        } catch (MovieNotFoundException ex) {
            return MOVIE_EXCEPTION_MAPPER.toResponse(ex);
        }
    }

    @GET
    @Path("search/{title}/{page}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByTitle(@PathParam("title") String title,
            @PathParam("page") int page) throws InterruptedException {
        try {
            String movie = GSON.toJson(FACADE.getMoviesByTitle(title, page));
            return Response.ok(movie).build();
        } catch (IOException ex) {
            return GENERIC_EXCEPTION_MAPPER.toResponse(ex);
        } catch (MovieNotFoundException ex) {
            return MOVIE_EXCEPTION_MAPPER.toResponse(ex);
        } catch (IllegalArgumentException ex) {
            return TOO_UNSPECIFIC_SEARCH.toResponse(ex);
        }
    }

    @POST
    @Path("add/rating")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user", "critic"})
    public Response addRating(String json) {
        RatingDTO rating = GSON.fromJson(json, RatingDTO.class);

        //We manually set the user to be the user from the JWT token, 
        //to ensure that a user can't edit the Request to the server with a different username.
        rating.setUserName(securityContext.getUserPrincipal().getName());

        String returnRating = GSON.toJson(FACADE.addRating(rating));
        return Response.ok(returnRating).build();
    }

    @PUT
    @Path("edit/rating")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user", "critic"})
    public Response editRating(String json) {
        try {
            RatingDTO rating = GSON.fromJson(json, RatingDTO.class);
            rating.setUserName(securityContext.getUserPrincipal().getName());
            String returnRating = GSON.toJson(FACADE.editRating(rating));
            return Response.ok(returnRating).build();
        } catch (NotFoundException ex) {
            return MOVIE_EXCEPTION_MAPPER.toResponse(new MovieNotFoundException(ex.getMessage()));
        }
    }

    @DELETE
    @Path("delete/rating")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user", "critic", "admin"})
    public Response deleteRating(String json) {
        try {
            RatingDTO rating = GSON.fromJson(json, RatingDTO.class);
            if (securityContext.isUserInRole("admin")) {
                rating.setUserName("adminRole");
            } else {
                rating.setUserName(securityContext.getUserPrincipal().getName());
            }
            String deletedRating = GSON.toJson(FACADE.deleteRating(rating));
            return Response.ok(deletedRating).build();
        } catch (NotFoundException ex) {
            return GENERIC_EXCEPTION_MAPPER.toResponse(ex);
        } catch (UserException ex) {
            return USER_EXCEPTION_MAPPER.toResponse(ex);
        }
    }

    @POST
    @Path("add/review")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user", "critic"})
    public Response addReview(String json) {
        ReviewDTO review = GSON.fromJson(json, ReviewDTO.class);
        review.setUser(securityContext.getUserPrincipal().getName());
        String returnReview = GSON.toJson(FACADE.addReview(review));
        return Response.ok(returnReview).build();
    }

    @PUT
    @Path("edit/review")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user", "critic"})
    public Response editReview(String json) {
        try {
            ReviewDTO review = GSON.fromJson(json, ReviewDTO.class);
            review.setUser(securityContext.getUserPrincipal().getName());
            String returnReview = GSON.toJson(FACADE.editReview(review));
            return Response.ok(returnReview).build();
        } catch (NotFoundException ex) {
            return GENERIC_EXCEPTION_MAPPER.toResponse(ex);
        }
    }

    @DELETE
    @Path("delete/review")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user", "critic", "admin"})
    public Response deleteReview(String json) {
        try {
            ReviewDTO review = GSON.fromJson(json, ReviewDTO.class);
            if (securityContext.isUserInRole("admin")) {
                review.setUser("adminRole");
            } else {
                review.setUser(securityContext.getUserPrincipal().getName());
            }
            String deletedReview = GSON.toJson(FACADE.deleteReview(review));
            return Response.ok(deletedReview).build();
        } catch (NotFoundException ex) {
            return GENERIC_EXCEPTION_MAPPER.toResponse(ex);
        } catch (UserException ex) {
            return USER_EXCEPTION_MAPPER.toResponse(ex);
        }
    }

    @GET
    @Path("topten")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopTenMovies() throws InterruptedException {
        String movie = GSON.toJson(FACADE.getTopTenMovies());
        return Response.ok(movie).build();
    }
}
