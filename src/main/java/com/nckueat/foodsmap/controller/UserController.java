package com.nckueat.foodsmap.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nckueat.foodsmap.annotation.CurrentUser;
import com.nckueat.foodsmap.annotation.CurrentUserId;
import com.nckueat.foodsmap.annotation.OptionalCurrentUserId;
import com.nckueat.foodsmap.component.nextId.NextIdTokenConverter;
import com.nckueat.foodsmap.model.dto.request.UserUpdate;
import com.nckueat.foodsmap.model.dto.response.ListResponse;
import com.nckueat.foodsmap.model.dto.vo.UserRead;
import com.nckueat.foodsmap.model.dto.vo.ArticleRead;
import com.nckueat.foodsmap.model.dto.vo.GlobalUserView;
import com.nckueat.foodsmap.model.entity.Article;
import com.nckueat.foodsmap.model.entity.User;
import com.nckueat.foodsmap.service.ArticleService;
import com.nckueat.foodsmap.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private NextIdTokenConverter nextIdTokenConverter;

    private ResponseEntity<ListResponse<ArticleRead>> articlesToListResponse(List<Article> results,
            int limit, Long searcherId) {
        String newToken = results.size() < limit ? null
                : nextIdTokenConverter.getNextToken(results.get(results.size() - 1).getId());

        List<Long> likeArticleIds = articleService.getUserLikeArticleIds(searcherId, results);

        return ResponseEntity.ok(new ListResponse<>(
                results.stream().map(Article.toArticleReadFunction(likeArticleIds)).toList(),
                newToken));
    }

    @GetMapping("")
    public ResponseEntity<UserRead> getCurrentUser(@CurrentUser User user) {
        return ResponseEntity.ok(user.toUserRead());
    };

    @PutMapping("")
    public ResponseEntity<UserRead> updateCurrentUser(@CurrentUser User user,
            @NonNull @RequestBody UserUpdate userUpdate) {
        User updatedUser = userService.updateUser(user, userUpdate);

        return ResponseEntity.ok(updatedUser.toUserRead());
    }

    @GetMapping("like-article-ids")
    public ResponseEntity<List<Long>> getCurrentUserLikeArticleIds(@CurrentUserId Long userId) {
        return ResponseEntity.ok(userService.getLikeArticleIdsById(userId));
    }

    @GetMapping("by-id/{userId}")
    public ResponseEntity<GlobalUserView> getUserById(@NonNull @PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user.toGlobalUserView());
    };

    @GetMapping("by-username/{username}")
    public ResponseEntity<GlobalUserView> getUserByUsername(
            @NonNull @PathVariable String username) {
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user.toGlobalUserView());
    };

    @GetMapping("by-display-name/{displayName}")
    public ResponseEntity<ListResponse<UserRead>> getArticlesByDisplayName(
            @NonNull @PathVariable String displayName,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(required = false) String token) {
        List<User> resultPair = userService.getArticlesByDisplayName(displayName, limit, token);

        if (resultPair.isEmpty()) {
            return ResponseEntity.ok(new ListResponse<>(List.of(), null));
        }

        ListResponse<UserRead> response =
                new ListResponse<>(resultPair.stream().map(User::toUserRead).toList(), null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("by-id/{userId}/articles")
    public ResponseEntity<ListResponse<ArticleRead>> getUserArticlesById(@PathVariable Long userId,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(required = false) String token, @OptionalCurrentUserId Long searcherId) {
        List<Article> results = articleService.getArticleListByUserId(userId, limit, token);

        return articlesToListResponse(results, limit, searcherId);
    }

    @GetMapping("by-username/{username}/articles")
    public ResponseEntity<ListResponse<ArticleRead>> getUserArticlesByUsername(
            @PathVariable String username, @RequestParam(defaultValue = "100") int limit,
            @RequestParam(required = false) String token, @OptionalCurrentUserId Long searcherId) {
        List<Article> results = articleService.getArticlesByUsername(username, limit, token);

        return articlesToListResponse(results, limit, searcherId);
    }
}
