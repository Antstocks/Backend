package com.antstocks.project.projection;

import org.springframework.beans.factory.annotation.Value;

public interface ArticleProjection {

    // 긴급뉴스 Projection
    interface BreakingNewsProjection {
        String getTitle();

        @Value("#{target.origin_link}")
        String getOrigin_link();
    }
}
