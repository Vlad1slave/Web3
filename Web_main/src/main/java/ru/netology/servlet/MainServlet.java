package ru.netology.servlet;

import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
    private static final String API_POSTS = "/api/posts";
    private static final String API_POSTS_DIGIT = "/api/posts/\\d+";
    private PostController controller;

    @Override
    public void init() {
        final var repository = new PostRepository();
        final var service = new PostService(repository);
        controller = new PostController(service);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();

            switch (method) {
                case "GET":
                    if (path.equals(API_POSTS)) {
                        controller.all(resp);
                        return;
                    }
                    if (path.matches(API_POSTS_DIGIT)) {
                        final var id = extractId(path);
                        controller.getById(id, resp);
                        return;
                    }
                    break;
                case "POST":
                    if (path.equals(API_POSTS)) {
                        controller.save(req.getReader(), resp);
                        return;
                    }
                    break;
                case "DELETE":
                    if (path.matches(API_POSTS_DIGIT)) {
                        final var id = extractId(path);
                        controller.removeById(id, resp);
                        return;
                    }
                    break;
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private long extractId(String path) {
        return Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
    }
}