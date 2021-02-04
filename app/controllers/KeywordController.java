package controllers;

import models.Keyword;
import models.KeywordRepository;
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
public class KeywordController extends Controller {

    private final FormFactory formFactory;
    private final KeywordRepository keywordRepository;
    private final HttpExecutionContext ec;

    @Inject
    public KeywordController(FormFactory formFactory, KeywordRepository keywordRepository, HttpExecutionContext ec) {
        this.formFactory = formFactory;
        this.keywordRepository = keywordRepository;
        this.ec = ec;
    }

    public Result index() {
        return ok(views.html.keyword.render());
    }

    public CompletionStage<Result> getKeyword(Long id) {
        return keywordRepository.get(id).thenApplyAsync(keywordStream -> {
            return ok(views.html.singlekeyword.render(keywordStream.collect(Collectors.toList()).get(0)));
        }, ec.current());
    }

    public CompletionStage<Result> addKeyword() {
        Keyword keyword = formFactory.form(Keyword.class).bindFromRequest().get();
        return keywordRepository.add(keyword).thenApplyAsync(p -> {
            return redirect(routes.KeywordController.getKeywords());
        }, ec.current());
    }

    public CompletionStage<Result> getKeywordsJson() {
        return keywordRepository.list().thenApplyAsync(keywordStream -> {
            return ok(toJson(keywordStream.collect(Collectors.toList())));
        }, ec.current());
    }

    public CompletionStage<Result> getKeywords() {
        return keywordRepository.list().thenApplyAsync(keywordStream -> {
            return ok(views.html.listkeywords.render(keywordStream.collect(Collectors.toList())));
        }, ec.current());
    }

    public CompletionStage<Result> searchKeywords(String name) {
        return keywordRepository.searchByName(name).thenApplyAsync(keywordStream -> {
            return ok(views.html.listkeywords.render(keywordStream.collect(Collectors.toList())));
        }, ec.current());
    }
}
