package com.antstocks.project.projection;

public interface ArticleProjection {

    // 긴급뉴스 Projection
    interface BreakingNewsProjection {
        String getTitle();
        String getOriginLink();
    }
}
