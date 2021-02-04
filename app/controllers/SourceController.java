package controllers;

import models.Source;
import models.SourceRepository;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

/**
 * The controller keeps all database operations behind the repository, and uses
 * {@link play.libs.concurrent.HttpExecutionContext} to provide access to the
 * {@link play.mvc.Http.Context} methods like {@code request()} and {@code flash()}.
 */
public class SourceController extends Controller {

    private final FormFactory formFactory;
    private final SourceRepository sourceRepository;
    private final HttpExecutionContext ec;

    @Inject
    public SourceController(FormFactory formFactory, SourceRepository sourceRepository, HttpExecutionContext ec) {
        this.formFactory = formFactory;
        this.sourceRepository = sourceRepository;
        this.ec = ec;
    }

    public Result index() {
        return ok(views.html.source.render());
    }

    public CompletionStage<Result> getSource(Long id) {
        return sourceRepository.get(id).thenApplyAsync(sourceStream -> {
            return ok(views.html.singlesource.render(sourceStream.collect(Collectors.toList()).get(0)));
        }, ec.current());
    }

    public CompletionStage<Result> addSource() {
        Source source = formFactory.form(Source.class).bindFromRequest().get();
        return sourceRepository.add(source).thenApplyAsync(p -> {
            return redirect(routes.SourceController.getSources());
        }, ec.current());
    }

    public CompletionStage<Result> getSourcesJson() {
        return sourceRepository.list().thenApplyAsync(sourceStream -> {
            return ok(toJson(sourceStream.collect(Collectors.toList())));
        }, ec.current());
    }

    public CompletionStage<Result> getSources() {
        return sourceRepository.list().thenApplyAsync(sourceStream -> {
            return ok(views.html.listsources.render(sourceStream.collect(Collectors.toList())));
        }, ec.current());
    }

    public CompletionStage<Result> searchSources(String name) {
        return sourceRepository.searchByName(name).thenApplyAsync(sourceStream -> {
            return ok(views.html.listsources.render(sourceStream.collect(Collectors.toList())));
        }, ec.current());
    }
}
