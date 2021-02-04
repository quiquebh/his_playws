package controllers;

import models.Review;
import models.ReviewRepository;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

/**
 * The controller keeps all database operations behind the repository, and uses
 * {@link play.libs.concurrent.HttpExecutionContext} to provide access to the
 * {@link play.mvc.Http.Context} methods like {@code request()} and {@code flash()}.
 */
public class ReviewController extends Controller {

    private final FormFactory formFactory;
    private final ReviewRepository reviewRepository;
    private final HttpExecutionContext ec;

    @Inject
    public ReviewController(FormFactory formFactory, ReviewRepository reviewRepository, HttpExecutionContext ec) {
        this.formFactory = formFactory;
        this.reviewRepository = reviewRepository;
        this.ec = ec;
    }

    public Result index() {
        return ok(views.html.review.render());
    }

    public CompletionStage<Result> getReview(Long id) {
        return reviewRepository.get(id).thenApplyAsync(reviewStream -> {
            return ok(views.html.singlereview.render(reviewStream.collect(Collectors.toList()).get(0)));
        }, ec.current());
    }

    public CompletionStage<Result> addReview() {
        Review review = formFactory.form(Review.class).bindFromRequest().get();
        return reviewRepository.add(review).thenApplyAsync(p -> {
            return redirect(routes.ReviewController.index());
        }, ec.current());
    }

    public CompletionStage<Result> getReviewsJson() {
        return reviewRepository.list().thenApplyAsync(reviewStream -> {
            return ok(toJson(reviewStream.collect(Collectors.toList())));
        }, ec.current());
    }

    public CompletionStage<Result> getReviews() {
        return reviewRepository.list().thenApplyAsync(reviewStream -> {
            return ok(views.html.listreviews.render(reviewStream.collect(Collectors.toList())));
        }, ec.current());
    }

    public CompletionStage<Result> searchReviews(String publication) {
        return reviewRepository.searchByPublication(publication).thenApplyAsync(reviewStream -> {
            return ok(views.html.listreviews.render(reviewStream.collect(Collectors.toList())));
        }, ec.current());
    }
}
