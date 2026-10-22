package org.sopt.post.dto.response;

import java.util.List;

public record PostPageResponse(
        List<PostResponse> posts,
        int page,
        int size,
        long totalElements,
        long totalPages,
        boolean hasNext
) {
}
